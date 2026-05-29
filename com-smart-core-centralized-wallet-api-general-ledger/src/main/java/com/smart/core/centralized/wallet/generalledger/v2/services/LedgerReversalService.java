package com.smart.core.centralized.wallet.generalledger.v2.services;

import com.smart.core.centralized.wallet.generalledger.utils.DecodedJWTToken;
import com.smart.core.centralized.wallet.generalledger.v2.domains.LedgerEntryV2;
import com.smart.core.centralized.wallet.generalledger.v2.enumm.LegTypeV2;
import com.smart.core.centralized.wallet.generalledger.v2.enumm.PostingModeV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.BatchLedgerPostRequestV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.BatchLedgerPostResponseV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.BatchLegV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.LedgerReversalRequest;
import com.smart.core.centralized.wallet.generalledger.v2.models.LedgerReversalResponse;
import com.smart.core.centralized.wallet.generalledger.v2.repository.LedgerEntryV2Repo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

@Service
public class LedgerReversalService {

    private static final String MANUAL_BACKOFFICE = "MANUAL_BACKOFFICE";
    private static final String SYSTEM_COMPENSATION = "SYSTEM_COMPENSATION";

    private final LedgerEntryV2Repo entryRepo;
    private final LedgerPostingV2Service postingService;
    private final LedgerApprovalVerificationService approvalVerificationService;
    private final LedgerSecurityAuditV2Service securityAuditService;

    public LedgerReversalService(LedgerEntryV2Repo entryRepo,
                                 LedgerPostingV2Service postingService,
                                 LedgerApprovalVerificationService approvalVerificationService,
                                 LedgerSecurityAuditV2Service securityAuditService) {
        this.entryRepo = entryRepo;
        this.postingService = postingService;
        this.approvalVerificationService = approvalVerificationService;
        this.securityAuditService = securityAuditService;
    }

    @Transactional
    public LedgerReversalResponse reverse(LedgerReversalRequest request, String auth) {
        LedgerReversalResponse response = new LedgerReversalResponse();

        try {
            if (request == null) {
                return fail(response, 400, "Request body is required");
            }
            if (isBlank(request.getProductCode()) || isBlank(request.getReversalRequestRef())) {
                return fail(response, 400, "productCode and reversalRequestRef are required");
            }
            if (isBlank(request.getOriginalRequestRef()) && isBlank(request.getOriginalTransactionId())) {
                return fail(response, 400, "originalRequestRef or originalTransactionId is required");
            }

            DecodedJWTToken decoded = DecodedJWTToken.getDecoded(auth);
            if (!request.getProductCode().equals(decoded.productCode)) {
                return fail(response, 400, "Invalid product code!");
            }

            String reversalType = normalizeReversalType(request.getReversalType());
            LedgerReversalResponse policyFailure = validateReversalPolicy(response, request, reversalType);
            if (policyFailure != null) {
                return policyFailure;
            }

            Optional<LedgerEntryV2> originalOpt = isBlank(request.getOriginalRequestRef())
                    ? entryRepo.findFirstByProductCodeAndTransactionIdOrderByCreatedAtDesc(request.getProductCode(), request.getOriginalTransactionId())
                    : entryRepo.findFirstByProductCodeAndRequestRefOrderByCreatedAtDesc(request.getProductCode(), request.getOriginalRequestRef());

            if (!originalOpt.isPresent()) {
                return fail(response, 404, "Original ledger entry not found");
            }

            LedgerEntryV2 original = originalOpt.get();
            response.setOriginalEntryId(original.getId());
            response.setReversalRequestRef(request.getReversalRequestRef());

            if (!isBlank(original.getReversalOfEntryId())) {
                return fail(response, 400, "Cannot reverse a reversal entry");
            }
            if (entryRepo.existsByProductCodeAndReversalOfEntryId(request.getProductCode(), original.getId())) {
                return fail(response, 409, "Original ledger entry has already been reversed");
            }

            if (MANUAL_BACKOFFICE.equals(reversalType)) {
                try {
                    approvalVerificationService.requireApproved(request.getApprovalRef(), request.getProductCode(), "LEDGER_REVERSAL");
                } catch (IllegalArgumentException ex) {
                    recordReversalSecurityEvent(request, "MANUAL_REVERSAL_APPROVAL_FAILED", "HIGH", ex.getMessage());
                    return fail(response, 403, ex.getMessage());
                }
            }

            BatchLegV2 reversalLeg = buildReversalLeg(original, request, decoded.productName);

            BatchLedgerPostRequestV2 post = new BatchLedgerPostRequestV2();
            post.setBatchRef(request.getReversalRequestRef());
            post.setProductCode(request.getProductCode());
            post.setProductName(decoded.productName);
            post.setNarration(firstNonBlank(request.getNarration(), "Reversal for " + original.getRequestRef()));
            post.setPostingMode(PostingModeV2.ONE_SIDED);
            post.setLegs(Collections.singletonList(reversalLeg));

            BatchLedgerPostResponseV2 posted = postingService.batchPost(post, decoded.productCode);
            response.setStatusCode(posted.getStatusCode());
            response.setDescription(posted.getDescription());
            response.setData(posted.getData());
            return response;
        } catch (Exception ex) {
            return fail(response, 500, "An error occurred, please try again");
        }
    }

