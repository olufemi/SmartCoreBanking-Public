package com.smart.core.centralized.wallet.profilings.controllers;

import com.smart.core.centralized.wallet.profilings.model.BackofficePortalLoginRequest;
import com.smart.core.centralized.wallet.profilings.model.BaseResponse;
import com.smart.core.centralized.wallet.profilings.services.BackofficePortalAuthService;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/backoffice/portal/auth")
public class BackofficePortalAuthController {

    private final BackofficePortalAuthService portalAuthService;

    public BackofficePortalAuthController(BackofficePortalAuthService portalAuthService) {
        this.portalAuthService = portalAuthService;
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse> login(@RequestBody @Valid BackofficePortalLoginRequest request) {
        return new ResponseEntity<BaseResponse>(portalAuthService.login(request), HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<BaseResponse> me(@RequestHeader("Authorization") String authorization) {
        return new ResponseEntity<BaseResponse>(portalAuthService.me(authorization), HttpStatus.OK);
    }
}
