package com.smart.core.centralized.wallet.profilings.services;

import com.smart.core.centralized.wallet.profilings.domains.BackofficeApprovalRequest;
import com.smart.core.centralized.wallet.profilings.domains.BackofficeUser;
import com.smart.core.centralized.wallet.profilings.model.BackofficeApprovalDecisionRequest;
import com.smart.core.centralized.wallet.profilings.model.BackofficeApprovalSubmitRequest;
import com.smart.core.centralized.wallet.profilings.model.BaseResponse;
import com.smart.core.centralized.wallet.profilings.repo.BackofficeApprovalRequestRepo;
import com.smart.core.centralized.wallet.profilings.repo.BackofficeUserRepo;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class BackofficeApprovalService {

    private static final String PENDING = "PENDING";
    private static final String APPROVED = "APPROVED";
    private static final String REJECTED = "REJECTED";
    private static final String CONSUMED = "CONSUMED";

    private final BackofficeApprovalRequestRepo approvalRepo;
    private final BackofficeUserRepo userRepo;

    @Value("${smartcore.backoffice.admin.api-key:}")
    private String adminApiKey;

    @Value("${smartcore.backoffice.approval.expiry-hours:24}")
    private long expiryHours;

    public BackofficeApprovalService(BackofficeApprovalRequestRepo approvalRepo, BackofficeUserRepo userRepo) {
        this.approvalRepo = approvalRepo;
        this.userRepo = userRepo;
    }

    public BaseResponse submit(String adminKey, String actor, BackofficeApprovalSubmitRequest request) {
        BaseResponse auth = authorize(adminKey);
        if (auth != null) {
            return auth;
        }
        BackofficeUser requester = requireActiveOperator(actor);
        if (requester == null) {
            return fail(401, "Unknown or inactive requester.");
        }
        if (!normalize(request.getProductCode()).equals(requester.getProductCode())) {
            return fail(400, "Requester product code does not match approval product code.");
        }

        BackofficeApprovalRequest approval = new BackofficeApprovalRequest();
        approval.setApprovalRef(newApprovalRef());
        approval.setProductCode(normalize(request.getProductCode()));
        approval.setOperationType(normalize(request.getOperationType()));
        approval.setStatus(PENDING);
        approval.setRequestPayload(request.getRequestPayload().trim());
        approval.setRequestedBy(requester.getOperatorId());
        approval.setRequestedAt(Instant.now());
        approval.setExpiresAt(Instant.now().plus(Math.max(1, expiryHours), ChronoUnit.HOURS));
        approvalRepo.save(approval);

        return ok(approval, "Approval request submitted.");
    }

    public BaseResponse list(String adminKey, String productCode, String status) {
        BaseResponse auth = authorize(adminKey);
        if (auth != null) {
            return auth;
        }
        String resolvedStatus = StringUtils.hasText(status) ? normalize(status) : PENDING;
        List<BackofficeApprovalRequest> approvals;
        if (StringUtils.hasText(productCode)) {
            approvals = approvalRepo.findByProductCodeAndStatusOrderByRequestedAtDesc(normalize(productCode), resolvedStatus);
        } else {
            approvals = approvalRepo.findByStatusOrderByRequestedAtDesc(resolvedStatus);
        }
        return ok(approvals, "Approval requests fetched.");
    }

    @Transactional
    public BaseResponse approve(String adminKey, String actor, String approvalRef, BackofficeApprovalDecisionRequest request) {
        return decide(adminKey, actor, approvalRef, request, APPROVED);
    }

    @Transactional
    public BaseResponse reject(String adminKey, String actor, String approvalRef, BackofficeApprovalDecisionRequest request) {
        return decide(adminKey, actor, approvalRef, request, REJECTED);
    }

    public BaseResponse verify(String adminKey, String approvalRef, String productCode, String operationType) {
        BaseResponse auth = authorize(adminKey);
        if (auth != null) {
            return auth;
        }
        if (!StringUtils.hasText(approvalRef)) {
            return fail(400, "approvalRef is required.");
        }

        Optional<BackofficeApprovalRequest> approvalOpt = approvalRepo.findByApprovalRef(approvalRef);
        if (!approvalOpt.isPresent()) {
            return fail(404, "Approval request not found.");
        }

        BackofficeApprovalRequest approval = approvalOpt.get();
        if (!APPROVED.equals(approval.getStatus())) {
            return fail(409, "Approval request is not approved.");
        }
        if (approval.getExpiresAt() != null && approval.getExpiresAt().isBefore(Instant.now())) {
            return fail(409, "Approval request has expired.");
        }
        if (!normalize(productCode).equals(approval.getProductCode())) {
            return fail(403, "Approval product code does not match operation product code.");
        }
        if (!normalize(operationType).equals(approval.getOperationType())) {
            return fail(403, "Approval operation type does not match requested operation.");
        }

        return ok(approval, "Approval verified.");
    }

    @Transactional
    public BaseResponse consume(String adminKey, String approvalRef, String productCode, String operationType) {
        BaseResponse auth = authorize(adminKey);
        if (auth != null) {
            return auth;
        }
        if (!StringUtils.hasText(approvalRef)) {
            return fail(400, "approvalRef is required.");
        }

        Optional<BackofficeApprovalRequest> approvalOpt = approvalRepo.findForUpdateByApprovalRef(approvalRef);
        if (!approvalOpt.isPresent()) {
            return fail(404, "Approval request not found.");
        }

        BackofficeApprovalRequest approval = approvalOpt.get();
        BaseResponse verification = validateApprovedApproval(approval, productCode, operationType);
        if (verification != null) {
            return verification;
        }

        approval.setStatus(CONSUMED);
        approval.setConsumedAt(Instant.now());
        approvalRepo.save(approval);

        return ok(approval, "Approval consumed.");
    }

    private BaseResponse decide(String adminKey, String actor, String approvalRef,
            BackofficeApprovalDecisionRequest request, String decision) {
        BaseResponse auth = authorize(adminKey);
        if (auth != null) {
            return auth;
        }
        BackofficeUser approver = requireActiveOperator(actor);
        if (approver == null) {
            return fail(401, "Unknown or inactive approver.");
        }

        Optional<BackofficeApprovalRequest> approvalOpt = approvalRepo.findByApprovalRef(approvalRef);
        if (!approvalOpt.isPresent()) {
            return fail(404, "Approval request not found.");
        }

        BackofficeApprovalRequest approval = approvalOpt.get();
        if (!PENDING.equals(approval.getStatus())) {
            return fail(409, "Approval request is not pending.");
        }
        if (approval.getExpiresAt() != null && approval.getExpiresAt().isBefore(Instant.now())) {
            approval.setStatus("EXPIRED");
            approval.setDecisionComment("Approval request expired before decision.");
            approvalRepo.save(approval);
            return fail(409, "Approval request has expired.");
        }
        if (approver.getOperatorId().equals(approval.getRequestedBy())) {
            return fail(403, "Requester cannot approve or reject own request.");
        }
        if (!approver.getProductCode().equals(approval.getProductCode())) {
            return fail(403, "Approver product code does not match approval product code.");
        }

        approval.setStatus(decision);
        approval.setDecidedBy(approver.getOperatorId());
        approval.setDecisionComment(request == null ? "" : firstNonBlank(request.getComment(), ""));
        approval.setDecidedAt(Instant.now());
        approvalRepo.save(approval);

        return ok(approval, "Approval request " + decision.toLowerCase() + ".");
    }

    private BaseResponse validateApprovedApproval(BackofficeApprovalRequest approval, String productCode, String operationType) {
        if (!APPROVED.equals(approval.getStatus())) {
            return fail(409, "Approval request is not approved.");
        }
        if (approval.getExpiresAt() != null && approval.getExpiresAt().isBefore(Instant.now())) {
            return fail(409, "Approval request has expired.");
        }
        if (!normalize(productCode).equals(approval.getProductCode())) {
            return fail(403, "Approval product code does not match operation product code.");
        }
        if (!normalize(operationType).equals(approval.getOperationType())) {
            return fail(403, "Approval operation type does not match requested operation.");
        }
        return null;
    }

    private BackofficeUser requireActiveOperator(String operatorId) {
        if (!StringUtils.hasText(operatorId)) {
            return null;
        }
        Optional<BackofficeUser> user = userRepo.findByOperatorId(normalize(operatorId));
        if (!user.isPresent() || !"ACTIVE".equals(user.get().getStatus())) {
            return null;
        }
        return user.get();
    }

    private BaseResponse authorize(String adminKey) {
        if (!StringUtils.hasText(adminApiKey)) {
            return null;
        }
        if (!adminApiKey.equals(adminKey)) {
            return fail(401, "Invalid backoffice admin key.");
        }
        return null;
    }

    private BaseResponse ok(Object data, String description) {
        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setDescription(description);
        response.addData("result", data);
        return response;
    }

    private BaseResponse fail(int statusCode, String description) {
        BaseResponse response = new BaseResponse();
        response.setStatusCode(statusCode);
        response.setDescription(description);
        return response;
    }

    private String newApprovalRef() {
        return "APR-" + UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase();
    }

    private String firstNonBlank(String first, String fallback) {
        return StringUtils.hasText(first) ? first.trim() : fallback;
    }
}
