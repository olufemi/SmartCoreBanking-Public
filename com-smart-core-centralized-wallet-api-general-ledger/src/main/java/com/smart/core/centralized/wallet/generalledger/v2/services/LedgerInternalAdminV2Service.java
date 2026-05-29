package com.smart.core.centralized.wallet.generalledger.v2.services;

import com.smart.core.centralized.wallet.generalledger.utils.DecodedJWTToken;
import com.smart.core.centralized.wallet.generalledger.v2.domains.LedgerEntryV2;
import com.smart.core.centralized.wallet.generalledger.v2.domains.LedgerOutboxEventV2;
import com.smart.core.centralized.wallet.generalledger.v2.domains.LedgerSecurityEventV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.LedgerAdminResponse;
import com.smart.core.centralized.wallet.generalledger.v2.repository.LedgerEntryV2Repo;
import com.smart.core.centralized.wallet.generalledger.v2.repository.LedgerOutboxEventV2Repo;
import com.smart.core.centralized.wallet.generalledger.v2.repository.LedgerPortalProductProjection;
import com.smart.core.centralized.wallet.generalledger.v2.repository.LedgerSecurityEventV2Repo;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LedgerInternalAdminV2Service {

    private final LedgerOutboxEventV2Repo outboxRepo;
    private final LedgerSecurityEventV2Repo securityEventRepo;
    private final LedgerEntryV2Repo entryRepo;
    private final LedgerOutboxPublisherV2Service publisherService;
    private final LedgerApprovalVerificationService approvalVerificationService;

    @Value("${smartcore.ledger.internal.admin.api-key:}")
    private String internalAdminApiKey;

    public LedgerInternalAdminV2Service(LedgerOutboxEventV2Repo outboxRepo,
                                        LedgerSecurityEventV2Repo securityEventRepo,
                                        LedgerEntryV2Repo entryRepo,
                                        LedgerOutboxPublisherV2Service publisherService,
                                        LedgerApprovalVerificationService approvalVerificationService) {
        this.outboxRepo = outboxRepo;
        this.securityEventRepo = securityEventRepo;
        this.entryRepo = entryRepo;
        this.publisherService = publisherService;
        this.approvalVerificationService = approvalVerificationService;
    }

    public LedgerAdminResponse listOutbox(String auth, String adminKey, String status, int limit) {
        try {
            DecodedJWTToken decoded = authenticate(auth, adminKey);
            String normalizedStatus = isBlank(status) ? "PENDING" : status.trim().toUpperCase();
            List<LedgerOutboxEventV2> events = outboxRepo.findByProductCodeAndStatusOrderByCreatedAtDesc(
                    decoded.productCode, normalizedStatus, PageRequest.of(0, cappedLimit(limit)));
            return ok(events, "Outbox events fetched.");
        } catch (Exception ex) {
            return fail(400, ex.getMessage());
        }
    }

    public LedgerAdminResponse getOutboxEvent(String auth, String adminKey, String eventId) {
        try {
            DecodedJWTToken decoded = authenticate(auth, adminKey);
            Optional<LedgerOutboxEventV2> event = outboxRepo.findByIdAndProductCode(eventId, decoded.productCode);
            if (!event.isPresent()) {
                return fail(404, "Outbox event not found.");
            }
            return ok(event.get(), "Outbox event fetched.");
        } catch (Exception ex) {
            return fail(400, ex.getMessage());
        }
    }

    public LedgerAdminResponse retryOutboxEvent(String auth, String adminKey, String approvalRef, String eventId) {
        try {
            DecodedJWTToken decoded = authenticate(auth, adminKey);
            approvalVerificationService.requireApproved(approvalRef, decoded.productCode, "OUTBOX_RETRY");
            Optional<LedgerOutboxEventV2> eventOpt = outboxRepo.findByIdAndProductCode(eventId, decoded.productCode);
            if (!eventOpt.isPresent()) {
                return fail(404, "Outbox event not found.");
            }
            LedgerOutboxEventV2 event = eventOpt.get();
            if (!"FAILED".equalsIgnoreCase(event.getStatus()) && !"PENDING".equalsIgnoreCase(event.getStatus())) {
                return fail(409, "Only FAILED or PENDING outbox events can be retried.");
            }
            resetForRetry(event);
            publisherService.publishOne(event);
            Optional<LedgerOutboxEventV2> refreshed = outboxRepo.findByIdAndProductCode(eventId, decoded.productCode);
            return ok(refreshed.orElse(event), "Outbox retry attempted.");
        } catch (Exception ex) {
            return fail(400, ex.getMessage());
        }
    }

    public LedgerAdminResponse retryFailedOutboxEvents(String auth, String adminKey, String approvalRef, int limit) {
        try {
            DecodedJWTToken decoded = authenticate(auth, adminKey);
            approvalVerificationService.requireApproved(approvalRef, decoded.productCode, "OUTBOX_RETRY_FAILED");
            List<LedgerOutboxEventV2> events = outboxRepo.findByProductCodeAndStatusOrderByCreatedAtDesc(
                    decoded.productCode, "FAILED", PageRequest.of(0, cappedLimit(limit)));
            int attempted = 0;
            for (LedgerOutboxEventV2 event : events) {
                resetForRetry(event);
                publisherService.publishOne(event);
                attempted++;
            }
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("attempted", Integer.valueOf(attempted));
            data.put("productCode", decoded.productCode);
            return ok(data, "Failed outbox retries attempted.");
        } catch (Exception ex) {
            return fail(400, ex.getMessage());
        }
    }

    public LedgerAdminResponse listSecurityEvents(String auth, String adminKey, String severity, String eventType, int limit) {
        try {
            DecodedJWTToken decoded = authenticate(auth, adminKey);
            List<LedgerSecurityEventV2> events = securityEventRepo.searchRecent(
                    decoded.productCode, blankToNull(severity), blankToNull(eventType), PageRequest.of(0, cappedLimit(limit)));
            return ok(events, "Security events fetched.");
        } catch (Exception ex) {
            return fail(400, ex.getMessage());
        }
    }

    public LedgerAdminResponse portalDashboard(String auth, String adminKey, String requestedProductCode, String window) {
        try {
            String productScope = resolveProductScope(auth, adminKey, requestedProductCode);
            WindowRange range = resolveWindow(window);
            Object[] summary = entryRepo.summarizeForPortal(productScope, range.from, range.to);
            Map<String, Object> data = new LinkedHashMap<String, Object>();
            data.put("productCode", productScope == null ? "ALL" : productScope);
            data.put("window", range.code);
            data.put("totalInflow", number(summary, 0));
            data.put("totalOutflow", number(summary, 1));
            data.put("transactionCount", longValue(summary, 2));
            data.put("failedCount", longValue(summary, 3));
            data.put("reversedCount", longValue(summary, 4));

            List<Map<String, Object>> topProducts = new ArrayList<Map<String, Object>>();
            for (LedgerPortalProductProjection row : entryRepo.summarizeTopProducts(productScope, range.from, range.to, PageRequest.of(0, 5))) {
                Map<String, Object> product = new LinkedHashMap<String, Object>();
                product.put("productCode", row.getProductCode());
                product.put("transactionCount", row.getTxnCount());
                product.put("totalValue", row.getTotalValue() == null ? BigDecimal.ZERO : row.getTotalValue());
                topProducts.add(product);
            }
            data.put("topProducts", topProducts);
            return ok(data, "Portal dashboard fetched.");
        } catch (Exception ex) {
            return fail(400, ex.getMessage());
        }
    }

    public LedgerAdminResponse portalTransactions(String auth,
                                                  String adminKey,
                                                  String requestedProductCode,
                                                  String window,
                                                  Integer statusCode,
                                                  String legType,
                                                  String search,
                                                  int limit) {
        try {
            String productScope = resolveProductScope(auth, adminKey, requestedProductCode);
            WindowRange range = resolveWindow(window);
            List<LedgerEntryV2> entries = entryRepo.searchForPortal(
                    productScope,
                    statusCode,
                    blankToNull(legType),
                    range.from,
                    range.to,
                    likeOrNull(search),
                    PageRequest.of(0, cappedLimit(limit))
            );
            List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
            for (LedgerEntryV2 entry : entries) {
                Map<String, Object> item = new LinkedHashMap<String, Object>();
                item.put("requestRef", entry.getRequestRef());
                item.put("batchRef", entry.getBatchRef());
                item.put("transactionId", entry.getTransactionId());
                item.put("productCode", entry.getProductCode());
                item.put("accountNumber", entry.getAccountNumber());
                item.put("legType", entry.getLegType());
                item.put("transType", entry.getTransType());
                item.put("amount", entry.getAmount());
                item.put("fees", entry.getFees());
                item.put("finalCharges", entry.getFinalCharges());
                item.put("balanceBefore", entry.getBalanceBefore());
                item.put("balanceAfter", entry.getBalanceAfter());
                item.put("statusCode", entry.getStatusCode());
                item.put("narration", entry.getNarration());
                item.put("description", entry.getDescription());
                item.put("createdAt", entry.getCreatedAt());
                items.add(item);
            }
            Map<String, Object> data = new LinkedHashMap<String, Object>();
            data.put("productCode", productScope == null ? "ALL" : productScope);
            data.put("window", range.code);
            data.put("total", Integer.valueOf(items.size()));
            data.put("items", items);
            return ok(data, "Portal transactions fetched.");
        } catch (Exception ex) {
            return fail(400, ex.getMessage());
        }
    }

    @Transactional
    public void resetForRetry(LedgerOutboxEventV2 event) {
        event.setStatus("PENDING");
        event.setLastError(null);
        outboxRepo.save(event);
    }

    private DecodedJWTToken authenticate(String auth, String adminKey) throws Exception {
        if (!isBlank(internalAdminApiKey) && !internalAdminApiKey.equals(adminKey)) {
            throw new IllegalArgumentException("Invalid internal admin key.");
        }
        DecodedJWTToken decoded = DecodedJWTToken.getDecoded(auth);
        if (isBlank(decoded.productCode)) {
            throw new IllegalArgumentException("Product code missing from token.");
        }
        return decoded;
    }

    private String resolveProductScope(String auth, String adminKey, String requestedProductCode) throws Exception {
        String normalizedRequested = normalizeProduct(requestedProductCode);
        boolean adminAuthorized = isBlank(internalAdminApiKey) || internalAdminApiKey.equals(adminKey);
        if (adminAuthorized) {
            return normalizedRequested;
        }
        if (isBlank(auth)) {
            throw new IllegalArgumentException("Authorization is required.");
        }
        DecodedJWTToken decoded = DecodedJWTToken.getDecoded(auth);
        if (isBlank(decoded.productCode)) {
            throw new IllegalArgumentException("Product code missing from token.");
        }
        if (normalizedRequested != null && !normalizedRequested.equals(decoded.productCode.trim().toUpperCase())) {
            throw new IllegalArgumentException("Requested product code does not match token scope.");
        }
        return decoded.productCode.trim().toUpperCase();
    }

    private String normalizeProduct(String productCode) {
        if (isBlank(productCode) || "ALL".equalsIgnoreCase(productCode)) {
            return null;
        }
        return productCode.trim().toUpperCase();
    }

    private WindowRange resolveWindow(String window) {
        String code = isBlank(window) ? "24H" : window.trim().toUpperCase();
        LocalDateTime now = LocalDateTime.now();
        if ("30M".equals(code)) {
            return new WindowRange(code, now.minusMinutes(30), now);
        }
        if ("1H".equals(code)) {
            return new WindowRange(code, now.minusHours(1), now);
        }
        if ("7D".equals(code)) {
            return new WindowRange(code, now.minusDays(7), now);
        }
        return new WindowRange("24H", now.minusHours(24), now);
    }

    private String likeOrNull(String search) {
        if (isBlank(search)) {
            return null;
        }
        return "%" + search.trim().toUpperCase() + "%";
    }

    private BigDecimal number(Object[] values, int index) {
        Object value = values != null && values.length > index ? values[index] : BigDecimal.ZERO;
        return value instanceof BigDecimal ? (BigDecimal) value : BigDecimal.ZERO;
    }

    private long longValue(Object[] values, int index) {
        Object value = values != null && values.length > index ? values[index] : Long.valueOf(0L);
        return value instanceof Long ? ((Long) value).longValue() : 0L;
    }

    private LedgerAdminResponse ok(Object data, String description) {
        LedgerAdminResponse response = new LedgerAdminResponse();
        response.setStatusCode(200);
        response.setDescription(description);
        response.setData(data);
        return response;
    }

    private LedgerAdminResponse fail(int code, String description) {
        LedgerAdminResponse response = new LedgerAdminResponse();
        response.setStatusCode(code);
        response.setDescription(description);
        return response;
    }

    private int cappedLimit(int limit) {
        if (limit <= 0) {
            return 100;
        }
        return Math.min(limit, 500);
    }

    private String blankToNull(String value) {
        return isBlank(value) ? null : value.trim().toUpperCase();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static class WindowRange {
        private final String code;
        private final LocalDateTime from;
        private final LocalDateTime to;

        private WindowRange(String code, LocalDateTime from, LocalDateTime to) {
            this.code = code;
            this.from = from;
            this.to = to;
        }
    }
}
