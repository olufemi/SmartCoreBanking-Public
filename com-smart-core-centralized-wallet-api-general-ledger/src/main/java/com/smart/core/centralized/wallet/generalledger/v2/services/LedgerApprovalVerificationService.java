package com.smart.core.centralized.wallet.generalledger.v2.services;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Service
public class LedgerApprovalVerificationService {

    private final RestTemplate restTemplate;

    @Value("${smartcore.ledger.approval.enabled:false}")
    private boolean approvalEnabled;

    @Value("${smartcore.ledger.approval.profiling-base-url:http://localhost:60002}")
    private String profilingBaseUrl;

    @Value("${smartcore.ledger.approval.admin-api-key:}")
    private String approvalAdminApiKey;

    public LedgerApprovalVerificationService(@Qualifier("withoutEureka") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void requireApproved(String approvalRef, String productCode, String operationType) {
        if (!approvalEnabled) {
            return;
        }
        if (!StringUtils.hasText(approvalRef)) {
            throw new IllegalArgumentException("approvalRef is required for " + operationType + ".");
        }

        URI uri = UriComponentsBuilder.fromHttpUrl(profilingBaseUrl)
                .path("/backoffice/approvals/{approvalRef}/consume")
                .queryParam("productCode", productCode)
                .queryParam("operationType", operationType)
                .buildAndExpand(approvalRef.trim())
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        if (StringUtils.hasText(approvalAdminApiKey)) {
            headers.set("X-Backoffice-Admin-Key", approvalAdminApiKey);
        }

        ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<Void>(headers), Map.class);
        Map body = response.getBody();
        int statusCode = extractStatusCode(body);
        if (statusCode != 200) {
            throw new IllegalArgumentException(extractDescription(body));
        }
    }

    private int extractStatusCode(Map body) {
        if (body == null || body.get("statusCode") == null) {
            return 500;
        }
        Object statusCode = body.get("statusCode");
        if (statusCode instanceof Number) {
            return ((Number) statusCode).intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(statusCode));
        } catch (NumberFormatException ex) {
            return 500;
        }
    }

    private String extractDescription(Map body) {
        if (body == null || body.get("description") == null) {
            return "Approval verification failed.";
        }
        return String.valueOf(body.get("description"));
    }
}
