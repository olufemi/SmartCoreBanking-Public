/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart.core.centralized.wallet.generalledger.v2.services;

/**
 *
 * @author SmartCore Contributors
 */
import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.smart.core.centralized.wallet.generalledger.domains.GlobalLimitConfig;
import com.smart.core.centralized.wallet.generalledger.domains.Onboarded;
import com.smart.core.centralized.wallet.generalledger.domains.ProcessorFailedTransInfo;
import com.smart.core.centralized.wallet.generalledger.domains.UserLimitConfig;
import com.smart.core.centralized.wallet.generalledger.models.BaseResponse;
import com.smart.core.centralized.wallet.generalledger.models.CreditWallet;
import com.smart.core.centralized.wallet.generalledger.models.RequestDebitWallet;
import com.smart.core.centralized.wallet.generalledger.models.WalletInfo;
import com.smart.core.centralized.wallet.generalledger.proxies.UtilitiesProxy;
import com.smart.core.centralized.wallet.generalledger.repository.GlobalLimitConfigRepo;
import com.smart.core.centralized.wallet.generalledger.repository.OnboardedRepo;
import com.smart.core.centralized.wallet.generalledger.repository.ProcessorFailedTransInfoRepo;
import com.smart.core.centralized.wallet.generalledger.repository.UserLimitConfigRepo;
import com.smart.core.centralized.wallet.generalledger.utils.DecodedJWTToken;
import com.smart.core.centralized.wallet.generalledger.utils.GlobalMethods;
import com.smart.core.centralized.wallet.generalledger.utils.UttilityMethods;
import com.smart.core.centralized.wallet.generalledger.v2.domains.LedgerWalletBalanceV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.BatchCreditWalletRequestV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.BatchDebitWalletRequestV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.BatchLedgerCreditPostRequestV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.BatchLedgerItemResultV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.BatchLedgerPostApiResponseV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.BatchLedgerPostLineResultV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.BatchLedgerPostRequestV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.BatchLedgerPostResponseV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.SingleLedgerPostRequestV2;
import com.smart.core.centralized.wallet.generalledger.v2.models.SingleLedgerPostResponseV2;
import com.smart.core.centralized.wallet.generalledger.v2.repository.LedgerWalletBalanceV2Repo;
import com.google.gson.Gson;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class LedgerApiV2Service {

    // inject your repos + posting engine
    private final OnboardedRepo onboardedRepo;
    private final UserLimitConfigRepo userLimitConfigRepo;
    private final GlobalLimitConfigRepo globalLimitConfigRepo;
    private final LedgerWalletBalanceV2Repo ledgerWalletBalanceV2Repo;
    private final ProcessorFailedTransInfoRepo processorFailedTransInfoRepo;
    //private final LedgerPostingV2Service postingV2; // the batch/single engine
    private final LedgerPostingV2Service postingV2;

    public LedgerApiV2Service(OnboardedRepo onboardedRepo,
            UserLimitConfigRepo userLimitConfigRepo,
            GlobalLimitConfigRepo globalLimitConfigRepo,
            LedgerWalletBalanceV2Repo ledgerWalletBalanceV2Repo,
            ProcessorFailedTransInfoRepo processorFailedTransInfoRepo,
            LedgerPostingV2Service postingV2
    ) {
        this.onboardedRepo = onboardedRepo;
        this.userLimitConfigRepo = userLimitConfigRepo;
        this.globalLimitConfigRepo = globalLimitConfigRepo;
        this.ledgerWalletBalanceV2Repo = ledgerWalletBalanceV2Repo;
        this.processorFailedTransInfoRepo = processorFailedTransInfoRepo;
        this.postingV2 = postingV2;
    }

    /**
     * Batch Debit API - v2 - allOrNothing=true => throws to rollback if any
     * item fails after we start posting - allOrNothing=false => processes
     * independent items; each item has its own result
     */
    @Transactional
    public BatchLedgerPostApiResponseV2 batchDebit(BatchDebitWalletRequestV2 batchRq, String auth) {

        BatchLedgerPostApiResponseV2 resp = new BatchLedgerPostApiResponseV2();
        resp.setGroupRef(batchRq.getGroupRef());
        resp.setTotal(batchRq.getItems() == null ? 0 : batchRq.getItems().size());

        int statusCode = 400;

        try {
            DecodedJWTToken decoded = DecodedJWTToken.getDecoded(auth);

            // Pre-check: empty batch
            if (batchRq.getItems() == null || batchRq.getItems().isEmpty()) {
                resp.setStatusCode(400);
                resp.setDescription("Batch items is empty.");
                resp.setFailedCount(resp.getTotal());
                return resp;
            }

            // 1) PRE-VALIDATION PHASE (no writes)
            // If allOrNothing=true, we validate all lines first.
            // If any fail => return immediately without posting.
            List<BatchLedgerItemResultV2> preResults = new ArrayList<>();
            boolean anyPreFail = false;

            for (RequestDebitWallet rq : batchRq.getItems()) {
                BatchLedgerItemResultV2 r = validateSingleDebit(rq, decoded, statusCode);
                preResults.add(r);
                if (r.getStatusCode() != 200) {
                    anyPreFail = true;
                    if (batchRq.isAllOrNothing()) {
                        break;
                    }
                }
            }

            if (batchRq.isAllOrNothing() && anyPreFail) {
                // fail the entire batch without posting anything
                resp.setResults(preResults);
                resp.setSuccessCount(0);
                resp.setFailedCount(preResults.size());
                resp.setStatusCode(400);
                resp.setDescription("Batch validation failed. Nothing was posted.");
                return resp;
            }

            // 2) POSTING PHASE
            // If allOrNothing=false => we use each item’s validation result and proceed only for valid ones.
            // If allOrNothing=true and validation passed => post all, and any posting failure throws to rollback.
            List<BatchLedgerItemResultV2> finalResults = new ArrayList<>();
            int success = 0;
            int failed = 0;

            for (int i = 0; i < batchRq.getItems().size(); i++) {
                RequestDebitWallet rq = batchRq.getItems().get(i);
                BatchLedgerItemResultV2 pre = preResults.size() > i ? preResults.get(i) : null;

                // If partial mode and validation failed, keep failure and continue
                if (!batchRq.isAllOrNothing() && pre != null && pre.getStatusCode() != 200) {
                    finalResults.add(pre);
                    failed++;
                    continue;
                }

                // Build v2 posting request
                BigDecimal fees = bd(rq.getFees());
                BigDecimal amount = bd(rq.getTransAmount());
                BigDecimal finalCharges = bd(rq.getFinalCHarges());

                String accountNumber = rq.getPhoneNumber();
                SingleLedgerPostRequestV2 v2 = new SingleLedgerPostRequestV2();
                v2.setGroupRef(batchRq.getGroupRef());             // ties items together
                v2.setRequestRef(rq.getTransactionId());           // idempotency per item
                v2.setProductCode(rq.getProductCode());
                v2.setProductName(decoded.productName);
                v2.setAccountNumber(accountNumber);
                v2.setNarration(rq.getNarration());
                v2.setTransType(rq.getTransType());               // Withdrawal
                v2.setAmount(amount);
                v2.setFees(fees);
                v2.setFinalCharges(finalCharges);
                v2.setDescription(rq.getNarration());

                // Post via v2 engine (this should lock wallet row and write entry + balance atomically)
                SingleLedgerPostResponseV2 posted = postingV2.debit(v2, decoded.productCode);

                BatchLedgerItemResultV2 itemRes = new BatchLedgerItemResultV2();
                itemRes.setRequestRef(rq.getTransactionId());
                itemRes.setAccountNumber(accountNumber);
                itemRes.setProductCode(rq.getProductCode());
                itemRes.setStatusCode(posted.getStatusCode());
                itemRes.setDescription(posted.getDescription());
                if (posted.getData() != null) {
                    itemRes.setData(posted.getData());
                }

                // If allOrNothing and any posting failed => throw to rollback everything
                if (batchRq.isAllOrNothing() && posted.getStatusCode() != 200) {
                    throw new RuntimeException("Batch posting failed: " + posted.getDescription());
                }

                if (posted.getStatusCode() == 200) {
                    success++;
                } else {
                    failed++;
                }
                finalResults.add(itemRes);
            }

            resp.setResults(finalResults);
            resp.setSuccessCount(success);
            resp.setFailedCount(failed);

            if (batchRq.isAllOrNothing()) {
                resp.setStatusCode(200);
                resp.setDescription("Batch debit posted successfully.");
            } else {
                // partial mode summary
                if (failed == 0) {
                    resp.setStatusCode(200);
                    resp.setDescription("Batch debit posted successfully.");
                } else if (success == 0) {
                    resp.setStatusCode(400);
                    resp.setDescription("Batch debit failed.");
                } else {
                    resp.setStatusCode(207); // Multi-Status style (optional)
                    resp.setDescription("Batch debit partially successful.");
                }
            }

            return resp;

        } catch (Exception ex) {
            ex.printStackTrace();
            resp.setStatusCode(500);
            resp.setDescription("An error occured,please try again");
            return resp;
        }
    }

    /**
     * Your v1 validations translated as "validate only" (no writes). Returns
     * statusCode=200 if ok, otherwise 400 with message.
     */
    private BatchLedgerItemResultV2 validateSingleDebit(RequestDebitWallet rq, DecodedJWTToken decoded, int statusCode) {

        BatchLedgerItemResultV2 r = new BatchLedgerItemResultV2();
        r.setRequestRef(rq.getTransactionId());
        r.setAccountNumber(rq.getPhoneNumber());
        r.setProductCode(rq.getProductCode());

        final String accountNumber = rq.getPhoneNumber();

        // productCode must match caller
        if (!decoded.productCode.equals(rq.getProductCode())) {
            return fail(r, "Invalid product code!", statusCode, accountNumber, decoded.productCode, "v2-batch-debit");
        }

        // only Withdrawal
        if (!"Withdrawal".equals(rq.getTransType())) {
            return fail(r, "Invalid transaction type!", statusCode, accountNumber, decoded.productCode, "v2-batch-debit");
        }

        // ensure onboarded exists (v1 tables, legacy key)
        List<Onboarded> onboarded = onboardedRepo.findByWalletNoProductCode(accountNumber, rq.getProductCode());
        if (onboarded == null || onboarded.isEmpty()) {
            return fail(r, "Wallet Number does not exist", statusCode, accountNumber, decoded.productCode, "v2-batch-debit");
        }

        // finalCharges == amount + fees
        BigDecimal fees = bd(rq.getFees());
        BigDecimal amount = bd(rq.getTransAmount());
        BigDecimal finalCharges = bd(rq.getFinalCHarges());

        if (finalCharges.compareTo(amount.add(fees)) != 0) {
            return fail(r, "The Final-Charges is not equal to the total amount plus fees", 400, accountNumber, decoded.productCode, "v2-batch-debit");
        }

        // wallet must be funded (v2 wallet balance exists)
        String walletKey = LedgerWalletBalanceV2.walletKey(accountNumber, rq.getProductCode()); // acct:product
        Optional<LedgerWalletBalanceV2> walletOpt = ledgerWalletBalanceV2Repo.findByAccountNumberProductCode(walletKey);
        if (!walletOpt.isPresent()) {
            return fail(r, "Wallet Number has not funded account!", statusCode, accountNumber, decoded.productCode, "v2-batch-debit");
        }

        // limits (same as v1)
        List<UserLimitConfig> userLimit = userLimitConfigRepo.findByPhoneNumberProductCode(onboarded.get(0).getPhoneNumbProductCode());
        if (userLimit == null || userLimit.isEmpty()) {
            return fail(r, "User limit config not found for wallet.", 400, accountNumber, decoded.productCode, "v2-batch-debit");
        }

        List<GlobalLimitConfig> gl = globalLimitConfigRepo.findByLimitCategory(userLimit.get(0).getTierCategory());
        if (gl == null || gl.isEmpty()) {
            return fail(r, "Global limit config not found for tier.", 400, accountNumber, decoded.productCode, "v2-batch-debit");
        }

        GlobalLimitConfig getG = gl.get(0);

        LedgerWalletBalanceV2 wallet = walletOpt.get();
        BigDecimal accountBal = wallet.getBalance();
        BigDecimal totalTransAmt = amount.add(fees);
        BigDecimal minimumBalance = new BigDecimal(getG.getMinimumBalance());

        if (accountBal.subtract(totalTransAmt).compareTo(minimumBalance) < 0) {
            return fail(r, "Sorry, minimum account balance must be: N" + minimumBalance, 400, accountNumber, decoded.productCode, "v2-batch-debit");
        }

        if (new BigDecimal(getG.getWithdrawalSingleTransaction()).compareTo(totalTransAmt) < 0) {
            return fail(r, "Sorry, your single maximum withdrwal is: N" + getG.getWithdrawalSingleTransaction(), 400, accountNumber, decoded.productCode, "v2-batch-debit");
        }

        if (new BigDecimal(getG.getWithdrawal()).compareTo(totalTransAmt) < 0) {
            return fail(r, "Sorry, your maximum withdrwal is: N" + getG.getWithdrawal(), 400, accountNumber, decoded.productCode, "v2-batch-debit");
        }

        // ok
        r.setStatusCode(200);
        r.setDescription("OK");
        return r;
    }

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
        return new BigDecimal(s.trim());
    }

    @Transactional
    public BatchLedgerPostApiResponseV2 batchCredit(BatchCreditWalletRequestV2 batchRq, String auth) {

        BatchLedgerPostApiResponseV2 resp = new BatchLedgerPostApiResponseV2();
        resp.setGroupRef(batchRq.getGroupRef());
        resp.setTotal(batchRq.getItems() == null ? 0 : batchRq.getItems().size());

        int statusCode = 400;

        try {
            DecodedJWTToken decoded = DecodedJWTToken.getDecoded(auth);

            // Pre-check: empty batch
            if (batchRq.getItems() == null || batchRq.getItems().isEmpty()) {
                resp.setStatusCode(400);
                resp.setDescription("Batch items is empty.");
                resp.setFailedCount(resp.getTotal());
                return resp;
            }

            // 1) PRE-VALIDATION PHASE (no writes to ledger tables; only failure logs like v1)
            List<BatchLedgerItemResultV2> preResults = new ArrayList<>();
            boolean anyPreFail = false;

            for (CreditWallet rq : batchRq.getItems()) {
                BatchLedgerItemResultV2 r = validateSingleCredit(rq, decoded, statusCode);
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

            // 2) POSTING PHASE
            List<BatchLedgerItemResultV2> finalResults = new ArrayList<>();
            int success = 0;
            int failed = 0;

            for (int i = 0; i < batchRq.getItems().size(); i++) {

                CreditWallet rq = batchRq.getItems().get(i);
                BatchLedgerItemResultV2 pre = preResults.size() > i ? preResults.get(i) : null;

                // Partial mode: skip invalid ones
                if (!batchRq.isAllOrNothing() && pre != null && pre.getStatusCode() != 200) {
                    finalResults.add(pre);
                    failed++;
                    continue;
                }

                BigDecimal fees = bd(rq.getFees());
                BigDecimal amount = bd(rq.getTransAmount());
                BigDecimal finalCharges = bd(rq.getFinalCHarges()); // keep for payload symmetry; not used in credit calc unless you want
                if (fees.compareTo(BigDecimal.ZERO.add(fees)) != 0) {

                    resp.setStatusCode(400);
                    resp.setDescription("The Fees must be zero!");
                    return resp;

                }

                if (finalCharges.compareTo(amount) != 0) {
                    resp.setStatusCode(400);
                    resp.setDescription("The Final-Charges is not equal to the total amount!");
                    return resp;

                }
                String accountNumber = rq.getPhoneNumber();

                SingleLedgerPostRequestV2 v2 = new SingleLedgerPostRequestV2();
                v2.setGroupRef(batchRq.getGroupRef());
                v2.setRequestRef(rq.getTransactionId());
                v2.setProductCode(rq.getProductCode());
                v2.setProductName(decoded.productName);
                v2.setAccountNumber(accountNumber);
                v2.setNarration(rq.getNarration());
                v2.setTransType(rq.getTransType());     // Deposit
                v2.setAmount(amount);
                v2.setFees(fees);
                v2.setFinalCharges(finalCharges);
                v2.setDescription(rq.getNarration());

                SingleLedgerPostResponseV2 posted = postingV2.credit(v2, decoded.productCode);

                BatchLedgerItemResultV2 itemRes = new BatchLedgerItemResultV2();
                itemRes.setRequestRef(rq.getTransactionId());
                itemRes.setAccountNumber(accountNumber);
                itemRes.setProductCode(rq.getProductCode());
                itemRes.setStatusCode(posted.getStatusCode());
                itemRes.setDescription(posted.getDescription());
                if (posted.getData() != null) {
                    itemRes.setData(posted.getData());
                }

                // allOrNothing: any posting failure => throw to rollback everything
                if (batchRq.isAllOrNothing() && posted.getStatusCode() != 200) {
                    throw new RuntimeException("Batch posting failed: " + posted.getDescription());
                }

                if (posted.getStatusCode() == 200) {
                    success++;
                } else {
                    failed++;
                }

                finalResults.add(itemRes);
            }

            resp.setResults(finalResults);
            resp.setSuccessCount(success);
            resp.setFailedCount(failed);

            if (batchRq.isAllOrNothing()) {
                resp.setStatusCode(200);
                resp.setDescription("Batch credit posted successfully.");
            } else {
                if (failed == 0) {
                    resp.setStatusCode(200);
                    resp.setDescription("Batch credit posted successfully.");
                } else if (success == 0) {
                    resp.setStatusCode(400);
                    resp.setDescription("Batch credit failed.");
                } else {
                    resp.setStatusCode(207);
                    resp.setDescription("Batch credit partially successful.");
                }
            }

            return resp;

        } catch (Exception ex) {
            ex.printStackTrace();
            resp.setStatusCode(500);
            resp.setDescription("An error occured,please try again");
            return resp;
        }
    }

    /**
     * v1 credit validations translated as validate-only (no ledger writes).
     * Returns 200 OK else 400 + message.
     */
    private BatchLedgerItemResultV2 validateSingleCredit(CreditWallet rq, DecodedJWTToken decoded, int statusCode) {

        BatchLedgerItemResultV2 r = new BatchLedgerItemResultV2();
        r.setRequestRef(rq.getTransactionId());
        r.setAccountNumber(rq.getPhoneNumber());
        r.setProductCode(rq.getProductCode());

        final String accountNumber = rq.getPhoneNumber();

        // productCode must match caller
        if (!decoded.productCode.equals(rq.getProductCode())) {
            return fail(r, "Invalid product code!", statusCode, accountNumber, decoded.productCode, "v2-batch-credit");
        }

        // only Deposit
        if (!"Deposit".equals(rq.getTransType())) {
            return fail(r, "Invalid transaction type!", statusCode, accountNumber, decoded.productCode, "v2-batch-credit");
        }

        // ensure onboarded exists (v1 tables)
        List<Onboarded> onboarded = onboardedRepo.findByWalletNoProductCode(accountNumber, rq.getProductCode());
        if (onboarded == null || onboarded.isEmpty()) {
            return fail(r, "Wallet Number does not exist", statusCode, accountNumber, decoded.productCode, "v2-batch-credit");
        }

        // Parse amounts
        BigDecimal amount = bd(rq.getTransAmount());      // deposit amount (gross, like v1)
        BigDecimal fees = bd(rq.getFees());              // product fees
        // finalCharges is present in request but v1 doesn't validate equation; keep optional

        // Fetch limits for wallet
        List<UserLimitConfig> userLimit = userLimitConfigRepo.findByPhoneNumberProductCode(onboarded.get(0).getPhoneNumbProductCode());
        if (userLimit == null || userLimit.isEmpty()) {
            return fail(r, "User limit config not found for wallet.", 400, accountNumber, decoded.productCode, "v2-batch-credit");
        }

        List<GlobalLimitConfig> gl = globalLimitConfigRepo.findByLimitCategory(userLimit.get(0).getTierCategory());
        if (gl == null || gl.isEmpty()) {
            return fail(r, "Global limit config not found for tier.", 400, accountNumber, decoded.productCode, "v2-batch-credit");
        }

        GlobalLimitConfig getG = gl.get(0);

        // v1: single max deposit (compare transAmount only)
        BigDecimal maxSingleDeposit = new BigDecimal(getG.getWalletSingleDeposit());
        if (amount.compareTo(maxSingleDeposit) > 0) {
            return fail(r,
                    "Sorry, your single maximum deposit is: N" + maxSingleDeposit,
                    400, accountNumber, decoded.productCode, "v2-batch-credit");
        }

        // v1: max balance check uses (amount + accountBal) <= maximumBalance
        // In v2, the "source of truth" is LedgerWalletBalanceV2, but for credit:
        // - wallet row may not exist yet (first funding). treat balance as 0.
        BigDecimal currentBal = BigDecimal.ZERO;
        String walletKey = LedgerWalletBalanceV2.walletKey(accountNumber, rq.getProductCode());
        Optional<LedgerWalletBalanceV2> walletOpt = ledgerWalletBalanceV2Repo.findByAccountNumberProductCode(walletKey);
        if (walletOpt.isPresent() && walletOpt.get().getBalance() != null) {
            currentBal = walletOpt.get().getBalance();
        }

        BigDecimal maxBalance = new BigDecimal(getG.getMaximumBalance());
        if (currentBal.add(amount).compareTo(maxBalance) > 0) {
            return fail(r,
                    "Sorry, your maximum account balance is: N" + maxBalance,
                    400, accountNumber, decoded.productCode, "v2-batch-credit");
        }

        // OK
        r.setStatusCode(200);
        r.setDescription("OK");
        return r;
    }

}
