package com.smart.core.centralized.wallet.profilings.controllers;

import com.smart.core.centralized.wallet.profilings.model.BackofficePasswordResetRequest;
import com.smart.core.centralized.wallet.profilings.model.BackofficeRoleRequest;
import com.smart.core.centralized.wallet.profilings.model.BackofficeUserRequest;
import com.smart.core.centralized.wallet.profilings.model.BaseResponse;
import com.smart.core.centralized.wallet.profilings.services.BackofficeAdminService;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/backoffice/admin")
public class BackofficeAdminController {

    private final BackofficeAdminService adminService;

    public BackofficeAdminController(BackofficeAdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/roles")
    public ResponseEntity<BaseResponse> createRole(
            @RequestHeader(value = "X-Backoffice-Admin-Key", required = false) String adminKey,
            @RequestHeader(value = "X-Actor", required = false) String actor,
            @RequestBody @Valid BackofficeRoleRequest request) {
        return new ResponseEntity<BaseResponse>(adminService.createRole(adminKey, actor, request), HttpStatus.OK);
    }

    @GetMapping("/roles")
    public ResponseEntity<BaseResponse> listRoles(
            @RequestHeader(value = "X-Backoffice-Admin-Key", required = false) String adminKey) {
        return new ResponseEntity<BaseResponse>(adminService.listRoles(adminKey), HttpStatus.OK);
    }

    @PostMapping("/operators")
    public ResponseEntity<BaseResponse> createUser(
            @RequestHeader(value = "X-Backoffice-Admin-Key", required = false) String adminKey,
            @RequestHeader(value = "X-Actor", required = false) String actor,
            @RequestBody @Valid BackofficeUserRequest request) {
        return new ResponseEntity<BaseResponse>(adminService.createUser(adminKey, actor, request), HttpStatus.OK);
    }

    @GetMapping("/operators")
    public ResponseEntity<BaseResponse> listUsers(
            @RequestHeader(value = "X-Backoffice-Admin-Key", required = false) String adminKey,
            @RequestParam(value = "productCode", required = false) String productCode) {
        return new ResponseEntity<BaseResponse>(adminService.listUsers(adminKey, productCode), HttpStatus.OK);
    }

    @PostMapping("/operators/password")
    public ResponseEntity<BaseResponse> resetPassword(
            @RequestHeader(value = "X-Backoffice-Admin-Key", required = false) String adminKey,
            @RequestHeader(value = "X-Actor", required = false) String actor,
            @RequestBody @Valid BackofficePasswordResetRequest request) {
        return new ResponseEntity<BaseResponse>(adminService.setUserPassword(adminKey, actor, request), HttpStatus.OK);
    }
}