    private LedgerReversalResponse validateReversalPolicy(LedgerReversalResponse response,
                                                          LedgerReversalRequest request,
                                                          String reversalType) {
        if (SYSTEM_COMPENSATION.equals(reversalType)) {
            if (isBlank(request.getFulfilmentStatus()) || !"FAILED".equalsIgnoreCase(request.getFulfilmentStatus().trim())) {
                recordReversalSecurityEvent(request, "SYSTEM_COMPENSATION_EVIDENCE_MISSING", "HIGH",
                        "System compensation reversal requires fulfilmentStatus=FAILED.");
                return fail(response, 400, "System compensation reversal requires fulfilmentStatus=FAILED.");
            }
            if (isBlank(request.getFulfilmentReference()) && isBlank(request.getFulfilmentResponseCode())
                    && isBlank(request.getFulfilmentEvidence())) {
                recordReversalSecurityEvent(request, "SYSTEM_COMPENSATION_EVIDENCE_MISSING", "HIGH",
                        "System compensation reversal requires downstream fulfilment evidence.");
                return fail(response, 400, "System compensation reversal requires downstream fulfilment evidence.");
            }
            if (isBlank(request.getReason())) {
                return fail(response, 400, "System compensation reversal requires reason.");
            }
            return null;
        }

        if (!MANUAL_BACKOFFICE.equals(reversalType)) {
            recordReversalSecurityEvent(request, "UNKNOWN_REVERSAL_TYPE", "MEDIUM",
                    "Unknown reversal type supplied: " + reversalType);
            return fail(response, 400, "Unsupported reversalType.");
        }
        return null;
    }

    private BatchLegV2 buildReversalLeg(LedgerEntryV2 original, LedgerReversalRequest request, String productName) {
        BatchLegV2 leg = new BatchLegV2();
        leg.setLegRef("REV-" + original.getId());
        leg.setRequestRef(request.getReversalRequestRef());
        leg.setAccountNumber(original.getAccountNumber());
        leg.setProductCode(original.getProductCode());
        leg.setProductName(productName);
        leg.setNarration(firstNonBlank(request.getNarration(), "Reversal for " + original.getRequestRef()));
        leg.setDescription("Reversal of entry " + original.getId());
        leg.setReversalOfEntryId(original.getId());
        leg.setReversalReason(request.getReason());

        if ("DEBIT".equalsIgnoreCase(original.getLegType())) {
            BigDecimal amount = nz(original.getFinalCharges());
            leg.setLegType(LegTypeV2.CREDIT);
            leg.setTransType("Deposit");
            leg.setAmount(amount);
            leg.setFees(BigDecimal.ZERO);
            leg.setFinalCharges(amount);
        } else {
            BigDecimal amount = nz(original.getAmount()).subtract(nz(original.getFees()));
            leg.setLegType(LegTypeV2.DEBIT);
            leg.setTransType("Withdrawal");
            leg.setAmount(amount);
            leg.setFees(BigDecimal.ZERO);
            leg.setFinalCharges(amount);
        }

        return leg;
    }

    private LedgerReversalResponse fail(LedgerReversalResponse response, int code, String description) {
        response.setStatusCode(code);
        response.setDescription(description);
        return response;
    }

    private BigDecimal nz(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String firstNonBlank(String first, String second) {
        return isBlank(first) ? second : first;
    }

    private String normalizeReversalType(String reversalType) {
        if (isBlank(reversalType)) {
            return MANUAL_BACKOFFICE;
        }
        return reversalType.trim().toUpperCase();
    }

    private void recordReversalSecurityEvent(LedgerReversalRequest request, String eventType, String severity, String reason) {
        if (request == null) {
            return;
        }
        securityAuditService.record(
                request.getProductCode(),
                eventType,
                severity,
                request.getReversalRequestRef(),
                null,
                reason,
                reversalAuditPayload(request));
    }

    private String reversalAuditPayload(LedgerReversalRequest request) {
        return "originalRequestRef=" + firstNonBlank(request.getOriginalRequestRef(), "")
                + ", originalTransactionId=" + firstNonBlank(request.getOriginalTransactionId(), "")
                + ", reversalType=" + firstNonBlank(request.getReversalType(), MANUAL_BACKOFFICE)
                + ", approvalRef=" + firstNonBlank(request.getApprovalRef(), "")
                + ", fulfilmentStatus=" + firstNonBlank(request.getFulfilmentStatus(), "")
                + ", fulfilmentReference=" + firstNonBlank(request.getFulfilmentReference(), "")
                + ", fulfilmentResponseCode=" + firstNonBlank(request.getFulfilmentResponseCode(), "");
    }
}
