package com.smart.core.centralized.wallet.generalledger.v2.controllers;

import com.smart.core.centralized.wallet.generalledger.v2.models.LedgerAdminResponse;
import com.smart.core.centralized.wallet.generalledger.v2.services.LedgerInternalAdminV2Service;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v2/internal")
public class LedgerInternalAdminV2Controller {

    private final LedgerInternalAdminV2Service adminService;

    public LedgerInternalAdminV2Controller(LedgerInternalAdminV2Service adminService) {
        this.adminService = adminService;
    }

    @GetMapping(value = "/outbox", produces = MediaType.APPLICATION_JSON_VALUE)
    public LedgerAdminResponse listOutbox(
            @RequestHeader("Authorization") String auth,
            @RequestHeader(value = "X-Internal-Admin-Key", required = false) String adminKey,
            @RequestParam(value = "status", defaultValue = "PENDING") String status,
            @RequestParam(value = "limit", defaultValue = "100") int limit) {
        return adminService.listOutbox(auth, adminKey, status, limit);
    }

    @GetMapping(value = "/outbox/{eventId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public LedgerAdminResponse getOutboxEvent(
            @RequestHeader("Authorization") String auth,
            @RequestHeader(value = "X-Internal-Admin-Key", required = false) String adminKey,
            @PathVariable("eventId") String eventId) {
        return adminService.getOutboxEvent(auth, adminKey, eventId);
    }

    @PostMapping(value = "/outbox/{eventId}/retry", produces = MediaType.APPLICATION_JSON_VALUE)
    public LedgerAdminResponse retryOutboxEvent(
            @RequestHeader("Authorization") String auth,
            @RequestHeader(value = "X-Internal-Admin-Key", required = false) String adminKey,
            @RequestHeader(value = "X-Approval-Ref", required = false) String approvalRef,
            @PathVariable("eventId") String eventId) {
        return adminService.retryOutboxEvent(auth, adminKey, approvalRef, eventId);
    }

    @PostMapping(value = "/outbox/retry-failed", produces = MediaType.APPLICATION_JSON_VALUE)
    public LedgerAdminResponse retryFailedOutboxEvents(
            @RequestHeader("Authorization") String auth,
            @RequestHeader(value = "X-Internal-Admin-Key", required = false) String adminKey,
            @RequestHeader(value = "X-Approval-Ref", required = false) String approvalRef,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return adminService.retryFailedOutboxEvents(auth, adminKey, approvalRef, limit);
    }

    @GetMapping(value = "/security-events", produces = MediaType.APPLICATION_JSON_VALUE)
    public LedgerAdminResponse listSecurityEvents(
            @RequestHeader("Authorization") String auth,
            @RequestHeader(value = "X-Internal-Admin-Key", required = false) String adminKey,
            @RequestParam(value = "severity", required = false) String severity,
            @RequestParam(value = "eventType", required = false) String eventType,
            @RequestParam(value = "limit", defaultValue = "100") int limit) {
        return adminService.listSecurityEvents(auth, adminKey, severity, eventType, limit);
    }

    @GetMapping(value = "/portal/dashboard", produces = MediaType.APPLICATION_JSON_VALUE)
    public LedgerAdminResponse portalDashboard(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @RequestHeader(value = "X-Internal-Admin-Key", required = false) String adminKey,
            @RequestParam(value = "productCode", required = false) String productCode,
            @RequestParam(value = "window", defaultValue = "24h") String window) {
        return adminService.portalDashboard(auth, adminKey, productCode, window);
    }

    @GetMapping(value = "/portal/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
    public LedgerAdminResponse portalTransactions(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @RequestHeader(value = "X-Internal-Admin-Key", required = false) String adminKey,
            @RequestParam(value = "productCode", required = false) String productCode,
            @RequestParam(value = "window", defaultValue = "24h") String window,
            @RequestParam(value = "statusCode", required = false) Integer statusCode,
            @RequestParam(value = "legType", required = false) String legType,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "limit", defaultValue = "100") int limit) {
        return adminService.portalTransactions(auth, adminKey, productCode, window, statusCode, legType, search, limit);
    }
}
