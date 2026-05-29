/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart.core.centralized.wallet.generalledger.v2.services;

/**
 *
 * @author SmartCore Contributors
 */
import com.smart.core.centralized.wallet.generalledger.domains.GlobalLimitConfig;
import com.smart.core.centralized.wallet.generalledger.domains.Onboarded;
import com.smart.core.centralized.wallet.generalledger.domains.ProcessorFailedTransInfo;
import com.smart.core.centralized.wallet.generalledger.domains.UserLimitConfig;
import com.smart.core.centralized.wallet.generalledger.repository.GlobalLimitConfigRepo;
import com.smart.core.centralized.wallet.generalledger.repository.OnboardedRepo;
import com.smart.core.centralized.wallet.generalledger.repository.ProcessorFailedTransInfoRepo;
import com.smart.core.centralized.wallet.generalledger.repository.UserLimitConfigRepo;
import com.smart.core.centralized.wallet.generalledger.utils.DecodedJWTToken;
import com.smart.core.centralized.wallet.generalledger.utils.GlobalMethods;
import com.smart.core.centralized.wallet.generalledger.v2.domains.LedgerWalletBalanceV2;
import com.smart.core.centralized.wallet.generalledger.v2.enumm.LegTypeV2;
import com.smart.core.centralized.wallet.generalledger.v2.enumm.PostingModeV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.BatchLedgerFullItemResultV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.BatchLedgerFullPostApiResponseV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.BatchLedgerFullPostRequestV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.BatchLedgerItemResultV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.BatchLedgerItemV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.BatchLedgerPostRequestV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.BatchLedgerPostResponseV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.BatchLegV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.LegResultV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.SingleLedgerPostRequestV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.SingleLedgerPostResponseV2;
import com.smart.core.centralized.wallet.generalledger.v2.repository.LedgerWalletBalanceV2Repo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class LedgerBatchServiceV2 {

    private final LedgerPostingV2Service postingV2;

    // v1 repos you already have
    private final OnboardedRepo onboardedRepo;
    private final UserLimitConfigRepo userLimitConfigRepo;
    private final GlobalLimitConfigRepo globalLimitConfigRepo;
    private final ProcessorFailedTransInfoRepo processorFailedTransInfoRepo;

    // v2 balance repo
    private final LedgerWalletBalanceV2Repo ledgerWalletBalanceV2Repo;

    public LedgerBatchServiceV2(
            LedgerPostingV2Service postingV2,
            OnboardedRepo onboardedRepo,
            UserLimitConfigRepo userLimitConfigRepo,
            GlobalLimitConfigRepo globalLimitConfigRepo,
            ProcessorFailedTransInfoRepo processorFailedTransInfoRepo,
            LedgerWalletBalanceV2Repo ledgerWalletBalanceV2Repo
    ) {
        this.postingV2 = postingV2;
        this.onboardedRepo = onboardedRepo;
        this.userLimitConfigRepo = userLimitConfigRepo;
        this.globalLimitConfigRepo = globalLimitConfigRepo;
        this.processorFailedTransInfoRepo = processorFailedTransInfoRepo;
        this.ledgerWalletBalanceV2Repo = ledgerWalletBalanceV2Repo;
    }

    @Transactional
    public BatchLedgerFullPostApiResponseV2 batchPost(BatchLedgerFullPostRequestV2 batchRq, String auth) {
        batchRq.setAllOrNothing(true);
        BatchLedgerFullPostApiResponseV2 resp = new BatchLedgerFullPostApiResponseV2();
        resp.setGroupRef(batchRq.getGroupRef());
        resp.setTotal(batchRq.getItems() == null ? 0 : batchRq.getItems().size());

        try {
            DecodedJWTToken decoded = DecodedJWTToken.getDecoded(auth);

            if (batchRq.getItems() == null || batchRq.getItems().isEmpty()) {
                resp.setStatusCode(400);
                resp.setDescription("Batch items is empty.");
                resp.setFailedCount(resp.getTotal());
                return resp;
            }

            // 1) PRE-VALIDATION
            List<BatchLedgerFullItemResultV2> preResults = new ArrayList<>();
            boolean anyPreFail = false;

            for (BatchLedgerItemV2 item : batchRq.getItems()) {
                BatchLedgerFullItemResultV2 r = validateSingle(item, decoded);
                preResults.add(r);

                if (r.getStatusCode() != 200) {
                    anyPreFail = true;
                    if (batchRq.isAllOrNothing()) {
                        break;
                    }
                }
            }

            if (batchRq.isAllOrNothing() && anyPreFail) {
                resp.setResults(preResults);
                resp.setSuccessCount(0);
                resp.setFailedCount(preResults.size());
                resp.setStatusCode(400);
                resp.setDescription("Batch validation failed. Nothing was posted.");
                return resp;
            }

            // 2) POSTING PHASE: one engine call, one transaction, one wallet-lock simulation.
            BatchLedgerPostRequestV2 postRq = toEngineBatchRequest(batchRq, decoded);
            BatchLedgerPostResponseV2 posted = postingV2.batchPost(postRq, decoded.productCode);

            List<BatchLedgerFullItemResultV2> finalResults = toFullResults(batchRq.getItems(), posted);
            int success = 0;
            int failed = 0;
            for (BatchLedgerFullItemResultV2 result : finalResults) {
                if (result.getStatusCode() == 200) {
                    success++;
                } else {
                    failed++;
                }
            }

            resp.setResults(finalResults);
            resp.setSuccessCount(success);
            resp.setFailedCount(failed);

            if (posted.getStatusCode() != 200) {
                resp.setStatusCode(posted.getStatusCode());
                resp.setDescription(posted.getDescription());
            } else if (batchRq.isAllOrNothing()) {
                resp.setStatusCode(200);
                resp.setDescription("Batch posted successfully.");
            } else {
                if (failed == 0) {
                    resp.setStatusCode(200);
                    resp.setDescription("Batch posted successfully.");
                } else if (success == 0) {
                    resp.setStatusCode(400);
                    resp.setDescription("Batch failed.");
                } else {
                    resp.setStatusCode(207);
                    resp.setDescription("Batch partially successful.");
                }
            }

            return resp;

        } catch (Exception ex) {
            ex.printStackTrace();
            resp.setStatusCode(500);
            resp.setDescription("An error occured,please try again");
            resp.setFailedCount(resp.getTotal());
            return resp;
        }
    }

    // -------------------------
    // VALIDATION (no ledger writes)
    // -------------------------
    private BatchLedgerFullItemResultV2 validateSingle(BatchLedgerItemV2 item, DecodedJWTToken decoded) {

        BatchLedgerFullItemResultV2 r = new BatchLedgerFullItemResultV2();
        r.setRequestRef(item.getRequestRef());
        r.setDirection(item.getDirection());
        r.setAccountNumber(item.getAccountNumber());
        r.setProductCode(item.getProductCode());
        r.setLegTag(item.getLegTag());

        String accountNumber = item.getAccountNumber();
        String productCode = item.getProductCode();

        // productCode must match caller
        if (decoded == null || decoded.productCode == null || !decoded.productCode.equals(productCode)) {
            return fail(r, "Invalid product code!", 400, accountNumber, decoded == null ? null : decoded.productCode, "v2-batch-post");
        }

        // must be onboarded (v1 behavior)
        List<Onboarded> onboarded = onboardedRepo.findByWalletNoProductCode(accountNumber, productCode);
        if (onboarded == null || onboarded.isEmpty()) {
            return fail(r, "Wallet Number does not exist", 400, accountNumber, decoded.productCode, "v2-batch-post");
        }

        // direction rules
        if ("DEBIT".equalsIgnoreCase(item.getDirection())) {
            return validateDebitLeg(item, decoded, onboarded.get(0), r);
        }
        if ("CREDIT".equalsIgnoreCase(item.getDirection())) {
            return validateCreditLeg(item, decoded, onboarded.get(0), r);
        }

        return fail(r, "Invalid direction (must be DEBIT or CREDIT).", 400, accountNumber, decoded.productCode, "v2-batch-post");
    }

    private BatchLedgerFullItemResultV2 validateDebitLeg(BatchLedgerItemV2 item, DecodedJWTToken decoded, Onboarded ob, BatchLedgerFullItemResultV2 r) {

        String accountNumber = item.getAccountNumber();

        if (!"Withdrawal".equals(item.getTransType())) {
            return fail(r, "Invalid transaction type!", 400, accountNumber, decoded.productCode, "v2-batch-debit");
        }

        BigDecimal fees = bd(item.getFees());
        BigDecimal amount = bd(item.getAmount());
        BigDecimal finalCharges = bd(item.getFinalCharges());

        // v1: finalCharges == amount + fees
        if (finalCharges.compareTo(amount.add(fees)) != 0) {
            return fail(r, "The Final-Charges is not equal to the total amount plus fees", 400, accountNumber, decoded.productCode, "v2-batch-debit");
        }

        // v2: must be funded for debit (wallet balance row must exist)
        String walletKey = LedgerWalletBalanceV2.walletKey(accountNumber, item.getProductCode());
        Optional<LedgerWalletBalanceV2> walletOpt = ledgerWalletBalanceV2Repo.findByAccountNumberProductCode(walletKey);
        if (!walletOpt.isPresent()) {
            return fail(r, "Wallet Number has not funded account!", 400, accountNumber, decoded.productCode, "v2-batch-debit");
        }

        // limits (same logic as your v1)
        GlobalLimitConfig lim = loadLimitsOrFail(ob.getPhoneNumbProductCode(), accountNumber, decoded.productCode, r, "v2-batch-debit");
        if (lim == null) {
            return r;
        }

        LedgerWalletBalanceV2 wallet = walletOpt.get();
        BigDecimal accountBal = nz(wallet.getBalance());
        BigDecimal totalTransAmt = amount.add(fees);
        BigDecimal minimumBalance = bd(lim.getMinimumBalance());

        if (accountBal.subtract(totalTransAmt).compareTo(minimumBalance) < 0) {
            return fail(r, "Sorry, minimum account balance must be: N" + minimumBalance, 400, accountNumber, decoded.productCode, "v2-batch-debit");
        }

        if (bd(lim.getWithdrawalSingleTransaction()).compareTo(totalTransAmt) < 0) {
            return fail(r, "Sorry, your single maximum withdrwal is: N" + lim.getWithdrawalSingleTransaction(), 400, accountNumber, decoded.productCode, "v2-batch-debit");
        }

        if (bd(lim.getWithdrawal()).compareTo(totalTransAmt) < 0) {
            return fail(r, "Sorry, your maximum withdrwal is: N" + lim.getWithdrawal(), 400, accountNumber, decoded.productCode, "v2-batch-debit");
        }

        r.setStatusCode(200);
        r.setDescription("OK");
        return r;
    }

    private BatchLedgerFullItemResultV2 validateCreditLeg(BatchLedgerItemV2 item, DecodedJWTToken decoded, Onboarded ob, BatchLedgerFullItemResultV2 r) {

        String accountNumber = item.getAccountNumber();

        if (!"Deposit".equals(item.getTransType())) {
            return fail(r, "Invalid transaction type!", 400, accountNumber, decoded.productCode, "v2-batch-credit");
        }

        BigDecimal amount = bd(item.getAmount());

        GlobalLimitConfig lim = loadLimitsOrFail(ob.getPhoneNumbProductCode(), accountNumber, decoded.productCode, r, "v2-batch-credit");
        if (lim == null) {
            return r;
        }

        BigDecimal maxSingleDeposit = bd(lim.getWalletSingleDeposit());
        if (amount.compareTo(maxSingleDeposit) > 0) {
            return fail(r, "Sorry, your single maximum deposit is: N" + maxSingleDeposit, 400, accountNumber, decoded.productCode, "v2-batch-credit");
        }

        // max balance: use v2 wallet balance if exists else 0 (first funding allowed)
        BigDecimal currentBal = BigDecimal.ZERO;
        String walletKey = LedgerWalletBalanceV2.walletKey(accountNumber, item.getProductCode());
        Optional<LedgerWalletBalanceV2> walletOpt = ledgerWalletBalanceV2Repo.findByAccountNumberProductCode(walletKey);
        if (walletOpt.isPresent()) {
            currentBal = nz(walletOpt.get().getBalance());
        }

        BigDecimal maxBalance = bd(lim.getMaximumBalance());
        if (currentBal.add(amount).compareTo(maxBalance) > 0) {
            return fail(r, "Sorry, your maximum account balance is: N" + maxBalance, 400, accountNumber, decoded.productCode, "v2-batch-credit");
        }

        r.setStatusCode(200);
        r.setDescription("OK");
        return r;
    }

    private BatchLedgerFullItemResultV2 fail(BatchLedgerFullItemResultV2 r,
            String msg,
            int code,
            String accountNumber,
            String productCode,
            String operation) {

        ProcessorFailedTransInfo procFailedTrans = new ProcessorFailedTransInfo(
                operation,
                msg,
                String.valueOf(GlobalMethods.generateTransactionId()),
                accountNumber,
                "",
                "General-Ledger-Service",
                productCode
        );
        processorFailedTransInfoRepo.save(procFailedTrans);

        r.setStatusCode(code);
        r.setDescription(msg);
        return r;
    }

    private GlobalLimitConfig loadLimitsOrFail(
            String phoneNumbProductCodeLegacyKey,
            String accountNumber,
            String productCode,
            BatchLedgerFullItemResultV2 r,
            String op) {

        List<UserLimitConfig> userLimit = userLimitConfigRepo.findByPhoneNumberProductCode(phoneNumbProductCodeLegacyKey);
        if (userLimit == null || userLimit.isEmpty()) {
            fail(r, "User limit config not found for wallet.", 400, accountNumber, productCode, op);
            return null;
        }

        List<GlobalLimitConfig> gl = globalLimitConfigRepo.findByLimitCategory(userLimit.get(0).getTierCategory());
        if (gl == null || gl.isEmpty()) {
            fail(r, "Global limit config not found for tier.", 400, accountNumber, productCode, op);
            return null;
        }

        return gl.get(0);
    }

    // -------------------------
    // MAPPING
    // -------------------------
    private SingleLedgerPostRequestV2 toSingleRequest(String groupRef, BatchLedgerItemV2 item, DecodedJWTToken decoded) {
        SingleLedgerPostRequestV2 v2 = new SingleLedgerPostRequestV2();
        v2.setGroupRef(groupRef);
        v2.setRequestRef(item.getRequestRef());
        v2.setProductCode(item.getProductCode());
        v2.setProductName(decoded.productName);
        v2.setAccountNumber(item.getAccountNumber());
        v2.setNarration(item.getNarration());
        v2.setTransType(item.getTransType());
        v2.setAmount(bd(item.getAmount()));
        v2.setFees(bd(item.getFees()));
        v2.setFinalCharges(bd(item.getFinalCharges()));
        v2.setDescription(item.getNarration());
        return v2;
    }

    private BatchLedgerPostRequestV2 toEngineBatchRequest(BatchLedgerFullPostRequestV2 batchRq, DecodedJWTToken decoded) {
        BatchLedgerPostRequestV2 rq = new BatchLedgerPostRequestV2();
        rq.setBatchRef(batchRq.getGroupRef());
        rq.setProductCode(decoded.productCode);
        rq.setProductName(decoded.productName);
        rq.setPostingMode(PostingModeV2.BALANCED);

        List<BatchLegV2> legs = new ArrayList<>();
        for (BatchLedgerItemV2 item : batchRq.getItems()) {
            BatchLegV2 leg = new BatchLegV2();
            leg.setLegRef(firstNonBlank(item.getLegTag(), item.getRequestRef()));
            leg.setRequestRef(item.getRequestRef());
            leg.setAccountNumber(item.getAccountNumber());
            leg.setProductCode(item.getProductCode());
            leg.setProductName(decoded.productName);
            leg.setLegType("DEBIT".equalsIgnoreCase(item.getDirection()) ? LegTypeV2.DEBIT : LegTypeV2.CREDIT);
            leg.setTransType(item.getTransType());
            leg.setAmount(bd(item.getAmount()));
            leg.setFees(bd(item.getFees()));
            leg.setFinalCharges(bd(item.getFinalCharges()));
            leg.setNarration(item.getNarration());
            leg.setDescription(item.getLegTag());
            legs.add(leg);
        }
        rq.setLegs(legs);
        return rq;
    }

    private List<BatchLedgerFullItemResultV2> toFullResults(List<BatchLedgerItemV2> items, BatchLedgerPostResponseV2 posted) {
        List<BatchLedgerFullItemResultV2> results = new ArrayList<>();
        Map<String, LegResultV2> byLegRef = new HashMap<>();
        if (posted.getLegResults() != null) {
            for (LegResultV2 lr : posted.getLegResults()) {
                byLegRef.put(lr.getLegRef(), lr);
            }
        }

        for (BatchLedgerItemV2 item : items) {
            String legRef = firstNonBlank(item.getLegTag(), item.getRequestRef());
            LegResultV2 lr = byLegRef.get(legRef);

            BatchLedgerFullItemResultV2 itemRes = new BatchLedgerFullItemResultV2();
            itemRes.setRequestRef(item.getRequestRef());
            itemRes.setDirection(item.getDirection());
            itemRes.setAccountNumber(item.getAccountNumber());
            itemRes.setProductCode(item.getProductCode());
            itemRes.setLegTag(item.getLegTag());
            itemRes.setStatusCode(lr == null ? posted.getStatusCode() : lr.getStatusCode());
            itemRes.setDescription(lr == null ? posted.getDescription() : lr.getDescription());
            if (lr != null && lr.getData() != null) {
                itemRes.setData(lr.getData());
            }
            results.add(itemRes);
        }

        return results;
    }

    // -------------------------
    // HELPERS
    // -------------------------
    private BatchLedgerItemResultV2 fail(BatchLedgerItemResultV2 r,
            String msg,
            int code,
            String accountNumber,
            String productCode,
            String operation) {

        ProcessorFailedTransInfo procFailedTrans = new ProcessorFailedTransInfo(
                operation,
                msg,
                String.valueOf(GlobalMethods.generateTransactionId()),
                accountNumber,
                "",
                "General-Ledger-Service",
                productCode
        );
        processorFailedTransInfoRepo.save(procFailedTrans);

        r.setStatusCode(code);
        r.setDescription(msg);
        return r;
    }

    private BigDecimal bd(String s) {
        if (s == null) {
            return BigDecimal.ZERO;
        }
        String t = s.trim();
        if (t.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(t);
    }

    private BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private String firstNonBlank(String a, String b) {
        return a == null || a.trim().isEmpty() ? b : a;
    }
}
