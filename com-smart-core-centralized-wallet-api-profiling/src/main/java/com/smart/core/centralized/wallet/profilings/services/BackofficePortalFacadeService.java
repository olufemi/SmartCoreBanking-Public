package com.smart.core.centralized.wallet.profilings.services;

import com.smart.core.centralized.wallet.profilings.domains.BackofficeApprovalRequest;
import com.smart.core.centralized.wallet.profilings.domains.BackofficeRole;
import com.smart.core.centralized.wallet.profilings.domains.BackofficeUser;
import com.smart.core.centralized.wallet.profilings.model.BackofficePortalSession;
import com.smart.core.centralized.wallet.profilings.model.BaseResponse;
import com.smart.core.centralized.wallet.profilings.repo.BackofficeApprovalRequestRepo;
import com.smart.core.centralized.wallet.profilings.repo.BackofficeRoleRepo;
import com.smart.core.centralized.wallet.profilings.repo.BackofficeUserRepo;
import com.smart.core.centralized.wallet.profilings.repo.UserDetailsRepo;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Comparator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class BackofficePortalFacadeService {

    private final BackofficePortalAuthService portalAuthService;
    private final BackofficeApprovalRequestRepo approvalRepo;
    private final BackofficeUserRepo userRepo;
    private final BackofficeRoleRepo roleRepo;
    private final UserDetailsRepo userDetailsRepo;
    private final RestTemplate restTemplate;

    @Value("${smartcore.portal.ledger-base-url:http://localhost:60004}")
    private String ledgerBaseUrl;

    @Value("${smartcore.portal.ledger-internal-admin-key:}")
    private String ledgerInternalAdminKey;

    public BackofficePortalFacadeService(BackofficePortalAuthService portalAuthService,
                                         BackofficeApprovalRequestRepo approvalRepo,
                                         BackofficeUserRepo userRepo,
                                         BackofficeRoleRepo roleRepo,
                                         UserDetailsRepo userDetailsRepo) {
        this.portalAuthService = portalAuthService;
        this.approvalRepo = approvalRepo;
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.userDetailsRepo = userDetailsRepo;
        this.restTemplate = new RestTemplate();
    }

    public BaseResponse dashboard(String authorization, String window, String requestedProductCode) {
        try {
            BackofficePortalSession session = portalAuthService.requireSession(authorization);
            BaseResponse ledgerResponse = getLedger("/v2/internal/portal/dashboard", query(session, requestedProductCode, window, null, null, null, Integer.valueOf(5)));
            BaseResponse response = ok("Portal dashboard fetched.");
            response.addData("session", session);
            response.addData("dashboard", ledgerResponse.getData());
            response.addData("approvalCount", Integer.valueOf(fetchApprovals(session, null, null).size()));
            return response;
        } catch (Exception ex) {
            return fail(401, ex.getMessage());
        }
    }

    public BaseResponse transactions(String authorization, String window, String requestedProductCode, Integer statusCode, String legType, String search, int limit) {
        try {
            BackofficePortalSession session = portalAuthService.requireSession(authorization);
            BaseResponse ledgerResponse = getLedger("/v2/internal/portal/transactions", query(session, requestedProductCode, window, statusCode, legType, search, Integer.valueOf(limit)));
            BaseResponse response = ok("Portal transactions fetched.");
            response.addData("session", session);
            response.addData("result", ledgerResponse.getData());
            return response;
        } catch (Exception ex) {
            return fail(401, ex.getMessage());
        }
    }

    public BaseResponse approvals(String authorization, String status, String requestedProductCode) {
        try {
            BackofficePortalSession session = portalAuthService.requireSession(authorization);
            List<BackofficeApprovalRequest> approvals = fetchApprovals(session, status, requestedProductCode);
            BaseResponse response = ok("Portal approvals fetched.");
            response.addData("session", session);
            response.addData("result", approvals);
            return response;
        } catch (Exception ex) {
            return fail(401, ex.getMessage());
        }
    }

    public BaseResponse operators(String authorization, String requestedProductCode) {
        try {
            BackofficePortalSession session = portalAuthService.requireSession(authorization);
            String productScope = resolveProductScope(session, requestedProductCode);
            List<BackofficeUser> users = productScope == null
                    ? userRepo.findAll()
                    : userRepo.findByProductCodeOrderByCreatedAtDesc(productScope);
            BaseResponse response = ok("Portal operators fetched.");
            response.addData("session", session);
            response.addData("result", users);
            return response;
        } catch (Exception ex) {
            return fail(401, ex.getMessage());
        }
    }

    public BaseResponse roles(String authorization) {
        try {
            BackofficePortalSession session = portalAuthService.requireSession(authorization);
            List<BackofficeRole> roles = roleRepo.findAll();
            BaseResponse response = ok("Portal roles fetched.");
            response.addData("session", session);
            response.addData("result", roles);
            return response;
        } catch (Exception ex) {
            return fail(401, ex.getMessage());
        }
    }

    public BaseResponse clients(String authorization, String window, String requestedProductCode, String search, int limit) {
        try {
            BackofficePortalSession session = portalAuthService.requireSession(authorization);
            String productScope = resolveProductScope(session, requestedProductCode);
            String normalizedSearch = search == null ? null : search.trim().toLowerCase(Locale.ENGLISH);
            List<com.smart.core.centralized.wallet.profilings.domains.UserDetails> clients = loadClients(productScope, normalizedSearch);
            Map<String, Map<String, Object>> productMetrics = buildClientMetrics(session, requestedProductCode, window, limit);

            List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
            for (com.smart.core.centralized.wallet.profilings.domains.UserDetails item : clients) {
                String clientProductCode = normalizeProduct(item.getProdudctCode());
                Map<String, Object> metrics = productMetrics.get(clientProductCode);
                Map<String, Object> mapped = new LinkedHashMap<String, Object>();
                mapped.put("id", item.getId());
                mapped.put("productName", item.getProductName());
                mapped.put("emailAddress", item.getEmailAddress());
                mapped.put("productCode", clientProductCode == null ? "-" : clientProductCode);
                mapped.put("clearanceId", item.getClearanceId());
                mapped.put("enabled", item.getEnabled());
                mapped.put("createdDate", item.getCreatedDate());
                mapped.put("lastModifiedDate", item.getLastModifiedDate());
                mapped.put("transactionCount", metrics == null ? Integer.valueOf(0) : metrics.get("transactionCount"));
                mapped.put("totalInflow", metrics == null ? "0" : metrics.get("totalInflow"));
                mapped.put("totalOutflow", metrics == null ? "0" : metrics.get("totalOutflow"));
                mapped.put("netMovement", metrics == null ? "0" : metrics.get("netMovement"));
                mapped.put("failedCount", metrics == null ? Integer.valueOf(0) : metrics.get("failedCount"));
                result.add(mapped);
            }

            Map<String, Object> summary = new LinkedHashMap<String, Object>();
            summary.put("clientCount", Integer.valueOf(result.size()));
            summary.put("activeCount", Long.valueOf(result.stream().filter(item -> "1".equals(String.valueOf(item.get("enabled")))).count()));
            summary.put("productCode", productScope == null ? "ALL" : productScope);
            summary.put("window", window);

            BaseResponse response = ok("Portal clients fetched.");
            response.addData("session", session);
            response.addData("summary", summary);
            response.addData("result", result);
            return response;
        } catch (Exception ex) {
            return fail(401, ex.getMessage());
        }
    }

    public BaseResponse reversals(String authorization, String window, String requestedProductCode, int limit) {
        try {
            BackofficePortalSession session = portalAuthService.requireSession(authorization);
            BaseResponse ledgerResponse = getLedger("/v2/internal/portal/transactions", query(session, requestedProductCode, window, Integer.valueOf(409), null, null, Integer.valueOf(limit)));
            Map<String, Object> ledgerData = ledgerResponse.getData();
            List<Map<String, Object>> reversals = new ArrayList<Map<String, Object>>();
            Object itemsObject = ledgerData == null ? null : ledgerData.get("items");
            if (itemsObject instanceof List) {
                List items = (List) itemsObject;
                for (Object itemObject : items) {
                    if (itemObject instanceof Map) {
                        Map item = (Map) itemObject;
                        Map<String, Object> mapped = new LinkedHashMap<String, Object>();
                        mapped.put("ref", asText(item.get("requestRef")));
                        mapped.put("reason", firstText(item.get("description"), item.get("narration"), "Ledger reversal/exception"));
                        mapped.put("product", asText(item.get("productCode")));
                        mapped.put("status", resolveRecoveryStatus(item.get("statusCode")));
                        reversals.add(mapped);
                    }
                }
            }
            BaseResponse response = ok("Portal reversals fetched.");
            response.addData("session", session);
            response.addData("result", reversals);
            return response;
        } catch (Exception ex) {
            return fail(401, ex.getMessage());
        }
    }

    public BaseResponse health(String authorization, String window, String requestedProductCode) {
        try {
            BackofficePortalSession session = portalAuthService.requireSession(authorization);
            BaseResponse dashboardResponse = getLedger("/v2/internal/portal/dashboard", query(session, requestedProductCode, window, null, null, null, Integer.valueOf(5)));
            Map<String, Object> dashboard = dashboardResponse.getData();
            List<BackofficeApprovalRequest> approvals = fetchApprovals(session, null, requestedProductCode);

            long transactionCount = asLong(dashboard.get("transactionCount"));
            long failedCount = asLong(dashboard.get("failedCount"));
            long reversedCount = asLong(dashboard.get("reversedCount"));
            long approvalCount = approvals.size();

            List<Map<String, Object>> services = new ArrayList<Map<String, Object>>();
            services.add(service("API Gateway", "Portal Route Active", "success"));
            services.add(service("Profiling", "Healthy", "success"));
            services.add(service("General Ledger", failedCount > 0 ? "Exceptions Observed" : "Healthy", failedCount > 0 ? "warning" : "success"));
            services.add(service("Approvals Engine", approvalCount > 0 ? "Queue Busy" : "Healthy", approvalCount > 0 ? "warning" : "success"));
            services.add(service("Portal Auth", "Session Active", "success"));

            List<Map<String, Object>> stats = new ArrayList<Map<String, Object>>();
            stats.add(stat("Pending Approvals", String.valueOf(approvalCount)));
            stats.add(stat("Failed Transactions", String.valueOf(failedCount)));
            stats.add(stat("Reversed Transactions", String.valueOf(reversedCount)));
            stats.add(stat("Transactions In Window", String.valueOf(transactionCount)));

            Map<String, Object> data = new LinkedHashMap<String, Object>();
            data.put("services", services);
            data.put("stats", stats);
            data.put("window", dashboard.get("window"));
            data.put("productCode", dashboard.get("productCode"));

            BaseResponse response = ok("Portal health fetched.");
            response.addData("session", session);
            response.addData("result", data);
            return response;
        } catch (Exception ex) {
            return fail(401, ex.getMessage());
        }
    }

    private List<BackofficeApprovalRequest> fetchApprovals(BackofficePortalSession session, String status, String requestedProductCode) {
        String resolvedStatus = StringUtils.hasText(status) ? status.trim().toUpperCase(Locale.ENGLISH) : "PENDING";
        String productScope = resolveProductScope(session, requestedProductCode);
        if (productScope == null) {
            return approvalRepo.findByStatusOrderByRequestedAtDesc(resolvedStatus);
        }
        return approvalRepo.findByProductCodeAndStatusOrderByRequestedAtDesc(productScope, resolvedStatus);
    }

    private List<com.smart.core.centralized.wallet.profilings.domains.UserDetails> loadClients(String productScope, String normalizedSearch) {
        List<com.smart.core.centralized.wallet.profilings.domains.UserDetails> clients = productScope == null
                ? userDetailsRepo.findAll()
                : userDetailsRepo.findByProdudctCodeDe(productScope);
        List<com.smart.core.centralized.wallet.profilings.domains.UserDetails> filtered = new ArrayList<com.smart.core.centralized.wallet.profilings.domains.UserDetails>();
        for (com.smart.core.centralized.wallet.profilings.domains.UserDetails item : clients) {
            String haystack = String.valueOf(item.getProductName()) + " "
                    + String.valueOf(item.getEmailAddress()) + " "
                    + String.valueOf(item.getProdudctCode()) + " "
                    + String.valueOf(item.getClearanceId());
            if (!StringUtils.hasText(normalizedSearch) || haystack.toLowerCase(Locale.ENGLISH).contains(normalizedSearch)) {
                filtered.add(item);
            }
        }
        filtered.sort(Comparator.comparing(com.smart.core.centralized.wallet.profilings.domains.UserDetails::getCreatedDate,
                Comparator.nullsLast(Comparator.reverseOrder())));
        return filtered;
    }

    private Map<String, Map<String, Object>> buildClientMetrics(BackofficePortalSession session, String requestedProductCode, String window, int limit) {
        BaseResponse ledgerResponse = getLedger("/v2/internal/portal/transactions", query(session, requestedProductCode, window, null, null, null, Integer.valueOf(limit)));
        Map<String, Map<String, Object>> metrics = new LinkedHashMap<String, Map<String, Object>>();
        Map<String, Object> ledgerData = ledgerResponse.getData();
        Object itemsObject = ledgerData == null ? null : ledgerData.get("items");
        if (!(itemsObject instanceof List)) {
            return metrics;
        }
        List items = (List) itemsObject;
        for (Object itemObject : items) {
            if (!(itemObject instanceof Map)) {
                continue;
            }
            Map item = (Map) itemObject;
            String productCode = normalizeProduct(asText(item.get("productCode")));
            if (!StringUtils.hasText(productCode) || "-".equals(productCode)) {
                continue;
            }
            Map<String, Object> current = metrics.get(productCode);
            if (current == null) {
                current = new LinkedHashMap<String, Object>();
                current.put("transactionCount", Integer.valueOf(0));
                current.put("totalInflow", Double.valueOf(0D));
                current.put("totalOutflow", Double.valueOf(0D));
                current.put("failedCount", Integer.valueOf(0));
                metrics.put(productCode, current);
            }
            current.put("transactionCount", Integer.valueOf(((Integer) current.get("transactionCount")).intValue() + 1));
            String legType = asText(item.get("legType"));
            double amount = asDouble(item.get("amount"));
            double finalCharges = asDouble(item.get("finalCharges"));
            if ("DEBIT".equalsIgnoreCase(legType)) {
                current.put("totalOutflow", Double.valueOf(((Double) current.get("totalOutflow")).doubleValue() + (finalCharges > 0D ? finalCharges : amount)));
            } else {
                current.put("totalInflow", Double.valueOf(((Double) current.get("totalInflow")).doubleValue() + amount));
            }
            if (asLong(item.get("statusCode")) >= 400L) {
                current.put("failedCount", Integer.valueOf(((Integer) current.get("failedCount")).intValue() + 1));
            }
        }
        for (Map<String, Object> current : metrics.values()) {
            double inflow = ((Double) current.get("totalInflow")).doubleValue();
            double outflow = ((Double) current.get("totalOutflow")).doubleValue();
            current.put("totalInflow", String.valueOf(inflow));
            current.put("totalOutflow", String.valueOf(outflow));
            current.put("netMovement", String.valueOf(inflow - outflow));
        }
        return metrics;
    }

    private BaseResponse getLedger(String path, String query) {
        String url = ledgerBaseUrl.replaceAll("/$", "") + path + query;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        if (StringUtils.hasText(ledgerInternalAdminKey)) {
            headers.set("X-Internal-Admin-Key", ledgerInternalAdminKey);
        }
        HttpEntity<Void> entity = new HttpEntity<Void>(headers);
        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            return parseLedgerBody(response.getBody());
        } catch (RestClientException ex) {
            return fail(502, "Unable to fetch ledger portal data now.");
        }
    }

    private BaseResponse parseLedgerBody(Map body) {
        BaseResponse result = new BaseResponse();
        if (body != null && body.get("statusCode") != null) {
            result.setStatusCode(((Number) body.get("statusCode")).intValue());
            result.setDescription(String.valueOf(body.get("description")));
            Object data = body.get("data");
            if (data instanceof Map) {
                result.setData((Map<String, Object>) data);
            } else {
                result.addData("result", data);
            }
            return result;
        }
        return fail(500, "Ledger portal response is invalid.");
    }

    private String query(BackofficePortalSession session, String requestedProductCode, String window, Integer statusCode, String legType, String search, Integer limit) {
        StringBuilder sb = new StringBuilder("?");
        append(sb, "window", StringUtils.hasText(window) ? window : "24h");
        String productScope = resolveProductScope(session, requestedProductCode);
        if (productScope != null) {
            append(sb, "productCode", productScope);
        }
        if (statusCode != null) {
            append(sb, "statusCode", String.valueOf(statusCode));
        }
        if (StringUtils.hasText(legType)) {
            append(sb, "legType", legType);
        }
        if (StringUtils.hasText(search)) {
            append(sb, "search", search);
        }
        if (limit != null) {
            append(sb, "limit", String.valueOf(limit));
        }
        return sb.toString();
    }


    private String resolveProductScope(BackofficePortalSession session, String requestedProductCode) {
        String sessionScope = normalizeProduct(session.getProductCode());
        String requestedScope = normalizeProduct(requestedProductCode);
        if (sessionScope == null || "ALL".equalsIgnoreCase(sessionScope)) {
            return requestedScope;
        }
        if (requestedScope == null || "ALL".equalsIgnoreCase(requestedScope) || sessionScope.equalsIgnoreCase(requestedScope)) {
            return sessionScope;
        }
        throw new IllegalArgumentException("Requested productCode is outside portal session scope.");
    }

    private void append(StringBuilder sb, String key, String value) {
        if (sb.length() > 1) {
            sb.append('&');
        }
        sb.append(key).append('=').append(urlEncode(value));
    }

    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to encode portal query parameter.", ex);
        }
    }

    private String normalizeProduct(String productCode) {
        return productCode == null ? null : productCode.trim().toUpperCase(Locale.ENGLISH);
    }

    private Map<String, Object> service(String name, String status, String tone) {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("name", name);
        data.put("status", status);
        data.put("tone", tone);
        return data;
    }

    private Map<String, Object> stat(String label, String value) {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("label", label);
        data.put("value", value);
        return data;
    }

    private String resolveRecoveryStatus(Object statusCode) {
        long code = asLong(statusCode);
        if (code == 200L) {
            return "Recovered";
        }
        if (code == 409L) {
            return "Reversed";
        }
        return code >= 400L ? "Needs Review" : "In Progress";
    }

    private long asLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value != null) {
            try {
                return Long.parseLong(String.valueOf(value));
            } catch (Exception ignored) {
            }
        }
        return 0L;
    }

    private double asDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value != null) {
            try {
                return Double.parseDouble(String.valueOf(value));
            } catch (Exception ignored) {
            }
        }
        return 0D;
    }

    private String asText(Object value) {
        return value == null ? "-" : String.valueOf(value);
    }

    private String firstText(Object primary, Object secondary, String fallback) {
        if (primary != null && StringUtils.hasText(String.valueOf(primary))) {
            return String.valueOf(primary);
        }
        if (secondary != null && StringUtils.hasText(String.valueOf(secondary))) {
            return String.valueOf(secondary);
        }
        return fallback;
    }

    private BaseResponse ok(String description) {
        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setDescription(description);
        response.setData(new HashMap<String, Object>());
        return response;
    }

    private BaseResponse fail(int statusCode, String description) {
        BaseResponse response = new BaseResponse();
        response.setStatusCode(statusCode);
        response.setDescription(description);
        return response;
    }
}
