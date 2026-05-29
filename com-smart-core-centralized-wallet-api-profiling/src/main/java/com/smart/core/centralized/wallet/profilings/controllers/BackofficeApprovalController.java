package com.smart.core.centralized.wallet.profilings.controllers;

import com.smart.core.centralized.wallet.profilings.model.BackofficeApprovalDecisionRequest;
import com.smart.core.centralized.wallet.profilings.model.BackofficeApprovalSubmitRequest;
import com.smart.core.centralized.wallet.profilings.model.BaseResponse;
import com.smart.core.centralized.wallet.profilings.services.BackofficeApprovalService;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/backoffice/approvals")
public class BackofficeApprovalController {

    private final BackofficeApprovalService approvalService;

    public BackofficeApprovalController(BackofficeApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @PostMapping
    public ResponseEntity<BaseResponse> submit(
            @RequestHeader(value = "X-Backoffice-Admin-Key", required = false) String adminKey,
            @RequestHeader(value = "X-Actor", required = false) String actor,
            @RequestBody @Valid BackofficeApprovalSubmitRequest request) {
        return new ResponseEntity<>(approvalService.submit(adminKey, actor, request), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<BaseResponse> list(
            @RequestHeader(value = "X-Backoffice-Admin-Key", required = false) String adminKey,
            @RequestParam(value = "productCode", required = false) String productCode,
            @RequestParam(value = "status", required = false) String status) {
        return new ResponseEntity<>(approvalService.list(adminKey, productCode, status), HttpStatus.OK);
    }

    @PostMapping("/{approvalRef}/approve")
    public ResponseEntity<BaseResponse> approve(
            @RequestHeader(value = "X-Backoffice-Admin-Key", required = false) String adminKey,
            @RequestHeader(value = "X-Actor", required = false) String actor,
            @PathVariable("approvalRef") String approvalRef,
            @RequestBody(required = false) BackofficeApprovalDecisionRequest request) {
        return new ResponseEntity<>(approvalService.approve(adminKey, actor, approvalRef, request), HttpStatus.OK);
    }

    @PostMapping("/{approvalRef}/reject")
    public ResponseEntity<BaseResponse> reject(
            @RequestHeader(value = "X-Backoffice-Admin-Key", required = false) String adminKey,
            @RequestHeader(value = "X-Actor", required = false) String actor,
            @PathVariable("approvalRef") String approvalRef,
            @RequestBody(required = false) BackofficeApprovalDecisionRequest request) {
        return new ResponseEntity<>(approvalService.reject(adminKey, actor, approvalRef, request), HttpStatus.OK);
    }

    @GetMapping("/{approvalRef}/verify")
    public ResponseEntity<BaseResponse> verify(
            @RequestHeader(value = "X-Backoffice-Admin-Key", required = false) String adminKey,
            @PathVariable("approvalRef") String approvalRef,
            @RequestParam("productCode") String productCode,
            @RequestParam("operationType") String operationType) {
        return new ResponseEntity<>(approvalService.verify(adminKey, approvalRef, productCode, operationType), HttpStatus.OK);
    }

    @PostMapping("/{approvalRef}/consume")
    public ResponseEntity<BaseResponse> consume(
            @RequestHeader(value = "X-Backoffice-Admin-Key", required = false) String adminKey,
            @PathVariable("approvalRef") String approvalRef,
            @RequestParam("productCode") String productCode,
            @RequestParam("operationType") String operationType) {
        return new ResponseEntity<>(approvalService.consume(adminKey, approvalRef, productCode, operationType), HttpStatus.OK);
    }
}
