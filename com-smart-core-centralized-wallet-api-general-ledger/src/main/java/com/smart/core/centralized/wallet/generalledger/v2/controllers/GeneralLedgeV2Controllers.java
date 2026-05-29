/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart.core.centralized.wallet.generalledger.v2.controllers;

import com.smart.core.centralized.wallet.generalledger.models.AddWalletNo;
import com.smart.core.centralized.wallet.generalledger.models.BaseResponse;
import com.smart.core.centralized.wallet.generalledger.models.CheckWallet;
import com.smart.core.centralized.wallet.generalledger.models.CreditWallet;
import com.smart.core.centralized.wallet.generalledger.models.RequestDebitWallet;
import com.smart.core.centralized.wallet.generalledger.models.UpgradeWalletNo;
import com.smart.core.centralized.wallet.generalledger.models.WalletInfo;
import com.smart.core.centralized.wallet.generalledger.services.GenLedgerUtilityService;
import com.smart.core.centralized.wallet.generalledger.v2.models.BatchCreditWalletRequestV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.BatchDebitWalletRequestV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.BatchLedgerFullPostApiResponseV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.BatchLedgerFullPostRequestV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.BatchLedgerPostApiResponseV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.LedgerReconciliationRequest;
import com.smart.core.centralized.wallet.generalledger.v2.models.LedgerReconciliationResponse;
import com.smart.core.centralized.wallet.generalledger.v2.models.LedgerReversalRequest;
import com.smart.core.centralized.wallet.generalledger.v2.models.LedgerReversalResponse;
import com.smart.core.centralized.wallet.generalledger.v2.services.LedgerReconciliationService;
import com.smart.core.centralized.wallet.generalledger.v2.services.LedgerReversalService;
import com.smart.core.centralized.wallet.generalledger.v2.services.LedgerApiV2Service;
import com.smart.core.centralized.wallet.generalledger.v2.services.LedgerBatchServiceV2;
import com.smart.core.centralized.wallet.generalledger.v2.services.LedgerV2FacadeService;
import javax.servlet.http.HttpServletRequest;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@RequestMapping("/v2")
public class GeneralLedgeV2Controllers {

    private final LedgerV2FacadeService genLedgerUtilityService;
    private final LedgerApiV2Service ledgerApiV2Service;
    private final LedgerBatchServiceV2 batchService;
    private final LedgerReversalService reversalService;
    private final LedgerReconciliationService reconciliationService;

    public GeneralLedgeV2Controllers(LedgerV2FacadeService genLedgerUtilityService,
            LedgerApiV2Service ledgerApiV2Service,
            LedgerBatchServiceV2 batchService,
            LedgerReversalService reversalService,
            LedgerReconciliationService reconciliationService) {
        this.genLedgerUtilityService = genLedgerUtilityService;
        this.ledgerApiV2Service = ledgerApiV2Service;
        this.batchService = batchService;
        this.reversalService = reversalService;
        this.reconciliationService = reconciliationService;

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

    @PostMapping(value = "/batch-post", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BatchLedgerFullPostApiResponseV2 batchPost(
            @RequestBody BatchLedgerFullPostRequestV2 rq,
            HttpServletRequest httpReq) throws Exception {

        String auth = httpReq.getHeader("Authorization");
        return batchService.batchPost(rq, auth);
    }

    @PostMapping(value = "/batch/debit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BatchLedgerPostApiResponseV2 batchDebit(@Valid @RequestBody BatchDebitWalletRequestV2 rq,
            @RequestHeader("Authorization") String auth) {
        return ledgerApiV2Service.batchDebit(rq, auth);
    }

    @PostMapping(value = "/batch/credit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BatchLedgerPostApiResponseV2 batchCebit(@Valid @RequestBody BatchCreditWalletRequestV2 rq,
            @RequestHeader("Authorization") String auth) {
        return ledgerApiV2Service.batchCredit(rq, auth);
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

    @PostMapping(value = "/reverse", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public LedgerReversalResponse reverse(@RequestHeader(value = "Authorization", required = true) String auth,
            @RequestBody @Valid LedgerReversalRequest rq) {
        return reversalService.reverse(rq, auth);
    }

    @PostMapping(value = "/reconcile", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public LedgerReconciliationResponse reconcile(@RequestHeader(value = "Authorization", required = true) String auth,
            @RequestBody LedgerReconciliationRequest rq) {
        return reconciliationService.reconcile(rq, auth);
    }

}
