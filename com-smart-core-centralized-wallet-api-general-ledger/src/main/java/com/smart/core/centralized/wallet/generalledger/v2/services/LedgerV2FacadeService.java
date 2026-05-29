/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart.core.centralized.wallet.generalledger.v2.services;

/**
 *
 * @author SmartCore Contributors
 */
import com.smart.core.centralized.wallet.generalledger.domains.GenLedgAccountCum;
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
import org.springframework.stereotype.Service;

@Service
public class LedgerV2FacadeService {

    private final LedgerPostingV2Service postingV2; // the batch/single engine
    private final OnboardedRepo onboardedRepo;      // your existing
    private final UtilitiesProxy utilitiesProxy;    // your existing (optional for tier init)
    private final UserLimitConfigRepo userLimitConfigRepo;      // your existing
    private final GlobalLimitConfigRepo globalLimitConfigRepo;  // your existing
    private final ProcessorFailedTransInfoRepo processorFailedTransInfoRepo; // your existing
    private final LedgerWalletBalanceV2Repo ledgerWalletBalanceV2Repo;
    private final UttilityMethods utilMeth;

    public LedgerV2FacadeService(LedgerPostingV2Service postingV2,
            OnboardedRepo onboardedRepo,
            UtilitiesProxy utilitiesProxy,
            UserLimitConfigRepo userLimitConfigRepo,
            GlobalLimitConfigRepo globalLimitConfigRepo,
            ProcessorFailedTransInfoRepo processorFailedTransInfoRepo,
            LedgerWalletBalanceV2Repo ledgerWalletBalanceV2Repo,
            UttilityMethods utilMeth) {
        this.postingV2 = postingV2;
        this.onboardedRepo = onboardedRepo;
        this.utilitiesProxy = utilitiesProxy;
        this.userLimitConfigRepo = userLimitConfigRepo;
        this.globalLimitConfigRepo = globalLimitConfigRepo;
        this.processorFailedTransInfoRepo = processorFailedTransInfoRepo;
        this.ledgerWalletBalanceV2Repo = ledgerWalletBalanceV2Repo;
        this.utilMeth = utilMeth;
    }

    private BaseResponse bad(String func, String msg, int code, String accountNumber, String productCodeFromJwt) {
        BaseResponse r = new BaseResponse();
        r.setStatusCode(code);
        r.setDescription(msg);

        // keep your existing failed logging style
        try {
            ProcessorFailedTransInfo procFailedTrans = new ProcessorFailedTransInfo(
                    func,
                    msg,
                    String.valueOf(GlobalMethods.generateTransactionId()),
                    accountNumber,
                    "",
                    "General-Ledger-Service",
                    productCodeFromJwt
            );
            processorFailedTransInfoRepo.save(procFailedTrans);
        } catch (Exception ignore) {
        }

        return r;
    }

