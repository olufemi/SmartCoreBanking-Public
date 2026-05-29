/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smart.core.centralized.wallet.generalledger.controllers;

import com.smart.core.centralized.wallet.generalledger.models.AddWalletNo;
import com.smart.core.centralized.wallet.generalledger.models.BaseResponse;
import com.smart.core.centralized.wallet.generalledger.models.CheckWallet;
import com.smart.core.centralized.wallet.generalledger.models.CreditWallet;
import com.smart.core.centralized.wallet.generalledger.models.RequestDebitWallet;
import com.smart.core.centralized.wallet.generalledger.models.UpgradeWalletNo;
import com.smart.core.centralized.wallet.generalledger.models.WalletInfo;
import com.smart.core.centralized.wallet.generalledger.services.GenLedgerUtilityService;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author SmartCore Contributors
 */
@RestController
//@RequestMapping("/generalledger")
@RequiredArgsConstructor
public class GeneralLedgeControllers {

    private final GenLedgerUtilityService genLedgerUtilityService;

    @PostMapping("/check-if-wallet-exists")
    public ResponseEntity<BaseResponse> checkWallet(@RequestHeader(value = "authorization", required = true) String auth,
            @RequestHeader(value = "channel", required = true) String channel, @RequestBody @Valid CheckWallet rq) {

        BaseResponse baseResponse = genLedgerUtilityService.checkIfWalletExists(rq, auth);
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    @PostMapping("/add-wallet-no")
    public ResponseEntity<BaseResponse> addWalletNo(@RequestHeader(value = "authorization", required = true) String auth,
            @RequestHeader(value = "channel", required = true) String channel, @RequestBody @Valid AddWalletNo rq) {

        BaseResponse baseResponse = genLedgerUtilityService.addWalletNo(rq, auth);
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    @PostMapping("/update-wallet-no-tier")
    public ResponseEntity<BaseResponse> updateWalletNoTier(@RequestHeader(value = "authorization", required = true) String auth,
            @RequestHeader(value = "channel", required = true) String channel, @RequestBody @Valid UpgradeWalletNo rq) {

        BaseResponse baseResponse = genLedgerUtilityService.updateWalletNoTier(rq, auth);
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    @PostMapping("/debit-wallet-account")
    public ResponseEntity<BaseResponse> saveGenLedgersDebitAccountOneTime(@RequestHeader(value = "authorization", required = true) String auth,
            @RequestHeader(value = "channel", required = true) String channel, @RequestBody @Valid RequestDebitWallet rq) {

        BaseResponse baseResponse = genLedgerUtilityService.saveGenLedgersDebitAccountOneTime(rq, auth);
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    @PostMapping("/credit-wallet-account")
    public ResponseEntity<BaseResponse> processCreditLedgerOneTime(@RequestHeader(value = "authorization", required = true) String auth,
            @RequestHeader(value = "channel", required = true) String channel, @RequestBody @Valid CreditWallet rq) {

        BaseResponse baseResponse = genLedgerUtilityService.processCreditLedgerOneTime(rq, auth);
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    @PostMapping("/get-account-info")
    public ResponseEntity<BaseResponse> getAccountBalance(@RequestHeader(value = "authorization", required = true) String auth,
            @RequestHeader(value = "channel", required = true) String channel, @RequestBody @Valid WalletInfo rq) {

        BaseResponse baseResponse = genLedgerUtilityService.getAccountBalance(rq, auth);
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    @PostMapping("/get-max-single-deposit")
    public ResponseEntity<BaseResponse> maxSingleDeposit(@RequestHeader(value = "authorization", required = true) String auth,
            @RequestHeader(value = "channel", required = true) String channel, @RequestBody @Valid WalletInfo rq) {

        BaseResponse baseResponse = genLedgerUtilityService.getMaxSingleDeposit(rq, auth);
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    @PostMapping("/get-max-account-bal")
    public ResponseEntity<BaseResponse> getMaxAcctBalance(@RequestHeader(value = "authorization", required = true) String auth,
            @RequestHeader(value = "channel", required = true) String channel, @RequestBody @Valid WalletInfo rq) {

        BaseResponse baseResponse = genLedgerUtilityService.getMaxAcctBalance(rq, auth);
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

}
