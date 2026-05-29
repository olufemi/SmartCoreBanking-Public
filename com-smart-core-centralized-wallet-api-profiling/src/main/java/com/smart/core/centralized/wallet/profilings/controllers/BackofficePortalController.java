package com.smart.core.centralized.wallet.profilings.controllers;

import com.smart.core.centralized.wallet.profilings.model.BaseResponse;
import com.smart.core.centralized.wallet.profilings.services.BackofficePortalFacadeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/backoffice/portal")
public class BackofficePortalController {

    private final BackofficePortalFacadeService portalFacadeService;

    public BackofficePortalController(BackofficePortalFacadeService portalFacadeService) {
        this.portalFacadeService = portalFacadeService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<BaseResponse> dashboard(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(value = "window", defaultValue = "24h") String window,
            @RequestParam(value = "productCode", required = false) String productCode) {
        return new ResponseEntity<BaseResponse>(portalFacadeService.dashboard(authorization, window, productCode), HttpStatus.OK);
    }

    @GetMapping("/transactions")
    public ResponseEntity<BaseResponse> transactions(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(value = "window", defaultValue = "24h") String window,
            @RequestParam(value = "productCode", required = false) String productCode,
            @RequestParam(value = "statusCode", required = false) Integer statusCode,
            @RequestParam(value = "legType", required = false) String legType,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "limit", defaultValue = "100") int limit) {
        return new ResponseEntity<BaseResponse>(portalFacadeService.transactions(authorization, window, productCode, statusCode, legType, search, limit), HttpStatus.OK);
    }

    @GetMapping("/approvals")
    public ResponseEntity<BaseResponse> approvals(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "productCode", required = false) String productCode) {
        return new ResponseEntity<BaseResponse>(portalFacadeService.approvals(authorization, status, productCode), HttpStatus.OK);
    }

    @GetMapping("/operators")
    public ResponseEntity<BaseResponse> operators(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(value = "productCode", required = false) String productCode) {
        return new ResponseEntity<BaseResponse>(portalFacadeService.operators(authorization, productCode), HttpStatus.OK);
    }

    @GetMapping("/roles")
    public ResponseEntity<BaseResponse> roles(@RequestHeader("Authorization") String authorization) {
        return new ResponseEntity<BaseResponse>(portalFacadeService.roles(authorization), HttpStatus.OK);
    }

    @GetMapping("/clients")
    public ResponseEntity<BaseResponse> clients(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(value = "window", defaultValue = "24h") String window,
            @RequestParam(value = "productCode", required = false) String productCode,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "limit", defaultValue = "500") int limit) {
        return new ResponseEntity<BaseResponse>(portalFacadeService.clients(authorization, window, productCode, search, limit), HttpStatus.OK);
    }

    @GetMapping("/reversals")
    public ResponseEntity<BaseResponse> reversals(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(value = "window", defaultValue = "24h") String window,
            @RequestParam(value = "productCode", required = false) String productCode,
            @RequestParam(value = "limit", defaultValue = "25") int limit) {
        return new ResponseEntity<BaseResponse>(portalFacadeService.reversals(authorization, window, productCode, limit), HttpStatus.OK);
    }

    @GetMapping("/health")
    public ResponseEntity<BaseResponse> health(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(value = "window", defaultValue = "24h") String window,
            @RequestParam(value = "productCode", required = false) String productCode) {
        return new ResponseEntity<BaseResponse>(portalFacadeService.health(authorization, window, productCode), HttpStatus.OK);
    }
}