    private BigDecimal bd(String s) {
        if (s == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(s.trim());
    }

    public BaseResponse saveGenLedgersDebitAccountOneTime(RequestDebitWallet rq, String auth) {

        BaseResponse responseModel = new BaseResponse();
        int statusCode = 500;
        String statusMessage = "An error occured,please try again";

        try {
            statusCode = 400;

            DecodedJWTToken decoded = DecodedJWTToken.getDecoded(auth);

            // v1: productCode must match caller
            if (!decoded.productCode.equals(rq.getProductCode())) {
                return bad("v2-debit", "Invalid product code!", statusCode, rq.getPhoneNumber(), decoded.productCode);
            }

            // v1: only Withdrawal
            if (!"Withdrawal".equals(rq.getTransType())) {
                return bad("v2-debit", "Invalid transaction type!", statusCode, rq.getPhoneNumber(), decoded.productCode);
            }

            final String accountNumber = rq.getPhoneNumber(); // v1 field still phoneNumber; internally treat as accountNumber

            // v1: ensure onboarded wallet exists
            List<Onboarded> onboarded = onboardedRepo.findByWalletNoProductCode(accountNumber, rq.getProductCode());
            if (onboarded == null || onboarded.isEmpty()) {
                return bad("v2-debit", "Wallet Number does not exist", statusCode, accountNumber, decoded.productCode);
            }

            // v1: finalCharges == amount + fees
            BigDecimal fees = bd(rq.getFees());
            BigDecimal amount = bd(rq.getTransAmount());
            BigDecimal finalCharges = bd(rq.getFinalCHarges());

            if (finalCharges.compareTo(amount.add(fees)) != 0) {
                return bad("v2-debit",
                        "The Final-Charges is not equal to the total amount plus fees",
                        400, accountNumber, decoded.productCode);
            }

            // Balance, minimum-balance and limit checks are enforced inside the
            // posting engine while the wallet row is locked.
            SingleLedgerPostRequestV2 v2 = new SingleLedgerPostRequestV2();
            // idempotency: use client transactionId, or generate new if not safe
            v2.setRequestRef(rq.getTransactionId());
            v2.setProductCode(rq.getProductCode());
            v2.setProductName(decoded.productName);
            v2.setAccountNumber(accountNumber);
            v2.setNarration(rq.getNarration());
            v2.setTransType(rq.getTransType()); // Withdrawal
            v2.setAmount(amount);
            v2.setFees(fees);
            v2.setFinalCharges(finalCharges);
            v2.setDescription(rq.getNarration());

            // Call v2 engine
            SingleLedgerPostResponseV2 posted = postingV2.debit(v2, decoded.productCode);

            responseModel.setStatusCode(posted.getStatusCode());
            responseModel.setDescription(posted.getDescription());
            if (posted.getData() != null) {
                responseModel.setData(posted.getData());
            }

            return responseModel;

        } catch (Exception ex) {
            responseModel.setDescription(statusMessage);
            responseModel.setStatusCode(statusCode);
            ex.printStackTrace();
        }

        return responseModel;
    }

    public BaseResponse getAccountBalance(WalletInfo rq, String auth) {

        BaseResponse responseModel = new BaseResponse();
        int statusCode = 500;
        String statusMessage = "An error occured,please try again";

        try {
            statusCode = 400;
            DecodedJWTToken decoded = DecodedJWTToken.getDecoded(auth);

            if (!decoded.productCode.equals(rq.getProductCode())) {
                return bad("v2-balance", "Invalid product code!", statusCode, rq.getPhoneNumber(), decoded.productCode);
            }

            final String accountNumber = rq.getPhoneNumber();
            List<Onboarded> getOnbord = onboardedRepo.findByWalletNoProductCode(accountNumber, rq.getProductCode());
            if (getOnbord == null || getOnbord.isEmpty()) {
                return bad("v2-balance", "Wallet Number does not exist", statusCode, accountNumber, decoded.productCode);
            }

            String walletKey = LedgerWalletBalanceV2.walletKey(accountNumber, rq.getProductCode());
            java.util.Optional<LedgerWalletBalanceV2> wallet
                    = ledgerWalletBalanceV2Repo.findByAccountNumberProductCode(walletKey);

            if (!wallet.isPresent()) {
                responseModel.addData("accountBalance", BigDecimal.ZERO);
                responseModel.addData("accountNumber", accountNumber);
                responseModel.addData("productCode", decoded.productCode);
                responseModel.setDescription("Wallet Number exists, but account not yet funded.");
                responseModel.setStatusCode(200);
                return responseModel;
            }

            BigDecimal bal = wallet.get().getBalance();

            responseModel.addData("accountBalance", bal);
            responseModel.addData("accountNumber", accountNumber);
            responseModel.addData("productCode", decoded.productCode);
            responseModel.setDescription("Wallet Number exists.");
            responseModel.setStatusCode(200);
            return responseModel;

        } catch (Exception ex) {
            responseModel.setDescription(statusMessage);
            responseModel.setStatusCode(statusCode);
            ex.printStackTrace();
        }

        return responseModel;
    }

    private LedgerWalletBalanceV2 findWalletMigrationSafe(String accountNumber, String productCode) {

        String v2Key = accountNumber + ":" + productCode;
        String legacyKey = accountNumber + productCode;

        return ledgerWalletBalanceV2Repo.findByAccountNumberProductCode(v2Key)
                .orElseGet(()
                        -> ledgerWalletBalanceV2Repo
                        .findByAccountNumberProductCode(legacyKey)
                        .orElse(null)
                );
    }

    public BaseResponse processCreditLedgerOneTime(CreditWallet rq, String auth) {

        BaseResponse responseModel = new BaseResponse();
        int statusCode = 500;
        String statusMessage = "An error occured,please try again";

        try {
            statusCode = 400;

            DecodedJWTToken decoded = DecodedJWTToken.getDecoded(auth);

            if (!decoded.productCode.equals(rq.getProductCode())) {
                return bad("v2-credit", "Invalid product code!", statusCode, rq.getPhoneNumber(), decoded.productCode);
            }

            if (!"Deposit".equals(rq.getTransType())) {
                return bad("v2-credit", "Invalid transaction type!", statusCode, rq.getPhoneNumber(), decoded.productCode);
            }

            final String accountNumber = rq.getPhoneNumber();

            // ensure onboarded
            List<Onboarded> onboarded = onboardedRepo.findByWalletNoProductCode(accountNumber, rq.getProductCode());
            if (onboarded == null || onboarded.isEmpty()) {
                return bad("v2-credit", "Wallet Number does not exist", statusCode, accountNumber, decoded.productCode);
            }

            // v1: finalCharges == amount + fees
            BigDecimal fees = bd(rq.getFees());
            if (fees.compareTo(BigDecimal.ZERO) != 0) {
                return bad("v2-credit",
                        "The Fees must be zero!",
                        400, accountNumber, decoded.productCode);
            }
            BigDecimal amount = bd(rq.getTransAmount());
            BigDecimal finalCharges = bd(rq.getFinalCHarges());

            if (finalCharges.compareTo(amount) != 0) {
                return bad("v2-credit",
                        "The Final-Charges is not equal to the total amount!",
                        400, accountNumber, decoded.productCode);
            }

            // Deposit limit and max-balance checks are enforced inside the
            // posting engine while the wallet row is locked.
            SingleLedgerPostRequestV2 v2 = new SingleLedgerPostRequestV2();
            v2.setRequestRef(rq.getTransactionId()); // idempotency
            v2.setProductCode(rq.getProductCode());
            v2.setProductName(decoded.productName);
            v2.setAccountNumber(accountNumber);
            v2.setNarration(rq.getNarration());
            v2.setTransType(rq.getTransType()); // Deposit
            v2.setAmount(amount);
            v2.setFees(fees);
            v2.setFinalCharges(amount); // not used for credit, but safe

            SingleLedgerPostResponseV2 posted = postingV2.credit(v2, decoded.productCode);

            responseModel.setStatusCode(posted.getStatusCode());
            responseModel.setDescription(posted.getDescription());
            if (posted.getData() != null) {
                responseModel.setData(posted.getData());
            }

            return responseModel;

        } catch (Exception ex) {
            responseModel.setDescription(statusMessage);
            responseModel.setStatusCode(statusCode);
            ex.printStackTrace();
        }

        return responseModel;
    }

    public BaseResponse getMaxSingleDeposit(WalletInfo rq, String auth) {

        BaseResponse responseModel = new BaseResponse();
        int statusCode = 500;
        String statusMessage = "An error occured,please try again";

        try {
            statusCode = 400;

            DecodedJWTToken decoded = DecodedJWTToken.getDecoded(auth);

            if (!decoded.productCode.equals(rq.getProductCode())) {

                ProcessorFailedTransInfo procFailedTrans = new ProcessorFailedTransInfo(
                        "v2-getMaxSingleDeposit",
                        "Invalid product code!",
                        String.valueOf(GlobalMethods.generateTransactionId()),
                        rq.getPhoneNumber(),
                        "",
                        "General-Ledger-Service",
                        decoded.productCode
                );
                processorFailedTransInfoRepo.save(procFailedTrans);

                responseModel.setDescription("Invalid product code!");
                responseModel.setStatusCode(statusCode);
                return responseModel;
            }

            final String accountNumber = rq.getPhoneNumber();

            List<Onboarded> onboarded = onboardedRepo.findByWalletNoProductCode(accountNumber, rq.getProductCode());
            if (onboarded == null || onboarded.isEmpty()) {

                ProcessorFailedTransInfo procFailedTrans = new ProcessorFailedTransInfo(
                        "v2-getMaxSingleDeposit",
                        "Wallet Number does not exist!",
                        String.valueOf(GlobalMethods.generateTransactionId()),
                        accountNumber,
                        "",
                        "General-Ledger-Service",
                        decoded.productCode
                );
                processorFailedTransInfoRepo.save(procFailedTrans);

                responseModel.setDescription("Wallet Number does not exist");
                responseModel.setStatusCode(statusCode);
                return responseModel;
            }

            // v1 legacy key for limits (keep as-is)
            String legacyWalletKey = onboarded.get(0).getPhoneNumbProductCode(); // typically acct+product

            List<UserLimitConfig> userLimit = userLimitConfigRepo.findByPhoneNumberProductCode(legacyWalletKey);
            if (userLimit == null || userLimit.isEmpty()) {
                responseModel.setStatusCode(400);
                responseModel.setDescription("User limit config not found for wallet.");
                return responseModel;
            }

            List<GlobalLimitConfig> global = globalLimitConfigRepo.findByLimitCategory(userLimit.get(0).getTierCategory());
            if (global == null || global.isEmpty()) {
                responseModel.setStatusCode(400);
                responseModel.setDescription("Global limit config not found for tier.");
                return responseModel;
            }

            responseModel.addData("maxSingleDeposit", new BigDecimal(global.get(0).getWalletSingleDeposit()));
            responseModel.addData("accountNumber", accountNumber); // you can keep phoneNumber if you want old response key
            responseModel.addData("productCode", decoded.productCode);
            responseModel.setDescription("Wallet Number exists.");
            responseModel.setStatusCode(200);

        } catch (Exception ex) {
            responseModel.setDescription(statusMessage);
            responseModel.setStatusCode(statusCode);
            ex.printStackTrace();
        }

        return responseModel;
    }

    public BaseResponse getMaxAcctBalance(WalletInfo rq, String auth) {

        BaseResponse responseModel = new BaseResponse();
        int statusCode = 500;
        String statusMessage = "An error occured,please try again";

        try {
            statusCode = 400;

            DecodedJWTToken decoded = DecodedJWTToken.getDecoded(auth);

            if (!decoded.productCode.equals(rq.getProductCode())) {

                ProcessorFailedTransInfo procFailedTrans = new ProcessorFailedTransInfo(
                        "v2-getMaxAcctBalance",
                        "Invalid product code!",
                        String.valueOf(GlobalMethods.generateTransactionId()),
                        rq.getPhoneNumber(),
                        "",
                        "General-Ledger-Service",
                        decoded.productCode
                );
                processorFailedTransInfoRepo.save(procFailedTrans);

                responseModel.setDescription("Invalid product code!");
                responseModel.setStatusCode(statusCode);
                return responseModel;
            }

            final String accountNumber = rq.getPhoneNumber();

            List<Onboarded> onboarded = onboardedRepo.findByWalletNoProductCode(accountNumber, rq.getProductCode());
            if (onboarded == null || onboarded.isEmpty()) {

                ProcessorFailedTransInfo procFailedTrans = new ProcessorFailedTransInfo(
                        "v2-getMaxAcctBalance",
                        "Wallet Number does not exist!",
                        String.valueOf(GlobalMethods.generateTransactionId()),
                        accountNumber,
                        "",
                        "General-Ledger-Service",
                        decoded.productCode
                );
                processorFailedTransInfoRepo.save(procFailedTrans);

                responseModel.setDescription("Wallet Number does not exist");
                responseModel.setStatusCode(statusCode);
                return responseModel;
            }

            // v1 legacy key for limits (keep as-is)
            String legacyWalletKey = onboarded.get(0).getPhoneNumbProductCode(); // acct+product

            List<UserLimitConfig> userLimit = userLimitConfigRepo.findByPhoneNumberProductCode(legacyWalletKey);
            if (userLimit == null || userLimit.isEmpty()) {
                responseModel.setStatusCode(400);
                responseModel.setDescription("User limit config not found for wallet.");
                return responseModel;
            }

            List<GlobalLimitConfig> global = globalLimitConfigRepo.findByLimitCategory(userLimit.get(0).getTierCategory());
            if (global == null || global.isEmpty()) {
                responseModel.setStatusCode(400);
                responseModel.setDescription("Global limit config not found for tier.");
                return responseModel;
            }

            responseModel.addData("maxAcctBalance", new BigDecimal(global.get(0).getMaximumBalance()));
            responseModel.addData("accountNumber", accountNumber);
            responseModel.addData("productCode", decoded.productCode);
            responseModel.setDescription("Wallet Number exists.");
            responseModel.setStatusCode(200);

        } catch (Exception ex) {
            responseModel.setDescription(statusMessage);
            responseModel.setStatusCode(statusCode);
            ex.printStackTrace();
        }

        return responseModel;
    }

}
