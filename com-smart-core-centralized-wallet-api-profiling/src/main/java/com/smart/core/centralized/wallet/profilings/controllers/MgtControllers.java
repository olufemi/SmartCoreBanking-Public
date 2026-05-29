/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smart.core.centralized.wallet.profilings.controllers;

import com.smart.core.centralized.wallet.profilings.model.BaseResponse;
import com.smart.core.centralized.wallet.profilings.model.InitiateForgetPwdDataUser;
import com.smart.core.centralized.wallet.profilings.model.UserDetailsRequest;
import com.smart.core.centralized.wallet.profilings.model.VerifyOtp;
import com.smart.core.centralized.wallet.profilings.services.UserServices;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author SmartCore Contributors
 */
@RestController
@RequestMapping("/usermgt")
@RequiredArgsConstructor
@Validated
public class MgtControllers {

    private final UserServices mgtServices;

    @ApiOperation(value = "Create User, The API will be consumed by all channels {Mobile}.", tags = "Manage Wallets Services")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success code, Description"),
        @ApiResponse(code = 400, message = "Validation Error code"),
        @ApiResponse(code = 201, message = "Accepted for processing"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 500, message = "Server end exception"),
        @ApiResponse(code = 404, message = "Resource not available")

    })
    @PostMapping("/create-user")
    public ResponseEntity<BaseResponse> createWalletNewUuid(
             @RequestBody @Valid UserDetailsRequest rq) {

        BaseResponse baseResponse = mgtServices.createNewUser(rq);
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "Initiate Forget PwdDataUser, The API will be consumed by all channels {Mobile}.", tags = "Manage Wallets Services")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success code, Description"),
        @ApiResponse(code = 400, message = "Validation Error code"),
        @ApiResponse(code = 201, message = "Accepted for processing"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 500, message = "Server end exception"),
        @ApiResponse(code = 404, message = "Resource not available")

    })
    @PostMapping("/initiate-forget-password")
    public ResponseEntity<BaseResponse> initiateForgetPwdDataUser(
            @RequestBody @Valid InitiateForgetPwdDataUser rq) {

        BaseResponse baseResponse = mgtServices.initiateForgetPwdDataUser(rq);
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "Verif Forget Password, The API will be consumed by all channels {Mobile}.", tags = "Manage Wallets Services")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success code, Description"),
        @ApiResponse(code = 400, message = "Validation Error code"),
        @ApiResponse(code = 201, message = "Accepted for processing"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 500, message = "Server end exception"),
        @ApiResponse(code = 404, message = "Resource not available")

    })
    @PostMapping("/verify-forget-pwd")
    public ResponseEntity<BaseResponse> verifForgetPwd(
           // @RequestHeader(value = "channel", required = true) String channel, 
            @RequestBody @Valid VerifyOtp rq) {

        BaseResponse baseResponse = mgtServices.verifForgetPwd(rq);
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

}
