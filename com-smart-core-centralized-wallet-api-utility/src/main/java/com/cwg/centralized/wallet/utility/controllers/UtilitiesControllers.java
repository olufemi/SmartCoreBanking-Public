/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwg.centralized.wallet.utility.controllers;

import com.cwg.centralized.wallet.utility.models.AddNewUserToLimit;
import com.cwg.centralized.wallet.utility.models.ApiResponseModel;
import com.cwg.centralized.wallet.utility.models.AuthUserRequest;
import com.cwg.centralized.wallet.utility.models.BaseResponse;
import com.cwg.centralized.wallet.utility.models.CheckUserLimit;
import com.cwg.centralized.wallet.utility.models.UpgradeUserToLimit;
import com.cwg.centralized.wallet.utility.services.UserServices;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
@RequestMapping("/utilities")
@RequiredArgsConstructor
public class UtilitiesControllers {

    private final UserServices userServices;

    @ApiOperation(value = "This is an internally consumed API, not avaialble on channels.", tags = "Utilities Services")
    @PostMapping("/walletmgt/add-tier-to-wallet")
    public ResponseEntity<BaseResponse> addTierToWallet(@RequestBody @Valid AddNewUserToLimit rq) {

        BaseResponse baseResponse = userServices.addTierToWallet(rq);
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    @PostMapping("/walletmgt/update-wallet-tier")
    public ResponseEntity<BaseResponse> updateWalletTier(@RequestBody @Valid UpgradeUserToLimit rq) {

        BaseResponse baseResponse = userServices.upGradeWalletTier(rq);
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    @PostMapping("/walletmgt/get-maximum-account-balance-limit")
    public ResponseEntity<BaseResponse> getMaxAcctBal(@RequestBody @Valid CheckUserLimit rq) {

        BaseResponse baseResponse = userServices.getMaxAcctBal(rq);
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    @PostMapping("/usermgt/user")
    public ResponseEntity<BaseResponse> authClientAdmin(
            @RequestHeader(value = "channel", required = true) String channel, @RequestBody @Valid AuthUserRequest rq) {

        BaseResponse baseResponse = userServices.authenticateUserAdmin(rq, channel);
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    /*@GetMapping("/get-limits")
    public ResponseEntity<ApiResponseModel> getLimitLists() {

        ApiResponseModel apiResponseModel = userServices.getLimitLists();
        return new ResponseEntity<>(apiResponseModel, HttpStatus.OK);
    }*/
}
