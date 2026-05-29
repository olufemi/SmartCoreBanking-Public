/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smart.core.centralized.wallet.generalledger.services;

import com.smart.core.centralized.wallet.generalledger.domains.GenLedgAccount;
import com.smart.core.centralized.wallet.generalledger.domains.GenLedgAccountCum;
import com.smart.core.centralized.wallet.generalledger.domains.WalletFundSucInfo;
import com.smart.core.centralized.wallet.generalledger.domains.WalletFundingInfoCum;
import com.smart.core.centralized.wallet.generalledger.models.BaseResponse;
import com.smart.core.centralized.wallet.generalledger.models.ProcLedgerRequestCreditOneTime;
import com.smart.core.centralized.wallet.generalledger.models.ProcLedgerRequestDebitOneTime;
import com.smart.core.centralized.wallet.generalledger.repository.GenLedgAccountCumRepo;
import com.smart.core.centralized.wallet.generalledger.repository.GenLedgAccountRepo;
import com.smart.core.centralized.wallet.generalledger.repository.WalletFundingInfoCumRepo;
import com.smart.core.centralized.wallet.generalledger.repository.WalletFundingSucessInfoRepo;
import com.google.gson.Gson;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 * @author SmartCore Contributors
 */
@Service
public class GenLedgerServices {

    private final Logger logger = LoggerFactory.getLogger(GenLedgerServices.class);
    org.joda.time.format.DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
    java.time.format.DateTimeFormatter jodaFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    private final WalletFundingSucessInfoRepo walletFundingSucessInfoRepo;
    private final GenLedgAccountRepo _genLedgAccountRepo;
    private final GenLedgAccountCumRepo _genLedgAccountCumRepo;
    private final WalletFundingInfoCumRepo walletFundingInfoCumRepo;

    public GenLedgerServices(WalletFundingSucessInfoRepo walletFundingSucessInfoRepo, GenLedgAccountRepo _genLedgAccountRepo,
            GenLedgAccountCumRepo _genLedgAccountCumRepo, WalletFundingInfoCumRepo walletFundingInfoCumRepo) {
        this.walletFundingSucessInfoRepo = walletFundingSucessInfoRepo;
        this._genLedgAccountRepo = _genLedgAccountRepo;
        this._genLedgAccountCumRepo = _genLedgAccountCumRepo;
        this.walletFundingInfoCumRepo = walletFundingInfoCumRepo;

    }

    //saveGenLedgersDebitAccountOneTime
    public BaseResponse saveGenLedgersDebitAccountOneTime(
            ProcLedgerRequestDebitOneTime rq) {

        String transactionId = rq.getTransactionId();
        String phonenumber = rq.getPhonenumber();
        String description = rq.getDescription();
        String finalCharges = rq.getFinalCharges();
        String fees = rq.getFees();
        String narration = rq.getNarration();

        BaseResponse responseModel = new BaseResponse();
        int statusCode = 500;
        String statusMessage = "An error occured,please try again";
        try {
            statusCode = 400;

            BigDecimal accountCredit = BigDecimal.ZERO;
            BigDecimal accountDebitCum = BigDecimal.ZERO;
            BigDecimal swChargesCumGel = BigDecimal.ZERO;
            BigDecimal fMoneyChargesCumGel = BigDecimal.ZERO;
            BigDecimal balance = BigDecimal.ZERO;
            BigDecimal bookBalance = BigDecimal.ZERO;
            BigDecimal merchantBookedBalance = BigDecimal.ZERO;
            BigDecimal swChargesGel = BigDecimal.ZERO;
            BigDecimal accountCreditCum = accountCredit;

            BigDecimal pl_cum_AccountCredit = BigDecimal.ZERO;
            BigDecimal pl_cum_AccountDebit = BigDecimal.ZERO;
            BigDecimal pl_cum_swChargesGel = BigDecimal.ZERO;
            BigDecimal pl_cum_fMoneyChargesGel = BigDecimal.ZERO;
            BigDecimal productCodeFeeCum = BigDecimal.ZERO;

            BigDecimal balancePhnProCode = BigDecimal.ZERO;
            BigDecimal accountCreditCumPhnProCode = accountCredit;
            BigDecimal accountDebitCumPhnProCode = BigDecimal.ZERO;
            BigDecimal swChargesCumGelPhnProCode = BigDecimal.ZERO;
            BigDecimal fMoneyChargesCumGelPhnProCode = BigDecimal.ZERO;
            BigDecimal bookBalancePhnProCode = BigDecimal.ZERO;
            BigDecimal merchantBookedBalancePhnProCode = BigDecimal.ZERO;

            int countProductCodeTrans = 0;

            if (_genLedgAccountRepo.findTopByOrderByIdDesc() != null) {

                List<GenLedgAccount> getDeee = _genLedgAccountRepo.findByPhoneNumberProdCode(rq.getPhonenumber(), rq.getProductCode());

                if (getDeee.size() > 0) {

                    logger.info(String.format("Getting phonenumber to update Gen Ledger Cummulative", phonenumber));
                    GenLedgAccount genLedResult;
                    Optional<GenLedgAccount> getGenLedgerTrans = _genLedgAccountRepo.findByOptPhoneNumberProdCode(rq.getPhoneNumberProductCode());
                    genLedResult = getGenLedgerTrans.get();

                    accountCreditCumPhnProCode = genLedResult.getAccountCreditCum().add(accountCredit);

                    accountDebitCumPhnProCode = genLedResult.getAccountDebitCum().add(new BigDecimal(finalCharges));
                    swChargesCumGelPhnProCode = genLedResult.getSwChargesCum().add(swChargesGel);
                    fMoneyChargesCumGelPhnProCode = genLedResult.getDemoPayChargesCum().add(new BigDecimal(fees));
                    balancePhnProCode = genLedResult.getBalance().subtract(new BigDecimal(finalCharges));
                    // bookBalance = genLedResult.getBookBalance().add(new BigDecimal(finalCharges).subtract(new BigDecimal(fMoneyChargesGel)));
                    bookBalancePhnProCode = genLedResult.getBookBalance();
                    merchantBookedBalancePhnProCode = genLedResult.getMerchantBookedBalance().add(merchantBookedBalance);

                }

                if (_genLedgAccountRepo.existsByPhoneNumber(phonenumber)) {

                    logger.info(String.format("Getting phonenumber to update Gen Ledger Cummulative", phonenumber));
                    GenLedgAccount genLedResult;
                    Optional<GenLedgAccount> getGenLedgerTrans = _genLedgAccountRepo.findByPhoneNumber(phonenumber);
                    genLedResult = getGenLedgerTrans.get();

                    accountCreditCum = genLedResult.getAccountCreditCum().add(accountCredit);

                    accountDebitCum = genLedResult.getAccountDebitCum().add(new BigDecimal(finalCharges));
                    swChargesCumGel = genLedResult.getSwChargesCum().add(swChargesGel);
                    fMoneyChargesCumGel = genLedResult.getDemoPayChargesCum().add(new BigDecimal(fees));
                    balance = genLedResult.getBalance().subtract(new BigDecimal(finalCharges));
                    // bookBalance = genLedResult.getBookBalance().add(new BigDecimal(finalCharges).subtract(new BigDecimal(fMoneyChargesGel)));
                    bookBalance = genLedResult.getBookBalance();
                    merchantBookedBalance = genLedResult.getMerchantBookedBalance().add(merchantBookedBalance);

                }

                if (_genLedgAccountRepo.existsByProductCode(rq.getProductCode())) {

                    logger.info(String.format("Getting productCode to update Gen Ledger Cummulative"));
                    GenLedgAccount genLedResult;
                    Optional<GenLedgAccount> getGenLedgerTrans = _genLedgAccountRepo.findByProductCode(rq.getProductCode());
                    genLedResult = getGenLedgerTrans.get();
                    productCodeFeeCum = genLedResult.getDemoPayChargesCum().add(new BigDecimal(fees));
                    countProductCodeTrans = genLedResult.getCountProductCodeTrans() + 1;

                }

                logger.info(String.format("Data exist in GenLedgAccount Table"));

                GenLedgAccount genLedgAccount = _genLedgAccountRepo.findTopByOrderByIdDesc();
                pl_cum_AccountCredit = genLedgAccount.getPl_cum_AccountCredit().add(accountCredit);
                pl_cum_AccountDebit = genLedgAccount.getPl_cum_AccountDebit().add(new BigDecimal(finalCharges));
                pl_cum_swChargesGel = genLedgAccount.getPl_cum_swCharges().add(swChargesGel);
                pl_cum_fMoneyChargesGel = genLedgAccount.getPl_cum_fMoneyCharges().add(new BigDecimal(fees));

                //save to gen ledger...
                logger.info(String.format("Saving to General Ledger Table"));
                GenLedgAccount genLedger = new GenLedgAccount(transactionId, phonenumber, description,
                        accountCredit, balance, bookBalance, accountCreditCum,
                        pl_cum_AccountCredit,
                        new BigDecimal(finalCharges),
                        accountDebitCum, pl_cum_AccountDebit, swChargesGel, swChargesCumGel, pl_cum_swChargesGel,
                        new BigDecimal(fees), pl_cum_fMoneyChargesGel, fMoneyChargesCumGel,
                        narration, merchantBookedBalance, rq.getProductCode(), rq.getProductName(), countProductCodeTrans,
                        productCodeFeeCum, rq.getPhoneNumberProductCode(),
                        balancePhnProCode, accountCreditCumPhnProCode, accountDebitCumPhnProCode,
                        swChargesCumGelPhnProCode, fMoneyChargesCumGelPhnProCode, bookBalancePhnProCode, merchantBookedBalancePhnProCode);
                _genLedgAccountRepo.save(genLedger);

                //save to gen ledger cummulative...
                GenLedgAccountCum genLedgAccountCum;

                if (_genLedgAccountCumRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Saving to GenLedgAccountCum Table phoneNumber exists"));
                    Optional<GenLedgAccountCum> getRecordGenLedgAccountCumCum = _genLedgAccountCumRepo.findByPhoneNumber(phonenumber);
                    genLedgAccountCum = getRecordGenLedgAccountCumCum.get();
                    genLedgAccountCum.setCountSuccessTrans(genLedgAccountCum.getCountSuccessTrans() + 1);
                    genLedgAccountCum.setTotalAmountCredited(genLedgAccountCum.getTotalAmountCredited().add(accountCredit));
                    genLedgAccountCum.setTotalAmountDebited(genLedgAccountCum.getTotalAmountDebited().add(new BigDecimal(finalCharges)));
                    genLedgAccountCum.setTotalPayCharge(genLedgAccountCum.getTotalPayCharge().add(new BigDecimal(fees)));
                    genLedgAccountCum.setTotalSwCharges(genLedgAccountCum.getTotalSwCharges().add(swChargesGel));
                    genLedgAccountCum.setTotalBalance(genLedgAccountCum.getTotalBalance().subtract(new BigDecimal(finalCharges)));
                    genLedgAccountCum.setTotalBookBalance(genLedgAccountCum.getTotalBookBalance());
                    genLedgAccountCum.setTotalMerchantBookedBalance(genLedgAccountCum.getTotalMerchantBookedBalance());
                    _genLedgAccountCumRepo.save(genLedgAccountCum);

                }

                if (_genLedgAccountCumRepo.existsByPhnProductCode(rq.getPhonenumber() + rq.getProductCode())) {
                    logger.info(String.format("Saving to GenLedgAccountCum Table phone-productcode exists"));
                    Optional<GenLedgAccountCum> getRecordGenLedgAccountCumCum = _genLedgAccountCumRepo.findByPhnProductCode(rq.getPhonenumber() + rq.getProductCode());
                    genLedgAccountCum = getRecordGenLedgAccountCumCum.get();
                    genLedgAccountCum.setCountSuccTransPhnProCode(genLedgAccountCum.getCountSuccTransPhnProCode() + 1);
                    genLedgAccountCum.setTotalAmtCreditedPhnProCode(genLedgAccountCum.getTotalAmtCreditedPhnProCode().add(accountCredit));
                    genLedgAccountCum.setTotalAmtDebitedPhnProCode(genLedgAccountCum.getTotalAmtDebitedPhnProCode().add(new BigDecimal(finalCharges)));
                    genLedgAccountCum.setTotalPayChargePhnProCode(genLedgAccountCum.getTotalPayChargePhnProCode().add(new BigDecimal(fees)));
                    genLedgAccountCum.setTotalSwChargesPhnProCode(genLedgAccountCum.getTotalSwChargesPhnProCode().add(swChargesGel));
                    genLedgAccountCum.setTotalBalancePhnProCode(genLedgAccountCum.getTotalBalancePhnProCode().subtract(new BigDecimal(finalCharges)));
                    genLedgAccountCum.setTotalBookBalancePhnProCode(genLedgAccountCum.getTotalBalancePhnProCode());
                    genLedgAccountCum.setTotalMerBookedBalPhnProCode(genLedgAccountCum.getTotalMerBookedBalPhnProCode());
                    _genLedgAccountCumRepo.save(genLedgAccountCum);

                }

                responseModel.setDescription("Account debited sucessfully.");
                responseModel.setStatusCode(200);
            } else {
                responseModel.setDescription("Account does not exist in the Ledger.");
                responseModel.setStatusCode(statusCode);
            }

        } catch (Exception ex) {
            responseModel.setDescription(statusMessage);
            responseModel.setStatusCode(statusCode);
            ex.printStackTrace();
        }

        return responseModel;

    }

    //esccowGenLedgersDebitAccountOneTimePayerAwaitingReleasedOrRollBack
    public BaseResponse esccowGenLedgersDebitAccountOneTimePayerAwaitingReleasedOrRollBack(
            ProcLedgerRequestDebitOneTime rq) {

        String transactionId = rq.getTransactionId();
        String phonenumber = rq.getPhonenumber();
        String description = rq.getDescription();
        String finalCharges = rq.getFinalCharges();
        String fees = rq.getFees();
        String narration = rq.getNarration();

        BaseResponse responseModel = new BaseResponse();
        int statusCode = 500;
        String statusMessage = "An error occured,please try again";
        try {
            statusCode = 400;

            BigDecimal accountCredit = BigDecimal.ZERO;
            BigDecimal accountDebitCum = BigDecimal.ZERO;
            BigDecimal swChargesCumGel = BigDecimal.ZERO;
            BigDecimal fMoneyChargesCumGel = BigDecimal.ZERO;
            BigDecimal balance = BigDecimal.ZERO;
            BigDecimal bookBalance = BigDecimal.ZERO;
            BigDecimal swChargesGel = BigDecimal.ZERO;
            BigDecimal accountCreditCum = accountCredit;
            BigDecimal pl_cum_AccountCredit = BigDecimal.ZERO;
            BigDecimal pl_cum_AccountDebit = BigDecimal.ZERO;
            BigDecimal pl_cum_swChargesGel = BigDecimal.ZERO;
            BigDecimal pl_cum_fMoneyChargesGel = BigDecimal.ZERO;
            BigDecimal merchantBookedBalance = BigDecimal.ZERO;
            BigDecimal productCodeFeeCum = BigDecimal.ZERO;

            BigDecimal balancePhnProCode = BigDecimal.ZERO;
            BigDecimal accountCreditCumPhnProCode = accountCredit;
            BigDecimal accountDebitCumPhnProCode = BigDecimal.ZERO;
            BigDecimal swChargesCumGelPhnProCode = BigDecimal.ZERO;
            BigDecimal fMoneyChargesCumGelPhnProCode = BigDecimal.ZERO;
            BigDecimal bookBalancePhnProCode = BigDecimal.ZERO;
            BigDecimal merchantBookedBalancePhnProCode = BigDecimal.ZERO;

            int countProductCodeTrans = 0;

            if (_genLedgAccountRepo.findTopByOrderByIdDesc() != null) {

                List<GenLedgAccount> getDeee = _genLedgAccountRepo.findByPhoneNumberProdCode(rq.getPhonenumber(), rq.getProductCode());

                if (getDeee.size() > 0) {

                    logger.info(String.format("Getting phonenumber to update Gen Ledger Cummulative", phonenumber));
                    GenLedgAccount genLedResult;
                    Optional<GenLedgAccount> getGenLedgerTrans = _genLedgAccountRepo.findByOptPhoneNumberProdCode(rq.getPhoneNumberProductCode());
                    genLedResult = getGenLedgerTrans.get();

                    accountCreditCumPhnProCode = genLedResult.getAccountCreditCum().add(accountCredit);

                    accountDebitCumPhnProCode = genLedResult.getAccountDebitCum().add(new BigDecimal(finalCharges));
                    swChargesCumGelPhnProCode = genLedResult.getSwChargesCum().add(swChargesGel);
                    fMoneyChargesCumGelPhnProCode = genLedResult.getDemoPayChargesCum().add(new BigDecimal(fees));
                    balancePhnProCode = genLedResult.getBalance().subtract(new BigDecimal(finalCharges));
                    // bookBalance = genLedResult.getBookBalance().add(new BigDecimal(finalCharges).subtract(new BigDecimal(fMoneyChargesGel)));
                    bookBalancePhnProCode = genLedResult.getBookBalance();
                    merchantBookedBalancePhnProCode = genLedResult.getMerchantBookedBalance().add(merchantBookedBalance);

                }

                if (_genLedgAccountRepo.existsByPhoneNumber(phonenumber)) {

                    logger.info(String.format("Getting phonenumber to update Gen Ledger Cummulative", phonenumber));
                    GenLedgAccount genLedResult;
                    Optional<GenLedgAccount> getGenLedgerTrans = _genLedgAccountRepo.findByPhoneNumber(phonenumber);
                    genLedResult = getGenLedgerTrans.get();

                    accountCreditCum = genLedResult.getAccountCreditCum().add(accountCredit);

                    accountDebitCum = genLedResult.getAccountDebitCum().add(new BigDecimal(finalCharges));
                    swChargesCumGel = genLedResult.getSwChargesCum().add(swChargesGel);
                    fMoneyChargesCumGel = genLedResult.getDemoPayChargesCum().add(new BigDecimal(fees));
                    balance = genLedResult.getBalance().subtract(new BigDecimal(finalCharges));
                    bookBalance = genLedResult.getBookBalance().add(new BigDecimal(finalCharges).subtract(new BigDecimal(fees)));
                    merchantBookedBalance = genLedResult.getBookBalance().add(merchantBookedBalance);

                }

                if (_genLedgAccountRepo.existsByProductCode(rq.getProductCode())) {

                    logger.info(String.format("Getting productCode to update Gen Ledger Cummulative"));
                    GenLedgAccount genLedResult;
                    Optional<GenLedgAccount> getGenLedgerTrans = _genLedgAccountRepo.findByProductCode(rq.getProductCode());
                    genLedResult = getGenLedgerTrans.get();
                    productCodeFeeCum = genLedResult.getDemoPayChargesCum().add(new BigDecimal(fees));
                    countProductCodeTrans = genLedResult.getCountProductCodeTrans() + 1;

                }

                logger.info(String.format("Data exist in GenLedgAccount Table"));

                GenLedgAccount genLedgAccount = _genLedgAccountRepo.findTopByOrderByIdDesc();
                pl_cum_AccountCredit = genLedgAccount.getPl_cum_AccountCredit().add(accountCredit);
                pl_cum_AccountDebit = genLedgAccount.getPl_cum_AccountDebit().add(new BigDecimal(finalCharges));
                pl_cum_swChargesGel = genLedgAccount.getPl_cum_swCharges().add(swChargesGel);
                pl_cum_fMoneyChargesGel = genLedgAccount.getPl_cum_fMoneyCharges().add(new BigDecimal(fees));

                //save to gen ledger...
                logger.info(String.format("Saving to General Ledger Table"));
                GenLedgAccount genLedger = new GenLedgAccount(transactionId, phonenumber, description,
                        accountCredit, balance, bookBalance, accountCreditCum,
                        pl_cum_AccountCredit,
                        new BigDecimal(finalCharges),
                        accountDebitCum, pl_cum_AccountDebit, swChargesGel, swChargesCumGel, pl_cum_swChargesGel,
                        new BigDecimal(fees), pl_cum_fMoneyChargesGel,
                        fMoneyChargesCumGel, narration, merchantBookedBalance,
                        rq.getProductCode(), rq.getProductName(), countProductCodeTrans,
                        productCodeFeeCum, rq.getPhoneNumberProductCode(),
                        balancePhnProCode, accountCreditCumPhnProCode, accountDebitCumPhnProCode,
                        swChargesCumGelPhnProCode, fMoneyChargesCumGelPhnProCode, bookBalancePhnProCode, merchantBookedBalancePhnProCode);
                _genLedgAccountRepo.save(genLedger);

                //save to gen ledger cummulative...
                GenLedgAccountCum genLedgAccountCum;

                if (_genLedgAccountCumRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Saving to GenLedgAccountCum Table phoneNumber exists"));
                    Optional<GenLedgAccountCum> getRecordGenLedgAccountCumCum = _genLedgAccountCumRepo.findByPhoneNumber(phonenumber);
                    genLedgAccountCum = getRecordGenLedgAccountCumCum.get();
                    //on the awating final release I will increase count, wen they finally release I will not increase count
                    //if they roll back I will decrease count
                    genLedgAccountCum.setCountSuccessTrans(genLedgAccountCum.getCountSuccessTrans() + 1);
                    genLedgAccountCum.setTotalAmountCredited(genLedgAccountCum.getTotalAmountCredited().add(accountCredit));
                    genLedgAccountCum.setTotalAmountDebited(genLedgAccountCum.getTotalAmountDebited().add(new BigDecimal(finalCharges)));
                    genLedgAccountCum.setTotalPayCharge(genLedgAccountCum.getTotalPayCharge().add(new BigDecimal(fees)));
                    genLedgAccountCum.setTotalSwCharges(genLedgAccountCum.getTotalSwCharges().add(swChargesGel));
                    genLedgAccountCum.setTotalBalance(genLedgAccountCum.getTotalBalance().subtract(new BigDecimal(finalCharges)));
                    genLedgAccountCum.setTotalBookBalance(genLedgAccountCum.getTotalBookBalance().add(new BigDecimal(finalCharges).subtract(new BigDecimal(fees))));
                    genLedgAccountCum.setTotalMerchantBookedBalance(genLedgAccountCum.getTotalMerchantBookedBalance());

                    _genLedgAccountCumRepo.save(genLedgAccountCum);

                }

                if (_genLedgAccountCumRepo.existsByPhnProductCode(rq.getPhonenumber() + rq.getProductCode())) {
                    logger.info(String.format("Saving to GenLedgAccountCum Table phone-productcode exists"));
                    Optional<GenLedgAccountCum> getRecordGenLedgAccountCumCum = _genLedgAccountCumRepo.findByPhnProductCode(rq.getPhonenumber() + rq.getProductCode());
                    genLedgAccountCum = getRecordGenLedgAccountCumCum.get();
                    //on the awating final release I will increase count, wen they finally release I will not increase count
                    //if they roll back I will decrease count
                    genLedgAccountCum.setCountSuccTransPhnProCode(genLedgAccountCum.getCountSuccTransPhnProCode() + 1);
                    genLedgAccountCum.setTotalAmtCreditedPhnProCode(genLedgAccountCum.getTotalAmtCreditedPhnProCode().add(accountCredit));
                    genLedgAccountCum.setTotalAmtDebitedPhnProCode(genLedgAccountCum.getTotalAmtDebitedPhnProCode().add(new BigDecimal(finalCharges)));
                    genLedgAccountCum.setTotalPayChargePhnProCode(genLedgAccountCum.getTotalPayChargePhnProCode().add(new BigDecimal(fees)));
                    genLedgAccountCum.setTotalSwChargesPhnProCode(genLedgAccountCum.getTotalSwChargesPhnProCode().add(swChargesGel));
                    genLedgAccountCum.setTotalBalancePhnProCode(genLedgAccountCum.getTotalBalancePhnProCode().subtract(new BigDecimal(finalCharges)));
                    genLedgAccountCum.setTotalBookBalancePhnProCode(genLedgAccountCum.getTotalBookBalancePhnProCode().add(new BigDecimal(finalCharges).subtract(new BigDecimal(fees))));
                    genLedgAccountCum.setTotalMerBookedBalPhnProCode(genLedgAccountCum.getTotalMerBookedBalPhnProCode());

                    _genLedgAccountCumRepo.save(genLedgAccountCum);

                }

                responseModel.setDescription("Account debited sucessfully.");
                responseModel.setStatusCode(200);
            } else {
                responseModel.setDescription("Account does not exist in the Ledger.");
                responseModel.setStatusCode(statusCode);
            }

        } catch (Exception ex) {
            responseModel.setDescription(statusMessage);
            responseModel.setStatusCode(statusCode);
            ex.printStackTrace();
        }

        return responseModel;

    }

    //esccowGenLedgersDebitAccountOneTimePayerReleased
    public BaseResponse esccowGenLedgersDebitAccountOneTimePayerReleased(
            ProcLedgerRequestDebitOneTime rq) {

        String transactionId = rq.getTransactionId();
        String phonenumber = rq.getPhonenumber();
        String description = rq.getDescription();
        String finalCharges = "0";
        String fees = rq.getFees();
        String narration = rq.getNarration();

        BaseResponse responseModel = new BaseResponse();
        int statusCode = 500;
        String statusMessage = "An error occured,please try again";
        try {
            statusCode = 400;

            BigDecimal accountCredit = BigDecimal.ZERO;
            BigDecimal accountDebitCum = BigDecimal.ZERO;
            BigDecimal swChargesCumGel = BigDecimal.ZERO;
            BigDecimal fMoneyChargesCumGel = BigDecimal.ZERO;
            BigDecimal balance = BigDecimal.ZERO;
            BigDecimal bookBalance = BigDecimal.ZERO;
            BigDecimal swChargesGel = BigDecimal.ZERO;
            BigDecimal accountCreditCum = accountCredit;
            BigDecimal pl_cum_AccountCredit = BigDecimal.ZERO;
            BigDecimal pl_cum_AccountDebit = BigDecimal.ZERO;
            BigDecimal pl_cum_swChargesGel = BigDecimal.ZERO;
            BigDecimal pl_cum_fMoneyChargesGel = BigDecimal.ZERO;
            BigDecimal merchantBookedBalance = BigDecimal.ZERO;
            BigDecimal productCodeFeeCum = BigDecimal.ZERO;

            BigDecimal balancePhnProCode = BigDecimal.ZERO;
            BigDecimal accountCreditCumPhnProCode = accountCredit;
            BigDecimal accountDebitCumPhnProCode = BigDecimal.ZERO;
            BigDecimal swChargesCumGelPhnProCode = BigDecimal.ZERO;
            BigDecimal fMoneyChargesCumGelPhnProCode = BigDecimal.ZERO;
            BigDecimal bookBalancePhnProCode = BigDecimal.ZERO;
            BigDecimal merchantBookedBalancePhnProCode = BigDecimal.ZERO;

            int countProductCodeTrans = 0;

            if (_genLedgAccountRepo.findTopByOrderByIdDesc() != null) {
                List<GenLedgAccount> getDeee = _genLedgAccountRepo.findByPhoneNumberProdCode(rq.getPhonenumber(), rq.getProductCode());

                if (getDeee.size() > 0) {

                    logger.info(String.format("Getting phonenumber to update Gen Ledger Cummulative", phonenumber));
                    GenLedgAccount genLedResult;
                    Optional<GenLedgAccount> getGenLedgerTrans = _genLedgAccountRepo.findByOptPhoneNumberProdCode(rq.getPhoneNumberProductCode());
                    genLedResult = getGenLedgerTrans.get();

                    accountCreditCumPhnProCode = genLedResult.getAccountCreditCum().add(accountCredit);

                    accountDebitCumPhnProCode = genLedResult.getAccountDebitCum().add(new BigDecimal(finalCharges));
                    swChargesCumGelPhnProCode = genLedResult.getSwChargesCum().add(swChargesGel);
                    fMoneyChargesCumGelPhnProCode = genLedResult.getDemoPayChargesCum().add(new BigDecimal(fees));
                    balancePhnProCode = genLedResult.getBalance().subtract(new BigDecimal(finalCharges));
                    // bookBalance = genLedResult.getBookBalance().add(new BigDecimal(finalCharges).subtract(new BigDecimal(fMoneyChargesGel)));
                    bookBalancePhnProCode = genLedResult.getBookBalance();
                    merchantBookedBalancePhnProCode = genLedResult.getMerchantBookedBalance().add(merchantBookedBalance);

                }

                if (_genLedgAccountRepo.existsByPhoneNumber(phonenumber)) {

                    logger.info(String.format("Getting phonenumber to update Gen Ledger Cummulative", phonenumber));
                    GenLedgAccount genLedResult;
                    Optional<GenLedgAccount> getGenLedgerTrans = _genLedgAccountRepo.findByPhoneNumber(phonenumber);
                    genLedResult = getGenLedgerTrans.get();

                    accountCreditCum = genLedResult.getAccountCreditCum().add(accountCredit);

                    accountDebitCum = genLedResult.getAccountDebitCum().add(new BigDecimal(finalCharges));
                    swChargesCumGel = genLedResult.getSwChargesCum().add(swChargesGel);
                    fMoneyChargesCumGel = genLedResult.getDemoPayChargesCum().add(new BigDecimal(fees));
                    balance = genLedResult.getBalance().subtract(new BigDecimal(finalCharges));
                    bookBalance = genLedResult.getBookBalance().subtract(new BigDecimal(rq.getFinalCharges()));
                    merchantBookedBalance = genLedResult.getBookBalance().add(merchantBookedBalance);

                }

                if (_genLedgAccountRepo.existsByProductCode(rq.getProductCode())) {

                    logger.info(String.format("Getting productCode to update Gen Ledger Cummulative"));
                    GenLedgAccount genLedResult;
                    Optional<GenLedgAccount> getGenLedgerTrans = _genLedgAccountRepo.findByProductCode(rq.getProductCode());
                    genLedResult = getGenLedgerTrans.get();
                    productCodeFeeCum = genLedResult.getDemoPayChargesCum().add(new BigDecimal(fees));
                    countProductCodeTrans = genLedResult.getCountProductCodeTrans() + 1;

                }

                logger.info(String.format("Data exist in GenLedgAccount Table"));

                GenLedgAccount genLedgAccount = _genLedgAccountRepo.findTopByOrderByIdDesc();
                pl_cum_AccountCredit = genLedgAccount.getPl_cum_AccountCredit().add(accountCredit);
                pl_cum_AccountDebit = genLedgAccount.getPl_cum_AccountDebit().add(new BigDecimal(finalCharges));
                pl_cum_swChargesGel = genLedgAccount.getPl_cum_swCharges().add(swChargesGel);
                pl_cum_fMoneyChargesGel = genLedgAccount.getPl_cum_fMoneyCharges().add(new BigDecimal(fees));

                //save to gen ledger...
                logger.info(String.format("Saving to General Ledger Table"));
                GenLedgAccount genLedger = new GenLedgAccount(transactionId, phonenumber, description,
                        accountCredit, balance, bookBalance, accountCreditCum,
                        pl_cum_AccountCredit,
                        new BigDecimal(finalCharges),
                        accountDebitCum, pl_cum_AccountDebit, swChargesGel, swChargesCumGel, pl_cum_swChargesGel,
                        new BigDecimal(fees), pl_cum_fMoneyChargesGel, fMoneyChargesCumGel, narration,
                        merchantBookedBalance,
                        rq.getProductCode(), rq.getProductName(), countProductCodeTrans,
                        productCodeFeeCum, rq.getPhoneNumberProductCode(),
                        balancePhnProCode, accountCreditCumPhnProCode, accountDebitCumPhnProCode,
                        swChargesCumGelPhnProCode, fMoneyChargesCumGelPhnProCode, bookBalancePhnProCode, merchantBookedBalancePhnProCode);
                _genLedgAccountRepo.save(genLedger);

                //save to gen ledger cummulative...
                GenLedgAccountCum genLedgAccountCum;

                if (_genLedgAccountCumRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Saving to GenLedgAccountCum Table phoneNumber exists"));
                    Optional<GenLedgAccountCum> getRecordGenLedgAccountCumCum = _genLedgAccountCumRepo.findByPhoneNumber(phonenumber);
                    genLedgAccountCum = getRecordGenLedgAccountCumCum.get();
                    //wen they finally release I will not increase count

                    genLedgAccountCum.setCountSuccessTrans(genLedgAccountCum.getCountSuccessTrans());
                    genLedgAccountCum.setTotalAmountCredited(genLedgAccountCum.getTotalAmountCredited().add(accountCredit));
                    genLedgAccountCum.setTotalAmountDebited(genLedgAccountCum.getTotalAmountDebited().add(new BigDecimal(finalCharges)));
                    genLedgAccountCum.setTotalPayCharge(genLedgAccountCum.getTotalPayCharge().add(new BigDecimal(fees)));
                    genLedgAccountCum.setTotalSwCharges(genLedgAccountCum.getTotalSwCharges().add(swChargesGel));
                    genLedgAccountCum.setTotalBalance(genLedgAccountCum.getTotalBalance().subtract(new BigDecimal(finalCharges)));
                    genLedgAccountCum.setTotalBookBalance(genLedgAccountCum.getTotalBookBalance().subtract(new BigDecimal(rq.getFinalCharges())));
                    genLedgAccountCum.setTotalMerchantBookedBalance(genLedgAccountCum.getTotalMerchantBookedBalance());

                    _genLedgAccountCumRepo.save(genLedgAccountCum);

                }

                if (_genLedgAccountCumRepo.existsByPhnProductCode(rq.getPhonenumber() + rq.getProductCode())) {
                    logger.info(String.format("Saving to GenLedgAccountCum Table phone-productcode exists"));
                    Optional<GenLedgAccountCum> getRecordGenLedgAccountCumCum = _genLedgAccountCumRepo.findByPhnProductCode(rq.getPhonenumber() + rq.getProductCode());
                    genLedgAccountCum = getRecordGenLedgAccountCumCum.get();
                    //wen they finally release I will not increase count

                    genLedgAccountCum.setCountSuccTransPhnProCode(genLedgAccountCum.getCountSuccTransPhnProCode());
                    genLedgAccountCum.setTotalAmtCreditedPhnProCode(genLedgAccountCum.getTotalAmtCreditedPhnProCode().add(accountCredit));
                    genLedgAccountCum.setTotalAmtDebitedPhnProCode(genLedgAccountCum.getTotalAmtDebitedPhnProCode().add(new BigDecimal(finalCharges)));
                    genLedgAccountCum.setTotalPayChargePhnProCode(genLedgAccountCum.getTotalPayChargePhnProCode().add(new BigDecimal(fees)));
                    genLedgAccountCum.setTotalSwChargesPhnProCode(genLedgAccountCum.getTotalSwChargesPhnProCode().add(swChargesGel));
                    genLedgAccountCum.setTotalBalancePhnProCode(genLedgAccountCum.getTotalBalancePhnProCode().subtract(new BigDecimal(finalCharges)));
                    genLedgAccountCum.setTotalBookBalancePhnProCode(genLedgAccountCum.getTotalBookBalancePhnProCode().subtract(new BigDecimal(rq.getFinalCharges())));
                    genLedgAccountCum.setTotalMerBookedBalPhnProCode(genLedgAccountCum.getTotalMerBookedBalPhnProCode());

                    _genLedgAccountCumRepo.save(genLedgAccountCum);

                }

                responseModel.setDescription("Account debited sucessfully.");
                responseModel.setStatusCode(200);
            } else {
                responseModel.setDescription("Account does not exist in the Ledger.");
                responseModel.setStatusCode(statusCode);
            }

        } catch (Exception ex) {
            responseModel.setDescription(statusMessage);
            responseModel.setStatusCode(statusCode);
            ex.printStackTrace();
        }

        return responseModel;

    }

    //processCreditLedgerOneTime
    public BaseResponse processCreditLedgerOneTime(ProcLedgerRequestCreditOneTime rq) {

        BaseResponse responseModel = new BaseResponse();
        int statusCode = 500;
        String statusMessage = "An error occured,please try again";
        try {
            statusCode = 400;

            System.out.println("processCreditLedgerOneTime rq   ::::::::::::::::               ::::: %S  " + new Gson().toJson(rq));

            BigDecimal fMoneyCharges = rq.getKulFees();
            String phonenumber = rq.getPhoneNumber();
            String fundingType = rq.getFundingType();
            String narration = rq.getNarration();
            String fMoneyTransId = rq.getKulTransactionId();
            BigDecimal dataVerifyateGetAmount = rq.getTransAmount();
            BigDecimal getVerifyChargeAmount = BigDecimal.ZERO;
            BigDecimal amountLeftBeforeFMoneyCharges = dataVerifyateGetAmount.subtract(getVerifyChargeAmount);
            BigDecimal w_amountPaidInCum = BigDecimal.ZERO;
            BigDecimal w_swChargesCum = BigDecimal.ZERO;
            BigDecimal w_fMoneyChargesCum = BigDecimal.ZERO;
            BigDecimal w_amtCreToWalletCum = BigDecimal.ZERO;

            BigDecimal p_swChargesCum = BigDecimal.ZERO;
            BigDecimal p_fMoneyChargesCum = BigDecimal.ZERO;
            BigDecimal p_amtCreToWalletCum = BigDecimal.ZERO;
            BigDecimal p_amountPaidInCum = BigDecimal.ZERO;
            BigDecimal balForWallet = amountLeftBeforeFMoneyCharges.subtract(fMoneyCharges);

            BigDecimal accountCredit = balForWallet;
            BigDecimal accountCreditCum = BigDecimal.ZERO;
            BigDecimal pl_cum_AccountCredit = BigDecimal.ZERO;
            BigDecimal accountDebit = BigDecimal.ZERO;
            BigDecimal accountDebitCum = BigDecimal.ZERO;
            BigDecimal pl_cum_AccountDebit = BigDecimal.ZERO;
            BigDecimal swChargesGel = rq.getSwFees();
            BigDecimal swChargesCumGel = BigDecimal.ZERO;
            BigDecimal pl_cum_swChargesGel = BigDecimal.ZERO;
            BigDecimal fMoneyChargesGel = fMoneyCharges;
            BigDecimal pl_cum_fMoneyChargesGel = BigDecimal.ZERO;
            BigDecimal fMoneyChargesCumGel = BigDecimal.ZERO;
            BigDecimal productCodeFeeCum = BigDecimal.ZERO;
            int countProductCodeTrans;

            BigDecimal balancePhnProCode = BigDecimal.ZERO;
            BigDecimal accountCreditCumPhnProCode = BigDecimal.ZERO;
            BigDecimal accountDebitCumPhnProCode = BigDecimal.ZERO;
            BigDecimal swChargesCumGelPhnProCode = BigDecimal.ZERO;
            BigDecimal fMoneyChargesCumGelPhnProCode = BigDecimal.ZERO;
            BigDecimal bookBalancePhnProCode = BigDecimal.ZERO;
            BigDecimal merchantBookedBalancePhnProCode = BigDecimal.ZERO;
            String phnPrdCode = rq.getPhoneNumber() + rq.getProductCode();

            if (walletFundingSucessInfoRepo.findTopByOrderByIdDesc() != null) {
                BigDecimal balance = BigDecimal.ZERO;
                BigDecimal bookBalance = BigDecimal.ZERO;
                BigDecimal merchantBookedBalance = BigDecimal.ZERO;
                if (walletFundingSucessInfoRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Getting phonenumber to update wallet's cummulatives", phonenumber));
                    WalletFundSucInfo result;

                    Optional<WalletFundSucInfo> getTrans = walletFundingSucessInfoRepo.findByPhoneNumber(phonenumber);
                    result = getTrans.get();
                    w_amountPaidInCum = result.getAmountPaidInCum().add(dataVerifyateGetAmount);
                    w_swChargesCum = result.getSwChargesCum().add(getVerifyChargeAmount);
                    w_fMoneyChargesCum = result.getDemoPayChargesCum().add(fMoneyCharges);
                    w_amtCreToWalletCum = result.getAmtCreToWalletCum().add(balForWallet);

                }
                logger.info(String.format("Data exist in WalletFundSucInfo Table"));

                List<GenLedgAccount> getDeee = _genLedgAccountRepo.findByPhoneNumberProdCode(rq.getPhoneNumber(), rq.getProductCode());

                if (getDeee.size() > 0) {

                    logger.info(String.format("Getting phonenumber to update Gen Ledger Cummulative", phonenumber));
                    GenLedgAccount genLedResult;
                    Optional<GenLedgAccount> getGenLedgerTrans = _genLedgAccountRepo.findByOptPhoneNumberProdCode(rq.getPhoneNumberProductCode());
                    genLedResult = getGenLedgerTrans.get();

                    accountCreditCumPhnProCode = genLedResult.getAccountCreditCum().add(accountCredit);
                    accountDebitCumPhnProCode = genLedResult.getAccountDebitCum().add(accountDebit);
                    swChargesCumGelPhnProCode = genLedResult.getSwChargesCum().add(swChargesGel);
                    fMoneyChargesCumGelPhnProCode = genLedResult.getDemoPayChargesCum().add(fMoneyChargesGel);
                    balancePhnProCode = genLedResult.getBalance().add(accountCredit);
                    bookBalancePhnProCode = genLedResult.getBookBalance().add(BigDecimal.ZERO);
                    merchantBookedBalancePhnProCode = genLedResult.getMerchantBookedBalance().add(BigDecimal.ZERO);

                } else {
                    balancePhnProCode = accountCredit;
                    bookBalancePhnProCode = BigDecimal.ZERO;
                    merchantBookedBalancePhnProCode = BigDecimal.ZERO;
                    accountCreditCumPhnProCode = accountCredit;
                    swChargesCumGelPhnProCode = swChargesGel;
                    fMoneyChargesCumGelPhnProCode = fMoneyChargesGel;

                }

                if (_genLedgAccountRepo.existsByPhoneNumber(phonenumber)) {

                    logger.info(String.format("Getting phonenumber to update Gen Ledger Cummulative", phonenumber));
                    GenLedgAccount genLedResult;
                    Optional<GenLedgAccount> getGenLedgerTrans = _genLedgAccountRepo.findByPhoneNumber(phonenumber);
                    genLedResult = getGenLedgerTrans.get();

                    accountCreditCum = genLedResult.getAccountCreditCum().add(accountCredit);
                    accountDebitCum = genLedResult.getAccountDebitCum().add(accountDebit);
                    swChargesCumGel = genLedResult.getSwChargesCum().add(swChargesGel);
                    fMoneyChargesCumGel = genLedResult.getDemoPayChargesCum().add(fMoneyChargesGel);
                    balance = genLedResult.getBalance().add(accountCredit);
                    bookBalance = genLedResult.getBookBalance().add(BigDecimal.ZERO);
                    merchantBookedBalance = genLedResult.getMerchantBookedBalance().add(BigDecimal.ZERO);

                } else {
                    balance = accountCredit;
                    bookBalance = BigDecimal.ZERO;
                    merchantBookedBalance = BigDecimal.ZERO;
                    accountCreditCum = accountCredit;
                    swChargesCumGel = swChargesGel;
                    fMoneyChargesCumGel = fMoneyChargesGel;
                }

                if (_genLedgAccountRepo.existsByProductCode(rq.getProductCode())) {

                    logger.info(String.format("Getting productCode to update Gen Ledger Cummulative"));
                    GenLedgAccount genLedResult;
                    Optional<GenLedgAccount> getGenLedgerTrans = _genLedgAccountRepo.findByProductCode(rq.getProductCode());
                    genLedResult = getGenLedgerTrans.get();
                    productCodeFeeCum = genLedResult.getDemoPayChargesCum().add(fMoneyChargesGel);
                    countProductCodeTrans = genLedResult.getCountProductCodeTrans() + 1;

                } else {

                    productCodeFeeCum = fMoneyChargesGel;
                    countProductCodeTrans = +1;
                }

                logger.info(String.format("Data exist in GenLedgAccount Table"));

                GenLedgAccount genLedgAccount = _genLedgAccountRepo.findTopByOrderByIdDesc();
                pl_cum_AccountCredit = genLedgAccount.getPl_cum_AccountCredit().add(accountCredit);
                pl_cum_AccountDebit = genLedgAccount.getPl_cum_AccountDebit().add(accountDebit);
                pl_cum_swChargesGel = genLedgAccount.getPl_cum_swCharges().add(swChargesGel);
                pl_cum_fMoneyChargesGel = genLedgAccount.getPl_cum_fMoneyCharges().add(fMoneyChargesGel);

                //save to gen ledger...
                logger.info(String.format("Saving to General Ledger Table"));
                GenLedgAccount genLedger = new GenLedgAccount(fMoneyTransId, phonenumber,
                        fundingType, accountCredit, balance,
                        bookBalance, accountCreditCum,
                        pl_cum_AccountCredit, accountDebit, accountDebitCum,
                        pl_cum_AccountDebit, swChargesGel, swChargesCumGel, pl_cum_swChargesGel,
                        fMoneyChargesGel, pl_cum_fMoneyChargesGel, fMoneyChargesCumGel, narration,
                        merchantBookedBalance,
                        rq.getProductCode(), rq.getProductName(), countProductCodeTrans,
                        productCodeFeeCum, rq.getPhoneNumberProductCode(), balancePhnProCode, accountCreditCumPhnProCode, accountDebitCumPhnProCode,
                        swChargesCumGelPhnProCode, fMoneyChargesCumGelPhnProCode, bookBalancePhnProCode, merchantBookedBalancePhnProCode
                );
                _genLedgAccountRepo.save(genLedger);

                //save to WalletFundSucInfo for Indirect Funding
                WalletFundSucInfo wallSucessInfoResult = walletFundingSucessInfoRepo.findTopByOrderByIdDesc();
                p_amountPaidInCum = wallSucessInfoResult.getAmountPaidInCum().add(dataVerifyateGetAmount);
                p_swChargesCum = wallSucessInfoResult.getSwChargesCum().add(getVerifyChargeAmount);
                p_fMoneyChargesCum = wallSucessInfoResult.getDemoPayChargesCum().add(fMoneyCharges);
                p_amtCreToWalletCum = wallSucessInfoResult.getAmtCreToWalletCum().add(balForWallet);
                logger.info(String.format("Saving to WalletFundSucInfo Table"));

                WalletFundSucInfo wallSucessInfo = new WalletFundSucInfo(
                        dataVerifyateGetAmount, w_amountPaidInCum, p_amountPaidInCum,
                        getVerifyChargeAmount, w_swChargesCum, p_swChargesCum,
                        fMoneyCharges, w_fMoneyChargesCum, p_fMoneyChargesCum,
                        balForWallet, w_amtCreToWalletCum, p_amtCreToWalletCum,
                        phonenumber, fMoneyTransId,
                        rq.getSwRefrenceNumber(), "No Validation from Bank", rq.getSwRefrenceNumber(), rq.getSwRefrenceNumber()
                );
                walletFundingSucessInfoRepo.save(wallSucessInfo);
                //save to gen ledger cummulative...
                GenLedgAccountCum genLedgAccountCum;

                if (_genLedgAccountCumRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Saving to GenLedgAccountCum Table phoneNumber exists"));
                    Optional<GenLedgAccountCum> getRecordGenLedgAccountCumCum = _genLedgAccountCumRepo.findByPhoneNumber(phonenumber);
                    genLedgAccountCum = getRecordGenLedgAccountCumCum.get();
                    genLedgAccountCum.setCountSuccessTrans(genLedgAccountCum.getCountSuccessTrans() + 1);
                    genLedgAccountCum.setTotalAmountCredited(genLedgAccountCum.getTotalAmountCredited().add(accountCredit));
                    genLedgAccountCum.setTotalAmountDebited(genLedgAccountCum.getTotalAmountDebited().add(accountDebit));
                    genLedgAccountCum.setTotalPayCharge(genLedgAccountCum.getTotalPayCharge().add(fMoneyChargesGel));
                    genLedgAccountCum.setTotalSwCharges(genLedgAccountCum.getTotalSwCharges().add(swChargesGel));
                    genLedgAccountCum.setTotalBalance(genLedgAccountCum.getTotalBalance().add(accountCredit));
                    genLedgAccountCum.setTotalBookBalance(genLedgAccountCum.getTotalBookBalance().add(BigDecimal.ZERO));
                    genLedgAccountCum.setTotalMerchantBookedBalance(genLedgAccountCum.getTotalMerchantBookedBalance().add(BigDecimal.ZERO));

                    _genLedgAccountCumRepo.save(genLedgAccountCum);

                }

                if (_genLedgAccountCumRepo.existsByPhnProductCode(rq.getPhoneNumber() + rq.getProductCode())) {
                    logger.info(String.format("Saving to GenLedgAccountCum Table phone-productcode exists"));
                    Optional<GenLedgAccountCum> getRecordGenLedgAccountCumCum = _genLedgAccountCumRepo.findByPhnProductCode(rq.getPhoneNumber() + rq.getProductCode());
                    genLedgAccountCum = getRecordGenLedgAccountCumCum.get();
                    genLedgAccountCum.setCountSuccTransPhnProCode(genLedgAccountCum.getCountSuccTransPhnProCode() + 1);
                    genLedgAccountCum.setTotalAmtCreditedPhnProCode(genLedgAccountCum.getTotalAmtCreditedPhnProCode().add(accountCredit));
                    genLedgAccountCum.setTotalAmtDebitedPhnProCode(genLedgAccountCum.getTotalAmtDebitedPhnProCode().add(accountDebit));
                    genLedgAccountCum.setTotalPayChargePhnProCode(genLedgAccountCum.getTotalPayChargePhnProCode().add(fMoneyChargesGel));
                    genLedgAccountCum.setTotalSwChargesPhnProCode(genLedgAccountCum.getTotalSwChargesPhnProCode().add(swChargesGel));
                    genLedgAccountCum.setTotalBalancePhnProCode(genLedgAccountCum.getTotalBalancePhnProCode().add(accountCredit));
                    genLedgAccountCum.setTotalBookBalancePhnProCode(genLedgAccountCum.getTotalBookBalance().add(BigDecimal.ZERO));
                    genLedgAccountCum.setTotalMerBookedBalPhnProCode(genLedgAccountCum.getTotalMerBookedBalPhnProCode().add(BigDecimal.ZERO));

                    _genLedgAccountCumRepo.save(genLedgAccountCum);

                } else {

                    logger.info(String.format("Saving to genLedgAccountCum Table phoneNumber first entry"));
                    GenLedgAccountCum genLedgAcct = new GenLedgAccountCum(
                            phonenumber, phonenumber, 1, accountCredit, accountCredit, BigDecimal.ZERO, accountDebit,
                            swChargesGel, fMoneyChargesGel, merchantBookedBalance, phnPrdCode, 1, accountCredit, accountCredit, BigDecimal.ZERO, accountDebit,
                            swChargesGel, fMoneyChargesGel, merchantBookedBalance
                    );
                    _genLedgAccountCumRepo.save(genLedgAcct);
                }

                WalletFundingInfoCum walletFundingInfoCum;

                if (walletFundingInfoCumRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Saving to WalletFundingInfoCum Table phoneNumber exists"));
                    Optional<WalletFundingInfoCum> getRecordWalletFundingInfoCumRe = walletFundingInfoCumRepo.findByPhoneNumber(phonenumber);

                    walletFundingInfoCum = getRecordWalletFundingInfoCumRe.get();
                    walletFundingInfoCum.setCountSuccessTrans(walletFundingInfoCum.getCountSuccessTrans() + 1);
                    walletFundingInfoCum.setTotalAmountPaidIn(walletFundingInfoCum.getTotalAmountPaidIn().add(dataVerifyateGetAmount));
                    walletFundingInfoCum.setTotalAmtCreToWallet(walletFundingInfoCum.getTotalAmtCreToWallet().add(balForWallet));
                    walletFundingInfoCum.setTotalFMoneyChrge(walletFundingInfoCum.getTotalFMoneyChrge().add(fMoneyCharges));
                    walletFundingInfoCum.setTotalSwCharges(walletFundingInfoCum.getTotalSwCharges().add(getVerifyChargeAmount));
                    walletFundingInfoCumRepo.save(walletFundingInfoCum);
                    //send SMS to subscriber
                    // responseModel.setDescription("Request initiated successfully");
                    responseModel.setStatusCode(200);
                } else {
                    logger.info(String.format("Saving to WalletFundingInfoCum Table phoneNumber first entry"));

                    WalletFundingInfoCum wallFundingCum = new WalletFundingInfoCum(
                            phonenumber, phonenumber, 1,
                            dataVerifyateGetAmount, getVerifyChargeAmount, fMoneyCharges,
                            balForWallet
                    );
                    walletFundingInfoCumRepo.save(wallFundingCum);
                    //send SMS to subscriber

                    responseModel.setStatusCode(200);
                }

            } else {

                logger.info(String.format("Data does not exist in WalletFundSucInfo and GenLedgAccount Table"));

                //save to gen ledger...
                logger.info(String.format("Saving to General-Ledger Table"));
                GenLedgAccount genLedger = new GenLedgAccount(fMoneyTransId, phonenumber,
                        "In-Direct Wallet Funding", accountCredit,
                        accountCredit, BigDecimal.ZERO, accountCredit,
                        accountCredit, accountDebit, accountDebit, accountDebit, swChargesGel, swChargesGel, swChargesGel,
                        fMoneyChargesGel, fMoneyChargesGel, fMoneyChargesGel, narration, BigDecimal.ZERO,
                        rq.getProductCode(), rq.getProductName(), 1,
                        fMoneyChargesGel, rq.getPhoneNumberProductCode(), balancePhnProCode, accountCreditCumPhnProCode, accountDebitCumPhnProCode,
                        swChargesCumGelPhnProCode, fMoneyChargesCumGelPhnProCode, bookBalancePhnProCode, merchantBookedBalancePhnProCode);
                _genLedgAccountRepo.save(genLedger);

                logger.info(String.format("Saving to WalletFundSucInfo Table"));
                WalletFundSucInfo wallSucessInfo = new WalletFundSucInfo(
                        dataVerifyateGetAmount, dataVerifyateGetAmount, dataVerifyateGetAmount,
                        getVerifyChargeAmount, getVerifyChargeAmount, getVerifyChargeAmount,
                        fMoneyCharges, fMoneyCharges, fMoneyCharges,
                        balForWallet, balForWallet, balForWallet,
                        phonenumber, fMoneyTransId,
                        rq.getSwRefrenceNumber(), "No Validation from Bank", rq.getSwRefrenceNumber(), rq.getSwRefrenceNumber()
                );
                walletFundingSucessInfoRepo.save(wallSucessInfo);

                WalletFundingInfoCum walletFundingInfoCum;

                if (walletFundingInfoCumRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Saving to WalletFundingInfoCum Table phoneNumber exists"));
                    Optional<WalletFundingInfoCum> getRecordWalletFundingInfoCumRe = walletFundingInfoCumRepo.findByPhoneNumber(phonenumber);

                    walletFundingInfoCum = getRecordWalletFundingInfoCumRe.get();
                    walletFundingInfoCum.setCountSuccessTrans(walletFundingInfoCum.getCountSuccessTrans() + 1);
                    walletFundingInfoCum.setTotalAmountPaidIn(walletFundingInfoCum.getTotalAmountPaidIn().add(dataVerifyateGetAmount));
                    walletFundingInfoCum.setTotalAmtCreToWallet(walletFundingInfoCum.getTotalAmtCreToWallet().add(balForWallet));
                    walletFundingInfoCum.setTotalFMoneyChrge(walletFundingInfoCum.getTotalFMoneyChrge().add(fMoneyCharges));
                    walletFundingInfoCum.setTotalSwCharges(walletFundingInfoCum.getTotalSwCharges().add(getVerifyChargeAmount));
                    walletFundingInfoCumRepo.save(walletFundingInfoCum);
                    //send SMS to subscriber
                    responseModel.setStatusCode(200);
                } else {
                    logger.info(String.format("Saving to WalletFundingInfoCum Table phoneNumber first entry"));
                    WalletFundingInfoCum wallFundingCum = new WalletFundingInfoCum(
                            phonenumber, phonenumber, 1,
                            dataVerifyateGetAmount, getVerifyChargeAmount, fMoneyCharges,
                            balForWallet
                    );
                    walletFundingInfoCumRepo.save(wallFundingCum);
                    //send SMS to subscriber
                    responseModel.setStatusCode(200);
                }
                GenLedgAccountCum genLedgAccountCum;

                if (_genLedgAccountCumRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Saving to GenLedgAccountCum Table phoneNumber exists"));
                    Optional<GenLedgAccountCum> getRecordGenLedgAccountCumCum = _genLedgAccountCumRepo.findByPhoneNumber(phonenumber);
                    genLedgAccountCum = getRecordGenLedgAccountCumCum.get();
                    genLedgAccountCum.setCountSuccessTrans(genLedgAccountCum.getCountSuccessTrans() + 1);
                    genLedgAccountCum.setTotalAmountCredited(genLedgAccountCum.getTotalAmountCredited().add(accountCredit));
                    genLedgAccountCum.setTotalAmountDebited(genLedgAccountCum.getTotalAmountDebited().add(accountDebit));
                    genLedgAccountCum.setTotalPayCharge(genLedgAccountCum.getTotalPayCharge().add(fMoneyChargesGel));
                    genLedgAccountCum.setTotalSwCharges(genLedgAccountCum.getTotalSwCharges().add(swChargesGel));
                    genLedgAccountCum.setTotalBalance(genLedgAccountCum.getTotalBalance().add(accountCredit));
                    genLedgAccountCum.setTotalBookBalance(genLedgAccountCum.getTotalBookBalance().add(BigDecimal.ZERO));
                    genLedgAccountCum.setTotalMerchantBookedBalance(genLedgAccountCum.getTotalMerchantBookedBalance().add(BigDecimal.ZERO));
                    _genLedgAccountCumRepo.save(genLedgAccountCum);

                }
                if (_genLedgAccountCumRepo.existsByPhnProductCode(rq.getPhoneNumber() + rq.getProductCode())) {
                    logger.info(String.format("Saving to GenLedgAccountCum Table phone-productcode exists"));
                    Optional<GenLedgAccountCum> getRecordGenLedgAccountCumCum = _genLedgAccountCumRepo.findByPhnProductCode(rq.getPhoneNumber() + rq.getProductCode());
                    genLedgAccountCum = getRecordGenLedgAccountCumCum.get();
                    genLedgAccountCum.setCountSuccTransPhnProCode(genLedgAccountCum.getCountSuccTransPhnProCode() + 1);
                    genLedgAccountCum.setTotalAmtCreditedPhnProCode(genLedgAccountCum.getTotalAmtCreditedPhnProCode().add(accountCredit));
                    genLedgAccountCum.setTotalAmtDebitedPhnProCode(genLedgAccountCum.getTotalAmtDebitedPhnProCode().add(accountDebit));
                    genLedgAccountCum.setTotalPayChargePhnProCode(genLedgAccountCum.getTotalPayChargePhnProCode().add(fMoneyChargesGel));
                    genLedgAccountCum.setTotalSwChargesPhnProCode(genLedgAccountCum.getTotalSwChargesPhnProCode().add(swChargesGel));
                    genLedgAccountCum.setTotalBalancePhnProCode(genLedgAccountCum.getTotalBalancePhnProCode().add(accountCredit));
                    genLedgAccountCum.setTotalBookBalancePhnProCode(genLedgAccountCum.getTotalBookBalance().add(BigDecimal.ZERO));
                    genLedgAccountCum.setTotalMerBookedBalPhnProCode(genLedgAccountCum.getTotalMerBookedBalPhnProCode().add(BigDecimal.ZERO));

                    _genLedgAccountCumRepo.save(genLedgAccountCum);

                } else {

                    logger.info(String.format("Saving to genLedgAccountCum Table phoneNumber first entry"));
                    GenLedgAccountCum genLedgAcct = new GenLedgAccountCum(
                            phonenumber, phonenumber, 1, accountCredit, accountCredit, BigDecimal.ZERO, accountDebit,
                            swChargesGel, fMoneyChargesGel, BigDecimal.ZERO, phnPrdCode, 1, accountCredit, accountCredit, BigDecimal.ZERO, accountDebit,
                            swChargesGel, fMoneyChargesGel, BigDecimal.ZERO
                    );
                    _genLedgAccountCumRepo.save(genLedgAcct);
                }
                responseModel.setStatusCode(200);
            }
        } catch (Exception ex) {
            responseModel.setDescription(statusMessage);
            responseModel.setStatusCode(statusCode);
            ex.printStackTrace();
        }

        return responseModel;

    }

    //escrowCreditLedgerOneTimeAwaitingReleasedOrRollBack
    public BaseResponse escrowCreditLedgerOneTimeAwaitingReleasedOrRollBack(ProcLedgerRequestCreditOneTime rq) {

        BaseResponse responseModel = new BaseResponse();
        int statusCode = 500;
        String statusMessage = "An error occured,please try again";
        try {
            statusCode = 400;

            BigDecimal fMoneyCharges = rq.getKulFees();
            String phonenumber = rq.getPhoneNumber();
            String fundingType = rq.getFundingType();
            String narration = rq.getNarration();
            String fMoneyTransId = rq.getKulTransactionId();
            BigDecimal dataVerifyateGetAmount = BigDecimal.ZERO; //no deduction of rq.getTransAmount() here 
            BigDecimal getVerifyChargeAmount = rq.getSwFees();
            BigDecimal amountLeftBeforeFMoneyCharges = dataVerifyateGetAmount.subtract(getVerifyChargeAmount);
            BigDecimal w_amountPaidInCum = BigDecimal.ZERO;
            BigDecimal w_swChargesCum = BigDecimal.ZERO;
            BigDecimal w_fMoneyChargesCum = BigDecimal.ZERO;
            BigDecimal w_amtCreToWalletCum = BigDecimal.ZERO;

            BigDecimal p_swChargesCum = BigDecimal.ZERO;
            BigDecimal p_fMoneyChargesCum = BigDecimal.ZERO;
            BigDecimal p_amtCreToWalletCum = BigDecimal.ZERO;
            BigDecimal p_amountPaidInCum = BigDecimal.ZERO;
            BigDecimal balForWallet = amountLeftBeforeFMoneyCharges.subtract(fMoneyCharges);

            BigDecimal accountCredit = balForWallet;
            BigDecimal accountCreditCum = BigDecimal.ZERO;
            BigDecimal pl_cum_AccountCredit = BigDecimal.ZERO;
            BigDecimal accountDebit = BigDecimal.ZERO;
            BigDecimal accountDebitCum = BigDecimal.ZERO;
            BigDecimal pl_cum_AccountDebit = BigDecimal.ZERO;
            BigDecimal swChargesGel = rq.getSwFees();
            BigDecimal swChargesCumGel = BigDecimal.ZERO;
            BigDecimal pl_cum_swChargesGel = BigDecimal.ZERO;
            BigDecimal fMoneyChargesGel = fMoneyCharges;
            BigDecimal pl_cum_fMoneyChargesGel = BigDecimal.ZERO;
            BigDecimal fMoneyChargesCumGel = BigDecimal.ZERO;
            BigDecimal productCodeFeeCum = BigDecimal.ZERO;

            BigDecimal balancePhnProCode = BigDecimal.ZERO;
            BigDecimal accountCreditCumPhnProCode = BigDecimal.ZERO;
            BigDecimal accountDebitCumPhnProCode = BigDecimal.ZERO;
            BigDecimal swChargesCumGelPhnProCode = BigDecimal.ZERO;
            BigDecimal fMoneyChargesCumGelPhnProCode = BigDecimal.ZERO;
            BigDecimal bookBalancePhnProCode = BigDecimal.ZERO;
            BigDecimal merchantBookedBalancePhnProCode = BigDecimal.ZERO;
            int countProductCodeTrans;
            String phnProdCode = rq.getPhoneNumber() + rq.getProductCode();

            if (walletFundingSucessInfoRepo.findTopByOrderByIdDesc() != null) {
                BigDecimal balance = BigDecimal.ZERO;
                BigDecimal bookBalance = BigDecimal.ZERO;
                BigDecimal merchantBookedBalance = BigDecimal.ZERO;
                if (walletFundingSucessInfoRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Getting phonenumber to update wallet's cummulatives", phonenumber));
                    WalletFundSucInfo result;

                    Optional<WalletFundSucInfo> getTrans = walletFundingSucessInfoRepo.findByPhoneNumber(phonenumber);
                    result = getTrans.get();
                    w_amountPaidInCum = result.getAmountPaidInCum().add(dataVerifyateGetAmount);
                    w_swChargesCum = result.getSwChargesCum().add(getVerifyChargeAmount);
                    w_fMoneyChargesCum = result.getDemoPayChargesCum().add(fMoneyCharges);
                    w_amtCreToWalletCum = result.getAmtCreToWalletCum().add(balForWallet);

                }
                logger.info(String.format("Data exist in WalletFundSucInfo Table"));

                List<GenLedgAccount> getDeee = _genLedgAccountRepo.findByPhoneNumberProdCode(rq.getPhoneNumber(), rq.getProductCode());

                if (getDeee.size() > 0) {

                    logger.info(String.format("Getting phonenumber to update Gen Ledger Cummulative", phonenumber));
                    GenLedgAccount genLedResult;
                    Optional<GenLedgAccount> getGenLedgerTrans = _genLedgAccountRepo.findByOptPhoneNumberProdCode(rq.getPhoneNumberProductCode());
                    genLedResult = getGenLedgerTrans.get();

                    accountCreditCumPhnProCode = genLedResult.getAccountCreditCum().add(accountCredit);
                    accountDebitCumPhnProCode = genLedResult.getAccountDebitCum().add(accountDebit);
                    swChargesCumGelPhnProCode = genLedResult.getSwChargesCum().add(swChargesGel);
                    fMoneyChargesCumGelPhnProCode = genLedResult.getDemoPayChargesCum().add(fMoneyChargesGel);
                    balancePhnProCode = genLedResult.getBalance().add(accountCredit);
                    bookBalancePhnProCode = genLedResult.getBookBalance().add(rq.getTransAmount());
                    merchantBookedBalancePhnProCode = genLedResult.getMerchantBookedBalance().add(merchantBookedBalance);

                } else {
                    bookBalancePhnProCode = rq.getTransAmount();
                    merchantBookedBalancePhnProCode = BigDecimal.ZERO;

                }

                if (_genLedgAccountRepo.existsByPhoneNumber(phonenumber)) {

                    logger.info(String.format("Getting phonenumber to update Gen Ledger Cummulative", phonenumber));
                    GenLedgAccount genLedResult;
                    Optional<GenLedgAccount> getGenLedgerTrans = _genLedgAccountRepo.findByPhoneNumber(phonenumber);
                    genLedResult = getGenLedgerTrans.get();

                    accountCreditCum = genLedResult.getAccountCreditCum().add(accountCredit);

                    accountDebitCum = genLedResult.getAccountDebitCum().add(accountDebit);
                    swChargesCumGel = genLedResult.getSwChargesCum().add(swChargesGel);
                    fMoneyChargesCumGel = genLedResult.getDemoPayChargesCum().add(fMoneyChargesGel);
                    balance = genLedResult.getBalance().add(accountCredit);

                    bookBalance = genLedResult.getBookBalance().add(rq.getTransAmount());
                    merchantBookedBalance = genLedResult.getMerchantBookedBalance().add(merchantBookedBalance);

                } else {
                    // balance = accountCredit;
                    bookBalance = rq.getTransAmount();
                    merchantBookedBalance = BigDecimal.ZERO;
                }
                if (_genLedgAccountRepo.existsByProductCode(rq.getProductCode())) {

                    logger.info(String.format("Getting productCode to update Gen Ledger Cummulative"));
                    GenLedgAccount genLedResult;
                    Optional<GenLedgAccount> getGenLedgerTrans = _genLedgAccountRepo.findByProductCode(rq.getProductCode());
                    genLedResult = getGenLedgerTrans.get();
                    productCodeFeeCum = genLedResult.getDemoPayChargesCum().add(fMoneyChargesGel);
                    countProductCodeTrans = genLedResult.getCountProductCodeTrans() + 1;

                } else {

                    productCodeFeeCum = fMoneyChargesGel;
                    countProductCodeTrans = +1;
                }

                logger.info(String.format("Data exist in GenLedgAccount Table"));

                GenLedgAccount genLedgAccount = _genLedgAccountRepo.findTopByOrderByIdDesc();
                pl_cum_AccountCredit = genLedgAccount.getPl_cum_AccountCredit().add(accountCredit);
                pl_cum_AccountDebit = genLedgAccount.getPl_cum_AccountDebit().add(accountDebit);
                pl_cum_swChargesGel = genLedgAccount.getPl_cum_swCharges().add(swChargesGel);
                pl_cum_fMoneyChargesGel = genLedgAccount.getPl_cum_fMoneyCharges().add(fMoneyChargesGel);

                //save to gen ledger...
                logger.info(String.format("Saving to General Ledger Table"));
                GenLedgAccount genLedger = new GenLedgAccount(fMoneyTransId, phonenumber, fundingType, accountCredit,
                        balance, bookBalance, accountCreditCum,
                        pl_cum_AccountCredit, accountDebit, accountDebitCum,
                        pl_cum_AccountDebit, swChargesGel, swChargesCumGel, pl_cum_swChargesGel,
                        fMoneyChargesGel, pl_cum_fMoneyChargesGel, fMoneyChargesCumGel,
                        narration, merchantBookedBalance,
                        rq.getProductCode(), rq.getProductName(), countProductCodeTrans,
                        productCodeFeeCum, rq.getPhoneNumberProductCode(), balancePhnProCode, accountCreditCumPhnProCode, accountDebitCumPhnProCode,
                        swChargesCumGelPhnProCode, fMoneyChargesCumGelPhnProCode,
                        bookBalancePhnProCode, merchantBookedBalancePhnProCode);
                _genLedgAccountRepo.save(genLedger);

                //save to WalletFundSucInfo for Indirect Funding
                WalletFundSucInfo wallSucessInfoResult = walletFundingSucessInfoRepo.findTopByOrderByIdDesc();
                p_amountPaidInCum = wallSucessInfoResult.getAmountPaidInCum().add(dataVerifyateGetAmount);
                p_swChargesCum = wallSucessInfoResult.getSwChargesCum().add(getVerifyChargeAmount);
                p_fMoneyChargesCum = wallSucessInfoResult.getDemoPayChargesCum().add(fMoneyCharges);
                p_amtCreToWalletCum = wallSucessInfoResult.getAmtCreToWalletCum().add(balForWallet);
                logger.info(String.format("Saving to WalletFundSucInfo Table"));

                WalletFundSucInfo wallSucessInfo = new WalletFundSucInfo(
                        dataVerifyateGetAmount, w_amountPaidInCum, p_amountPaidInCum,
                        getVerifyChargeAmount, w_swChargesCum, p_swChargesCum,
                        fMoneyCharges, w_fMoneyChargesCum, p_fMoneyChargesCum,
                        balForWallet, w_amtCreToWalletCum, p_amtCreToWalletCum,
                        phonenumber, fMoneyTransId,
                        rq.getSwRefrenceNumber(), "No Validation from Bank", rq.getSwRefrenceNumber(), rq.getSwRefrenceNumber()
                );
                walletFundingSucessInfoRepo.save(wallSucessInfo);
                //save to gen ledger cummulative...
                GenLedgAccountCum genLedgAccountCum;

                if (_genLedgAccountCumRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Saving to GenLedgAccountCum Table phoneNumber exists"));
                    Optional<GenLedgAccountCum> getRecordGenLedgAccountCumCum = _genLedgAccountCumRepo.findByPhoneNumber(phonenumber);
                    genLedgAccountCum = getRecordGenLedgAccountCumCum.get();
                    //on the awating final release for credit I will not increase count, wen they finally release I will increase count
                    //if they roll back I will leave without increase count
                    genLedgAccountCum.setCountSuccessTrans(genLedgAccountCum.getCountSuccessTrans());
                    genLedgAccountCum.setTotalAmountCredited(genLedgAccountCum.getTotalAmountCredited().add(accountCredit));
                    genLedgAccountCum.setTotalAmountDebited(genLedgAccountCum.getTotalAmountDebited().add(accountDebit));
                    genLedgAccountCum.setTotalPayCharge(genLedgAccountCum.getTotalPayCharge().add(fMoneyChargesGel));
                    genLedgAccountCum.setTotalSwCharges(genLedgAccountCum.getTotalSwCharges().add(swChargesGel));
                    genLedgAccountCum.setTotalBalance(genLedgAccountCum.getTotalBalance().add(accountCredit));
                    genLedgAccountCum.setTotalBookBalance(genLedgAccountCum.getTotalBookBalance().add(rq.getTransAmount()));
                    genLedgAccountCum.setTotalMerchantBookedBalance(genLedgAccountCum.getTotalMerchantBookedBalance());

                    _genLedgAccountCumRepo.save(genLedgAccountCum);

                }

                if (_genLedgAccountCumRepo.existsByPhnProductCode(rq.getPhoneNumber() + rq.getProductCode())) {
                    logger.info(String.format("Saving to GenLedgAccountCum Table phone-productcode exists"));
                    Optional<GenLedgAccountCum> getRecordGenLedgAccountCumCum = _genLedgAccountCumRepo.findByPhnProductCode(rq.getPhoneNumber() + rq.getProductCode());
                    genLedgAccountCum = getRecordGenLedgAccountCumCum.get();
                    //on the awating final release for credit I will not increase count, wen they finally release I will increase count
                    //if they roll back I will leave without increase count
                    genLedgAccountCum.setCountSuccessTrans(genLedgAccountCum.getCountSuccessTrans());
                    genLedgAccountCum.setTotalAmountCredited(genLedgAccountCum.getTotalAmountCredited().add(accountCredit));
                    genLedgAccountCum.setTotalAmountDebited(genLedgAccountCum.getTotalAmountDebited().add(accountDebit));
                    genLedgAccountCum.setTotalPayCharge(genLedgAccountCum.getTotalPayCharge().add(fMoneyChargesGel));
                    genLedgAccountCum.setTotalSwCharges(genLedgAccountCum.getTotalSwCharges().add(swChargesGel));
                    genLedgAccountCum.setTotalBalance(genLedgAccountCum.getTotalBalance().add(accountCredit));
                    genLedgAccountCum.setTotalBookBalance(genLedgAccountCum.getTotalBookBalance().add(rq.getTransAmount()));
                    genLedgAccountCum.setTotalMerchantBookedBalance(genLedgAccountCum.getTotalMerchantBookedBalance());

                    _genLedgAccountCumRepo.save(genLedgAccountCum);

                } else {

                    logger.info(String.format("Saving to genLedgAccountCum Table phoneNumber first entry"));
                    GenLedgAccountCum genLedgAcct = new GenLedgAccountCum(
                            phonenumber, phonenumber, 1, accountCredit, accountCredit, bookBalance, accountDebit,
                            swChargesGel, fMoneyChargesGel, merchantBookedBalance, phnProdCode,
                            1, accountCredit, accountCredit, bookBalance, accountDebit,
                            swChargesGel, fMoneyChargesGel, merchantBookedBalance
                    );
                    _genLedgAccountCumRepo.save(genLedgAcct);
                }

                WalletFundingInfoCum walletFundingInfoCum;

                if (walletFundingInfoCumRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Saving to WalletFundingInfoCum Table phoneNumber exists"));
                    Optional<WalletFundingInfoCum> getRecordWalletFundingInfoCumRe = walletFundingInfoCumRepo.findByPhoneNumber(phonenumber);

                    walletFundingInfoCum = getRecordWalletFundingInfoCumRe.get();
                    //on the awating final release for credit I will not increase count, wen they finally release I will increase count
                    //if they roll back I will leave without increase count
                    walletFundingInfoCum.setCountSuccessTrans(walletFundingInfoCum.getCountSuccessTrans());
                    walletFundingInfoCum.setTotalAmountPaidIn(walletFundingInfoCum.getTotalAmountPaidIn().add(dataVerifyateGetAmount));
                    walletFundingInfoCum.setTotalAmtCreToWallet(walletFundingInfoCum.getTotalAmtCreToWallet().add(balForWallet));
                    walletFundingInfoCum.setTotalFMoneyChrge(walletFundingInfoCum.getTotalFMoneyChrge().add(fMoneyCharges));
                    walletFundingInfoCum.setTotalSwCharges(walletFundingInfoCum.getTotalSwCharges().add(getVerifyChargeAmount));
                    walletFundingInfoCumRepo.save(walletFundingInfoCum);
                    //send SMS to subscriber
                    // responseModel.setDescription("Request initiated successfully");
                    responseModel.setStatusCode(200);
                } else {
                    logger.info(String.format("Saving to WalletFundingInfoCum Table phoneNumber first entry"));
                    WalletFundingInfoCum wallFundingCum = new WalletFundingInfoCum(
                            phonenumber, phonenumber, 1,
                            dataVerifyateGetAmount, getVerifyChargeAmount, fMoneyCharges,
                            balForWallet
                    );
                    walletFundingInfoCumRepo.save(wallFundingCum);
                    //send SMS to subscriber

                    responseModel.setStatusCode(200);
                }

            } else {

                logger.info(String.format("Data does not exist in WalletFundSucInfo and GenLedgAccount Table"));

                //save to gen ledger...
                logger.info(String.format("Saving to General-Ledger Table"));
                GenLedgAccount genLedger = new GenLedgAccount(fMoneyTransId, phonenumber,
                        "In-Direct Wallet Funding", accountCredit,
                        accountCredit, BigDecimal.ZERO, accountCredit,
                        accountCredit, accountDebit, accountDebit, accountDebit, swChargesGel, swChargesGel, swChargesGel,
                        fMoneyChargesGel, fMoneyChargesGel, fMoneyChargesGel, narration, BigDecimal.ZERO,
                        rq.getProductCode(), rq.getProductName(), 1,
                        fMoneyChargesGel, rq.getPhoneNumberProductCode(), balancePhnProCode, accountCreditCumPhnProCode,
                        accountDebitCumPhnProCode,
                        swChargesCumGelPhnProCode, fMoneyChargesCumGelPhnProCode, bookBalancePhnProCode, merchantBookedBalancePhnProCode);
                _genLedgAccountRepo.save(genLedger);

                logger.info(String.format("Saving to WalletFundSucInfo Table"));
                WalletFundSucInfo wallSucessInfo = new WalletFundSucInfo(
                        dataVerifyateGetAmount, dataVerifyateGetAmount, dataVerifyateGetAmount,
                        getVerifyChargeAmount, getVerifyChargeAmount, getVerifyChargeAmount,
                        fMoneyCharges, fMoneyCharges, fMoneyCharges,
                        balForWallet, balForWallet, balForWallet,
                        phonenumber, fMoneyTransId,
                        rq.getSwRefrenceNumber(), "No Validation from Bank", rq.getSwRefrenceNumber(), rq.getSwRefrenceNumber()
                );
                walletFundingSucessInfoRepo.save(wallSucessInfo);

                WalletFundingInfoCum walletFundingInfoCum;

                if (walletFundingInfoCumRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Saving to WalletFundingInfoCum Table phoneNumber exists"));
                    Optional<WalletFundingInfoCum> getRecordWalletFundingInfoCumRe = walletFundingInfoCumRepo.findByPhoneNumber(phonenumber);

                    walletFundingInfoCum = getRecordWalletFundingInfoCumRe.get();
                    //on the awating final release for credit I will not increase count, wen they finally release I will increase count
                    //if they roll back I will leave without increase count
                    walletFundingInfoCum.setCountSuccessTrans(walletFundingInfoCum.getCountSuccessTrans());
                    walletFundingInfoCum.setTotalAmountPaidIn(walletFundingInfoCum.getTotalAmountPaidIn().add(dataVerifyateGetAmount));
                    walletFundingInfoCum.setTotalAmtCreToWallet(walletFundingInfoCum.getTotalAmtCreToWallet().add(balForWallet));
                    walletFundingInfoCum.setTotalFMoneyChrge(walletFundingInfoCum.getTotalFMoneyChrge().add(fMoneyCharges));
                    walletFundingInfoCum.setTotalSwCharges(walletFundingInfoCum.getTotalSwCharges().add(getVerifyChargeAmount));
                    walletFundingInfoCumRepo.save(walletFundingInfoCum);
                    //send SMS to subscriber
                    responseModel.setStatusCode(200);
                } else {
                    logger.info(String.format("Saving to WalletFundingInfoCum Table phoneNumber first entry"));
                    WalletFundingInfoCum wallFundingCum = new WalletFundingInfoCum(
                            phonenumber, phonenumber, 1,
                            dataVerifyateGetAmount, getVerifyChargeAmount, fMoneyCharges,
                            balForWallet
                    );
                    walletFundingInfoCumRepo.save(wallFundingCum);
                    //send SMS to subscriber
                    responseModel.setStatusCode(200);
                }
                GenLedgAccountCum genLedgAccountCum;

                if (_genLedgAccountCumRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Saving to GenLedgAccountCum Table phoneNumber exists"));
                    Optional<GenLedgAccountCum> getRecordGenLedgAccountCumCum = _genLedgAccountCumRepo.findByPhoneNumber(phonenumber);
                    genLedgAccountCum = getRecordGenLedgAccountCumCum.get();
                    //on the awating final release for credit I will not increase count, wen they finally release I will increase count
                    //if they roll back I will leave without increase count

                    genLedgAccountCum.setCountSuccessTrans(genLedgAccountCum.getCountSuccessTrans());
                    genLedgAccountCum.setTotalAmountCredited(genLedgAccountCum.getTotalAmountCredited().add(accountCredit));
                    genLedgAccountCum.setTotalAmountDebited(genLedgAccountCum.getTotalAmountDebited().add(accountDebit));
                    genLedgAccountCum.setTotalPayCharge(genLedgAccountCum.getTotalPayCharge().add(fMoneyChargesGel));
                    genLedgAccountCum.setTotalSwCharges(genLedgAccountCum.getTotalSwCharges().add(swChargesGel));
                    genLedgAccountCum.setTotalBalance(genLedgAccountCum.getTotalBalance().add(accountCredit));
                    genLedgAccountCum.setTotalBookBalance(genLedgAccountCum.getTotalBookBalance().add(rq.getTransAmount()));
                    genLedgAccountCum.setTotalMerchantBookedBalance(genLedgAccountCum.getTotalMerchantBookedBalance());
                    _genLedgAccountCumRepo.save(genLedgAccountCum);

                }

                if (_genLedgAccountCumRepo.existsByPhnProductCode(rq.getPhoneNumber() + rq.getProductCode())) {
                    logger.info(String.format("Saving to GenLedgAccountCum Table phone-productcode exists"));
                    Optional<GenLedgAccountCum> getRecordGenLedgAccountCumCum = _genLedgAccountCumRepo.findByPhnProductCode(phnProdCode);
                    genLedgAccountCum = getRecordGenLedgAccountCumCum.get();
                    //on the awating final release for credit I will not increase count, wen they finally release I will increase count
                    //if they roll back I will leave without increase count

                    genLedgAccountCum.setCountSuccTransPhnProCode(genLedgAccountCum.getCountSuccTransPhnProCode());
                    genLedgAccountCum.setTotalAmtCreditedPhnProCode(genLedgAccountCum.getTotalAmtCreditedPhnProCode().add(accountCredit));
                    genLedgAccountCum.setTotalAmtDebitedPhnProCode(genLedgAccountCum.getTotalAmtDebitedPhnProCode().add(accountDebit));
                    genLedgAccountCum.setTotalPayChargePhnProCode(genLedgAccountCum.getTotalPayChargePhnProCode().add(fMoneyChargesGel));
                    genLedgAccountCum.setTotalSwChargesPhnProCode(genLedgAccountCum.getTotalSwChargesPhnProCode().add(swChargesGel));
                    genLedgAccountCum.setTotalBalancePhnProCode(genLedgAccountCum.getTotalBalancePhnProCode().add(accountCredit));
                    genLedgAccountCum.setTotalBookBalancePhnProCode(genLedgAccountCum.getTotalBookBalancePhnProCode().add(rq.getTransAmount()));
                    genLedgAccountCum.setTotalMerBookedBalPhnProCode(genLedgAccountCum.getTotalMerBookedBalPhnProCode());
                    _genLedgAccountCumRepo.save(genLedgAccountCum);

                } else {

                    logger.info(String.format("Saving to genLedgAccountCum Table phoneNumber first entry"));
                    GenLedgAccountCum genLedgAcct = new GenLedgAccountCum(
                            phonenumber, phonenumber, 1, accountCredit, accountCredit, BigDecimal.ZERO, accountDebit,
                            swChargesGel, fMoneyChargesGel, BigDecimal.ZERO, phnProdCode, 1, accountCredit, accountCredit, BigDecimal.ZERO, accountDebit,
                            swChargesGel, fMoneyChargesGel, BigDecimal.ZERO
                    );
                    _genLedgAccountCumRepo.save(genLedgAcct);
                }
                responseModel.setStatusCode(200);
            }
        } catch (Exception ex) {
            responseModel.setDescription(statusMessage);
            responseModel.setStatusCode(statusCode);
            ex.printStackTrace();
        }

        return responseModel;

    }

    //escrowCreditLedgerOneTimeAwaitingReleasedOrRollBack
    public BaseResponse escrowCardCreditLedgerOneTimeAwaitingReleasedOrRollBack(ProcLedgerRequestCreditOneTime rq) {

        BaseResponse responseModel = new BaseResponse();
        int statusCode = 500;
        String statusMessage = "An error occured,please try again";
        try {
            statusCode = 400;

            BigDecimal fMoneyCharges = rq.getKulFees();
            String phonenumber = rq.getPhoneNumber();
            String fundingType = rq.getFundingType();
            String narration = rq.getNarration();
            String fMoneyTransId = rq.getKulTransactionId();
            BigDecimal dataVerifyateGetAmount = BigDecimal.ZERO; //no deduction of rq.getTransAmount() here 
            BigDecimal getVerifyChargeAmount = BigDecimal.ZERO;
            BigDecimal amountLeftBeforeFMoneyCharges = dataVerifyateGetAmount.subtract(getVerifyChargeAmount);
            BigDecimal w_amountPaidInCum = BigDecimal.ZERO;
            BigDecimal w_swChargesCum = BigDecimal.ZERO;
            BigDecimal w_fMoneyChargesCum = BigDecimal.ZERO;
            BigDecimal w_amtCreToWalletCum = BigDecimal.ZERO;

            BigDecimal p_swChargesCum = BigDecimal.ZERO;
            BigDecimal p_fMoneyChargesCum = BigDecimal.ZERO;
            BigDecimal p_amtCreToWalletCum = BigDecimal.ZERO;
            BigDecimal p_amountPaidInCum = BigDecimal.ZERO;
            BigDecimal balForWallet = amountLeftBeforeFMoneyCharges;

            BigDecimal accountCredit = balForWallet;
            BigDecimal accountCreditCum = BigDecimal.ZERO;
            BigDecimal pl_cum_AccountCredit = BigDecimal.ZERO;
            BigDecimal accountDebit = BigDecimal.ZERO;
            BigDecimal accountDebitCum = BigDecimal.ZERO;
            BigDecimal pl_cum_AccountDebit = BigDecimal.ZERO;
            BigDecimal swChargesGel = rq.getSwFees();
            BigDecimal swChargesCumGel = BigDecimal.ZERO;
            BigDecimal pl_cum_swChargesGel = BigDecimal.ZERO;
            BigDecimal fMoneyChargesGel = fMoneyCharges;
            BigDecimal pl_cum_fMoneyChargesGel = BigDecimal.ZERO;
            BigDecimal fMoneyChargesCumGel = BigDecimal.ZERO;
            BigDecimal productCodeFeeCum = BigDecimal.ZERO;

            BigDecimal balancePhnProCode = BigDecimal.ZERO;
            BigDecimal accountCreditCumPhnProCode = BigDecimal.ZERO;
            BigDecimal accountDebitCumPhnProCode = BigDecimal.ZERO;
            BigDecimal swChargesCumGelPhnProCode = BigDecimal.ZERO;
            BigDecimal fMoneyChargesCumGelPhnProCode = BigDecimal.ZERO;
            BigDecimal bookBalancePhnProCode = BigDecimal.ZERO;
            BigDecimal merchantBookedBalancePhnProCode = BigDecimal.ZERO;

            int countProductCodeTrans;
            String phnProdCode = rq.getPhoneNumber() + rq.getProductCode();

            if (walletFundingSucessInfoRepo.findTopByOrderByIdDesc() != null) {
                BigDecimal balance = BigDecimal.ZERO;
                BigDecimal bookBalance = BigDecimal.ZERO;
                BigDecimal merchantBookedBalance = BigDecimal.ZERO;
                if (walletFundingSucessInfoRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Getting phonenumber to update wallet's cummulatives", phonenumber));
                    WalletFundSucInfo result;

                    Optional<WalletFundSucInfo> getTrans = walletFundingSucessInfoRepo.findByPhoneNumber(phonenumber);
                    result = getTrans.get();
                    w_amountPaidInCum = result.getAmountPaidInCum().add(dataVerifyateGetAmount);
                    w_swChargesCum = result.getSwChargesCum().add(getVerifyChargeAmount);
                    w_fMoneyChargesCum = result.getDemoPayChargesCum().add(fMoneyCharges);
                    w_amtCreToWalletCum = result.getAmtCreToWalletCum().add(balForWallet);

                }
                logger.info(String.format("Data exist in WalletFundSucInfo Table"));

                List<GenLedgAccount> getDeee = _genLedgAccountRepo.findByPhoneNumberProdCode(rq.getPhoneNumber(), rq.getProductCode());

                if (getDeee.size() > 0) {

                    logger.info(String.format("Getting phonenumber to update Gen Ledger Cummulative", phonenumber));
                    GenLedgAccount genLedResult;
                    Optional<GenLedgAccount> getGenLedgerTrans = _genLedgAccountRepo.findByOptPhoneNumberProdCode(rq.getPhoneNumberProductCode());
                    genLedResult = getGenLedgerTrans.get();

                    accountCreditCumPhnProCode = genLedResult.getAccountCreditCum().add(accountCredit);
                    accountDebitCumPhnProCode = genLedResult.getAccountDebitCum().add(accountDebit);
                    swChargesCumGelPhnProCode = genLedResult.getSwChargesCum().add(swChargesGel);
                    fMoneyChargesCumGelPhnProCode = genLedResult.getDemoPayChargesCum().add(fMoneyChargesGel);
                    balancePhnProCode = genLedResult.getBalance().add(accountCredit);
                    bookBalancePhnProCode = genLedResult.getBookBalance().add(rq.getTransAmount());
                    merchantBookedBalancePhnProCode = genLedResult.getMerchantBookedBalance().add(merchantBookedBalance);

                } else {
                    bookBalancePhnProCode = rq.getTransAmount();
                    merchantBookedBalancePhnProCode = BigDecimal.ZERO;

                }

                if (_genLedgAccountRepo.existsByPhoneNumber(phonenumber)) {

                    logger.info(String.format("Getting phonenumber to update Gen Ledger Cummulative", phonenumber));
                    GenLedgAccount genLedResult;
                    Optional<GenLedgAccount> getGenLedgerTrans = _genLedgAccountRepo.findByPhoneNumber(phonenumber);
                    genLedResult = getGenLedgerTrans.get();

                    accountCreditCum = genLedResult.getAccountCreditCum().add(accountCredit);

                    accountDebitCum = genLedResult.getAccountDebitCum().add(accountDebit);
                    swChargesCumGel = genLedResult.getSwChargesCum().add(swChargesGel);
                    fMoneyChargesCumGel = genLedResult.getDemoPayChargesCum().add(fMoneyChargesGel);
                    balance = genLedResult.getBalance().add(accountCredit);

                    bookBalance = genLedResult.getBookBalance().add(rq.getTransAmount());
                    merchantBookedBalance = genLedResult.getMerchantBookedBalance().add(merchantBookedBalance);

                } else {

                    bookBalance = rq.getTransAmount();
                    merchantBookedBalance = BigDecimal.ZERO;
                }

                if (_genLedgAccountRepo.existsByProductCode(rq.getProductCode())) {

                    logger.info(String.format("Getting productCode to update Gen Ledger Cummulative"));
                    GenLedgAccount genLedResult;
                    Optional<GenLedgAccount> getGenLedgerTrans = _genLedgAccountRepo.findByProductCode(rq.getProductCode());
                    genLedResult = getGenLedgerTrans.get();
                    productCodeFeeCum = genLedResult.getDemoPayChargesCum().add(fMoneyChargesGel);
                    countProductCodeTrans = genLedResult.getCountProductCodeTrans() + 1;

                } else {

                    productCodeFeeCum = fMoneyChargesGel;
                    countProductCodeTrans = +1;
                }

                logger.info(String.format("Data exist in GenLedgAccount Table"));

                GenLedgAccount genLedgAccount = _genLedgAccountRepo.findTopByOrderByIdDesc();
                pl_cum_AccountCredit = genLedgAccount.getPl_cum_AccountCredit().add(accountCredit);
                pl_cum_AccountDebit = genLedgAccount.getPl_cum_AccountDebit().add(accountDebit);
                pl_cum_swChargesGel = genLedgAccount.getPl_cum_swCharges().add(swChargesGel);
                pl_cum_fMoneyChargesGel = genLedgAccount.getPl_cum_fMoneyCharges().add(fMoneyChargesGel);

                //save to gen ledger...
                logger.info(String.format("Saving to General Ledger Table"));
                GenLedgAccount genLedger = new GenLedgAccount(fMoneyTransId, phonenumber, fundingType,
                        accountCredit, balance, bookBalance, accountCreditCum,
                        pl_cum_AccountCredit, accountDebit, accountDebitCum, pl_cum_AccountDebit, swChargesGel, swChargesCumGel, pl_cum_swChargesGel,
                        fMoneyChargesGel, pl_cum_fMoneyChargesGel, fMoneyChargesCumGel, narration,
                        merchantBookedBalance,
                        rq.getProductCode(), rq.getProductName(), countProductCodeTrans,
                        productCodeFeeCum, rq.getPhoneNumberProductCode(), balancePhnProCode, accountCreditCumPhnProCode, accountDebitCumPhnProCode,
                        swChargesCumGelPhnProCode, fMoneyChargesCumGelPhnProCode,
                        bookBalancePhnProCode, merchantBookedBalancePhnProCode);
                _genLedgAccountRepo.save(genLedger);

                //save to WalletFundSucInfo for Indirect Funding
                WalletFundSucInfo wallSucessInfoResult = walletFundingSucessInfoRepo.findTopByOrderByIdDesc();
                p_amountPaidInCum = wallSucessInfoResult.getAmountPaidInCum().add(dataVerifyateGetAmount);
                p_swChargesCum = wallSucessInfoResult.getSwChargesCum().add(getVerifyChargeAmount);
                p_fMoneyChargesCum = wallSucessInfoResult.getDemoPayChargesCum().add(fMoneyCharges);
                p_amtCreToWalletCum = wallSucessInfoResult.getAmtCreToWalletCum().add(balForWallet);
                logger.info(String.format("Saving to WalletFundSucInfo Table"));

                WalletFundSucInfo wallSucessInfo = new WalletFundSucInfo(
                        dataVerifyateGetAmount, w_amountPaidInCum, p_amountPaidInCum,
                        getVerifyChargeAmount, w_swChargesCum, p_swChargesCum,
                        fMoneyCharges, w_fMoneyChargesCum, p_fMoneyChargesCum,
                        balForWallet, w_amtCreToWalletCum, p_amtCreToWalletCum,
                        phonenumber, fMoneyTransId,
                        rq.getSwRefrenceNumber(), "No Validation from Bank", rq.getSwRefrenceNumber(), rq.getSwRefrenceNumber()
                );
                walletFundingSucessInfoRepo.save(wallSucessInfo);
                //save to gen ledger cummulative...
                GenLedgAccountCum genLedgAccountCum;

                if (_genLedgAccountCumRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Saving to GenLedgAccountCum Table phoneNumber exists"));
                    Optional<GenLedgAccountCum> getRecordGenLedgAccountCumCum = _genLedgAccountCumRepo.findByPhoneNumber(phonenumber);
                    genLedgAccountCum = getRecordGenLedgAccountCumCum.get();
                    //on the awating final release for credit I will not increase count, wen they finally release I will increase count
                    //if they roll back I will leave without increase count
                    genLedgAccountCum.setCountSuccessTrans(genLedgAccountCum.getCountSuccessTrans());
                    genLedgAccountCum.setTotalAmountCredited(genLedgAccountCum.getTotalAmountCredited().add(accountCredit));
                    genLedgAccountCum.setTotalAmountDebited(genLedgAccountCum.getTotalAmountDebited().add(accountDebit));
                    genLedgAccountCum.setTotalPayCharge(genLedgAccountCum.getTotalPayCharge().add(fMoneyChargesGel));
                    genLedgAccountCum.setTotalSwCharges(genLedgAccountCum.getTotalSwCharges().add(swChargesGel));
                    genLedgAccountCum.setTotalBalance(genLedgAccountCum.getTotalBalance().add(accountCredit));
                    genLedgAccountCum.setTotalBookBalance(genLedgAccountCum.getTotalBookBalance().add(rq.getTransAmount()));
                    genLedgAccountCum.setTotalMerchantBookedBalance(genLedgAccountCum.getTotalMerchantBookedBalance());

                    _genLedgAccountCumRepo.save(genLedgAccountCum);

                }
                if (_genLedgAccountCumRepo.existsByPhnProductCode(rq.getPhoneNumber() + rq.getProductCode())) {
                    logger.info(String.format("Saving to GenLedgAccountCum Table phone-productcode exists"));

                    Optional<GenLedgAccountCum> getRecordGenLedgAccountCumCum = _genLedgAccountCumRepo.findByPhnProductCode(phnProdCode);
                    genLedgAccountCum = getRecordGenLedgAccountCumCum.get();
                    //on the awating final release for credit I will not increase count, wen they finally release I will increase count
                    //if they roll back I will leave without increase count
                    genLedgAccountCum.setCountSuccTransPhnProCode(genLedgAccountCum.getCountSuccTransPhnProCode());
                    genLedgAccountCum.setTotalAmtCreditedPhnProCode(genLedgAccountCum.getTotalAmtCreditedPhnProCode().add(accountCredit));
                    genLedgAccountCum.setTotalAmtDebitedPhnProCode(genLedgAccountCum.getTotalAmtDebitedPhnProCode().add(accountDebit));
                    genLedgAccountCum.setTotalPayChargePhnProCode(genLedgAccountCum.getTotalPayChargePhnProCode().add(fMoneyChargesGel));
                    genLedgAccountCum.setTotalSwChargesPhnProCode(genLedgAccountCum.getTotalSwChargesPhnProCode().add(swChargesGel));
                    genLedgAccountCum.setTotalBalancePhnProCode(genLedgAccountCum.getTotalBalancePhnProCode().add(accountCredit));
                    genLedgAccountCum.setTotalBookBalancePhnProCode(genLedgAccountCum.getTotalBookBalancePhnProCode().add(rq.getTransAmount()));
                    genLedgAccountCum.setTotalMerBookedBalPhnProCode(genLedgAccountCum.getTotalMerBookedBalPhnProCode());

                    _genLedgAccountCumRepo.save(genLedgAccountCum);

                } else {

                    logger.info(String.format("Saving to genLedgAccountCum Table phoneNumber first entry"));
                    GenLedgAccountCum genLedgAcct = new GenLedgAccountCum(
                            phonenumber, phonenumber, 1, accountCredit, accountCredit, BigDecimal.ZERO, accountDebit,
                            swChargesGel, fMoneyChargesGel, merchantBookedBalance, phnProdCode, 1, accountCredit, accountCredit, BigDecimal.ZERO, accountDebit,
                            swChargesGel, fMoneyChargesGel, merchantBookedBalance
                    );
                    _genLedgAccountCumRepo.save(genLedgAcct);
                }

                WalletFundingInfoCum walletFundingInfoCum;

                if (walletFundingInfoCumRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Saving to WalletFundingInfoCum Table phoneNumber exists"));
                    Optional<WalletFundingInfoCum> getRecordWalletFundingInfoCumRe = walletFundingInfoCumRepo.findByPhoneNumber(phonenumber);

                    walletFundingInfoCum = getRecordWalletFundingInfoCumRe.get();
                    //on the awating final release for credit I will not increase count, wen they finally release I will increase count
                    //if they roll back I will leave without increase count
                    walletFundingInfoCum.setCountSuccessTrans(walletFundingInfoCum.getCountSuccessTrans());
                    walletFundingInfoCum.setTotalAmountPaidIn(walletFundingInfoCum.getTotalAmountPaidIn().add(dataVerifyateGetAmount));
                    walletFundingInfoCum.setTotalAmtCreToWallet(walletFundingInfoCum.getTotalAmtCreToWallet().add(balForWallet));
                    walletFundingInfoCum.setTotalFMoneyChrge(walletFundingInfoCum.getTotalFMoneyChrge().add(fMoneyCharges));
                    walletFundingInfoCum.setTotalSwCharges(walletFundingInfoCum.getTotalSwCharges().add(getVerifyChargeAmount));
                    walletFundingInfoCumRepo.save(walletFundingInfoCum);
                    //send SMS to subscriber
                    // responseModel.setDescription("Request initiated successfully");
                    responseModel.setStatusCode(200);
                } else {
                    logger.info(String.format("Saving to WalletFundingInfoCum Table phoneNumber first entry"));
                    WalletFundingInfoCum wallFundingCum = new WalletFundingInfoCum(
                            phonenumber, phonenumber, 1,
                            dataVerifyateGetAmount, getVerifyChargeAmount, fMoneyCharges,
                            balForWallet
                    );
                    walletFundingInfoCumRepo.save(wallFundingCum);
                    //send SMS to subscriber

                    responseModel.setStatusCode(200);
                }

            } else {

                logger.info(String.format("Data does not exist in WalletFundSucInfo and GenLedgAccount Table"));

                //save to gen ledger...
                logger.info(String.format("Saving to General-Ledger Table"));
                GenLedgAccount genLedger = new GenLedgAccount(fMoneyTransId, phonenumber, "In-Direct Wallet Funding",
                        accountCredit,
                        accountCredit, rq.getTransAmount(), accountCredit,
                        accountCredit, accountDebit, accountDebit, accountDebit, swChargesGel, swChargesGel, swChargesGel,
                        fMoneyChargesGel, fMoneyChargesGel, fMoneyChargesGel, narration, BigDecimal.ZERO,
                        rq.getProductCode(), rq.getProductName(), 1,
                        fMoneyChargesGel, rq.getPhoneNumberProductCode(), balancePhnProCode, accountCreditCumPhnProCode, accountDebitCumPhnProCode,
                        swChargesCumGelPhnProCode, fMoneyChargesCumGelPhnProCode, bookBalancePhnProCode, merchantBookedBalancePhnProCode);
                _genLedgAccountRepo.save(genLedger);

                logger.info(String.format("Saving to WalletFundSucInfo Table"));
                WalletFundSucInfo wallSucessInfo = new WalletFundSucInfo(
                        dataVerifyateGetAmount, dataVerifyateGetAmount, dataVerifyateGetAmount,
                        getVerifyChargeAmount, getVerifyChargeAmount, getVerifyChargeAmount,
                        fMoneyCharges, fMoneyCharges, fMoneyCharges,
                        balForWallet, balForWallet, balForWallet,
                        phonenumber, fMoneyTransId,
                        rq.getSwRefrenceNumber(), "No Validation from Bank", rq.getSwRefrenceNumber(), rq.getSwRefrenceNumber()
                );
                walletFundingSucessInfoRepo.save(wallSucessInfo);

                WalletFundingInfoCum walletFundingInfoCum;

                if (walletFundingInfoCumRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Saving to WalletFundingInfoCum Table phoneNumber exists"));
                    Optional<WalletFundingInfoCum> getRecordWalletFundingInfoCumRe = walletFundingInfoCumRepo.findByPhoneNumber(phonenumber);

                    walletFundingInfoCum = getRecordWalletFundingInfoCumRe.get();
                    //on the awating final release for credit I will not increase count, wen they finally release I will increase count
                    //if they roll back I will leave without increase count
                    walletFundingInfoCum.setCountSuccessTrans(walletFundingInfoCum.getCountSuccessTrans());
                    walletFundingInfoCum.setTotalAmountPaidIn(walletFundingInfoCum.getTotalAmountPaidIn().add(dataVerifyateGetAmount));
                    walletFundingInfoCum.setTotalAmtCreToWallet(walletFundingInfoCum.getTotalAmtCreToWallet().add(balForWallet));
                    walletFundingInfoCum.setTotalFMoneyChrge(walletFundingInfoCum.getTotalFMoneyChrge().add(fMoneyCharges));
                    walletFundingInfoCum.setTotalSwCharges(walletFundingInfoCum.getTotalSwCharges().add(getVerifyChargeAmount));
                    walletFundingInfoCumRepo.save(walletFundingInfoCum);
                    //send SMS to subscriber
                    responseModel.setStatusCode(200);
                } else {
                    logger.info(String.format("Saving to WalletFundingInfoCum Table phoneNumber first entry"));
                    WalletFundingInfoCum wallFundingCum = new WalletFundingInfoCum(
                            phonenumber, phonenumber, 1,
                            dataVerifyateGetAmount, getVerifyChargeAmount, fMoneyCharges,
                            balForWallet
                    );
                    walletFundingInfoCumRepo.save(wallFundingCum);
                    //send SMS to subscriber
                    responseModel.setStatusCode(200);
                }
                GenLedgAccountCum genLedgAccountCum;

                if (_genLedgAccountCumRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Saving to GenLedgAccountCum Table phoneNumber exists"));
                    Optional<GenLedgAccountCum> getRecordGenLedgAccountCumCum = _genLedgAccountCumRepo.findByPhoneNumber(phonenumber);
                    genLedgAccountCum = getRecordGenLedgAccountCumCum.get();
                    //on the awating final release for credit I will not increase count, wen they finally release I will increase count
                    //if they roll back I will leave without increase count

                    genLedgAccountCum.setCountSuccessTrans(genLedgAccountCum.getCountSuccessTrans());
                    genLedgAccountCum.setTotalAmountCredited(genLedgAccountCum.getTotalAmountCredited().add(accountCredit));
                    genLedgAccountCum.setTotalAmountDebited(genLedgAccountCum.getTotalAmountDebited().add(accountDebit));
                    genLedgAccountCum.setTotalPayCharge(genLedgAccountCum.getTotalPayCharge().add(fMoneyChargesGel));
                    genLedgAccountCum.setTotalSwCharges(genLedgAccountCum.getTotalSwCharges().add(swChargesGel));
                    genLedgAccountCum.setTotalBalance(genLedgAccountCum.getTotalBalance().add(accountCredit));
                    genLedgAccountCum.setTotalBookBalance(genLedgAccountCum.getTotalBookBalance().add(rq.getTransAmount()));
                    genLedgAccountCum.setTotalMerchantBookedBalance(genLedgAccountCum.getTotalMerchantBookedBalance());

                    _genLedgAccountCumRepo.save(genLedgAccountCum);

                }

                if (_genLedgAccountCumRepo.existsByPhnProductCode(rq.getPhoneNumber() + rq.getProductCode())) {
                    logger.info(String.format("Saving to GenLedgAccountCum Table phone-productcode exists"));
                    Optional<GenLedgAccountCum> getRecordGenLedgAccountCumCum = _genLedgAccountCumRepo.findByPhnProductCode(phnProdCode);
                    genLedgAccountCum = getRecordGenLedgAccountCumCum.get();
                    //on the awating final release for credit I will not increase count, wen they finally release I will increase count
                    //if they roll back I will leave without increase count

                    genLedgAccountCum.setCountSuccTransPhnProCode(genLedgAccountCum.getCountSuccTransPhnProCode());
                    genLedgAccountCum.setTotalAmtCreditedPhnProCode(genLedgAccountCum.getTotalAmtCreditedPhnProCode().add(accountCredit));
                    genLedgAccountCum.setTotalAmtDebitedPhnProCode(genLedgAccountCum.getTotalAmtDebitedPhnProCode().add(accountDebit));
                    genLedgAccountCum.setTotalPayChargePhnProCode(genLedgAccountCum.getTotalPayChargePhnProCode().add(fMoneyChargesGel));
                    genLedgAccountCum.setTotalSwChargesPhnProCode(genLedgAccountCum.getTotalSwChargesPhnProCode().add(swChargesGel));
                    genLedgAccountCum.setTotalBalancePhnProCode(genLedgAccountCum.getTotalBalancePhnProCode().add(accountCredit));
                    genLedgAccountCum.setTotalBookBalancePhnProCode(genLedgAccountCum.getTotalBookBalancePhnProCode().add(rq.getTransAmount()));
                    genLedgAccountCum.setTotalMerBookedBalPhnProCode(genLedgAccountCum.getTotalMerBookedBalPhnProCode());

                    _genLedgAccountCumRepo.save(genLedgAccountCum);

                } else {

                    logger.info(String.format("Saving to genLedgAccountCum Table phoneNumber first entry"));
                    GenLedgAccountCum genLedgAcct = new GenLedgAccountCum(
                            phonenumber, phonenumber, 1, accountCredit, accountCredit, BigDecimal.ZERO, accountDebit,
                            swChargesGel, fMoneyChargesGel, BigDecimal.ZERO, phnProdCode, 1, accountCredit, accountCredit, BigDecimal.ZERO, accountDebit,
                            swChargesGel, fMoneyChargesGel, BigDecimal.ZERO
                    );
                    _genLedgAccountCumRepo.save(genLedgAcct);
                }
                responseModel.setStatusCode(200);
            }
        } catch (Exception ex) {
            responseModel.setDescription(statusMessage);
            responseModel.setStatusCode(statusCode);
            ex.printStackTrace();
        }

        return responseModel;

    }

    //escrowCreditLedgerOneTimeReleased
    public BaseResponse escrowCreditLedgerOneTimeReleased(ProcLedgerRequestCreditOneTime rq) {

        BaseResponse responseModel = new BaseResponse();
        int statusCode = 500;
        String statusMessage = "An error occured,please try again";
        try {
            statusCode = 400;

            BigDecimal fMoneyCharges = BigDecimal.ZERO;
            String phonenumber = rq.getPhoneNumber();
            String fundingType = rq.getFundingType();
            String narration = rq.getNarration();
            String fMoneyTransId = rq.getKulTransactionId();
            BigDecimal dataVerifyateGetAmount = rq.getTransAmount(); //deduct rq.getTransAmount() here 
            BigDecimal getVerifyChargeAmount = rq.getSwFees();
            BigDecimal amountLeftBeforeFMoneyCharges = dataVerifyateGetAmount.subtract(getVerifyChargeAmount);
            BigDecimal w_amountPaidInCum = BigDecimal.ZERO;
            BigDecimal w_swChargesCum = BigDecimal.ZERO;
            BigDecimal w_fMoneyChargesCum = BigDecimal.ZERO;
            BigDecimal w_amtCreToWalletCum = BigDecimal.ZERO;

            BigDecimal p_swChargesCum = BigDecimal.ZERO;
            BigDecimal p_fMoneyChargesCum = BigDecimal.ZERO;
            BigDecimal p_amtCreToWalletCum = BigDecimal.ZERO;
            BigDecimal p_amountPaidInCum = BigDecimal.ZERO;
            BigDecimal balForWallet = amountLeftBeforeFMoneyCharges.subtract(fMoneyCharges);

            BigDecimal accountCredit = rq.getTransAmount();
            BigDecimal accountCreditCum = BigDecimal.ZERO;
            BigDecimal pl_cum_AccountCredit = BigDecimal.ZERO;
            BigDecimal accountDebit = BigDecimal.ZERO;
            BigDecimal accountDebitCum = BigDecimal.ZERO;
            BigDecimal pl_cum_AccountDebit = BigDecimal.ZERO;
            BigDecimal swChargesGel = rq.getSwFees();
            BigDecimal swChargesCumGel = BigDecimal.ZERO;
            BigDecimal pl_cum_swChargesGel = BigDecimal.ZERO;
            BigDecimal fMoneyChargesGel = fMoneyCharges;
            BigDecimal pl_cum_fMoneyChargesGel = BigDecimal.ZERO;
            BigDecimal fMoneyChargesCumGel = BigDecimal.ZERO;
            BigDecimal productCodeFeeCum = BigDecimal.ZERO;
            int countProductCodeTrans = 0;

            BigDecimal balancePhnProCode = BigDecimal.ZERO;
            BigDecimal accountCreditCumPhnProCode = BigDecimal.ZERO;
            BigDecimal accountDebitCumPhnProCode = BigDecimal.ZERO;
            BigDecimal swChargesCumGelPhnProCode = BigDecimal.ZERO;
            BigDecimal fMoneyChargesCumGelPhnProCode = BigDecimal.ZERO;
            BigDecimal bookBalancePhnProCode = BigDecimal.ZERO;
            BigDecimal merchantBookedBalancePhnProCode = BigDecimal.ZERO;
            String phnProdCode = rq.getPhoneNumber() + rq.getProductCode();

            if (walletFundingSucessInfoRepo.findTopByOrderByIdDesc() != null) {
                BigDecimal balance = BigDecimal.ZERO;
                BigDecimal bookBalance = BigDecimal.ZERO;
                BigDecimal merchantBookedBalance = BigDecimal.ZERO;

                if (walletFundingSucessInfoRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Getting phonenumber to update wallet's cummulatives", phonenumber));
                    WalletFundSucInfo result;

                    Optional<WalletFundSucInfo> getTrans = walletFundingSucessInfoRepo.findByPhoneNumber(phonenumber);
                    result = getTrans.get();
                    w_amountPaidInCum = result.getAmountPaidInCum().add(dataVerifyateGetAmount);
                    w_swChargesCum = result.getSwChargesCum().add(getVerifyChargeAmount);
                    w_fMoneyChargesCum = result.getDemoPayChargesCum().add(fMoneyCharges);
                    w_amtCreToWalletCum = result.getAmtCreToWalletCum().add(balForWallet);

                }
                logger.info(String.format("Data exist in WalletFundSucInfo Table"));

                List<GenLedgAccount> getDeee = _genLedgAccountRepo.findByPhoneNumberProdCode(rq.getPhoneNumber(), rq.getProductCode());

                if (getDeee.size() > 0) {

                    logger.info(String.format("Getting phonenumber to update Gen Ledger Cummulative", phonenumber));
                    GenLedgAccount genLedResult;
                    Optional<GenLedgAccount> getGenLedgerTrans = _genLedgAccountRepo.findByOptPhoneNumberProdCode(rq.getPhoneNumberProductCode());
                    genLedResult = getGenLedgerTrans.get();

                    accountCreditCumPhnProCode = genLedResult.getAccountCreditCum().add(accountCredit);
                    accountDebitCumPhnProCode = genLedResult.getAccountDebitCum().add(accountDebit);
                    swChargesCumGelPhnProCode = genLedResult.getSwChargesCum().add(swChargesGel);
                    fMoneyChargesCumGelPhnProCode = genLedResult.getDemoPayChargesCum().add(fMoneyChargesGel);
                    balancePhnProCode = genLedResult.getBalance().add(accountCredit);
                    bookBalancePhnProCode = genLedResult.getBookBalance().subtract(rq.getTransAmount());
                    merchantBookedBalancePhnProCode = genLedResult.getMerchantBookedBalance().add(merchantBookedBalance);

                }

                if (_genLedgAccountRepo.existsByPhoneNumber(phonenumber)) {

                    logger.info(String.format("Getting phonenumber to update Gen Ledger Cummulative", phonenumber));
                    GenLedgAccount genLedResult;
                    Optional<GenLedgAccount> getGenLedgerTrans = _genLedgAccountRepo.findByPhoneNumber(phonenumber);
                    genLedResult = getGenLedgerTrans.get();

                    accountCreditCum = genLedResult.getAccountCreditCum().add(accountCredit);

                    accountDebitCum = genLedResult.getAccountDebitCum().add(accountDebit);
                    swChargesCumGel = genLedResult.getSwChargesCum().add(swChargesGel);
                    fMoneyChargesCumGel = genLedResult.getDemoPayChargesCum().add(fMoneyChargesGel);
                    balance = genLedResult.getBalance().add(accountCredit);

                    bookBalance = genLedResult.getBookBalance().subtract(rq.getTransAmount());
                    merchantBookedBalance = genLedResult.getMerchantBookedBalance().add(merchantBookedBalance);

                }

                if (_genLedgAccountRepo.existsByProductCode(rq.getProductCode())) {

                    logger.info(String.format("Getting productCode to update Gen Ledger Cummulative"));
                    GenLedgAccount genLedResult;
                    Optional<GenLedgAccount> getGenLedgerTrans = _genLedgAccountRepo.findByProductCode(rq.getProductCode());
                    genLedResult = getGenLedgerTrans.get();
                    productCodeFeeCum = genLedResult.getDemoPayChargesCum().add(fMoneyChargesGel);
                    countProductCodeTrans = genLedResult.getCountProductCodeTrans() + 1;

                }

                logger.info(String.format("Data exist in GenLedgAccount Table"));

                GenLedgAccount genLedgAccount = _genLedgAccountRepo.findTopByOrderByIdDesc();
                pl_cum_AccountCredit = genLedgAccount.getPl_cum_AccountCredit().add(accountCredit);
                pl_cum_AccountDebit = genLedgAccount.getPl_cum_AccountDebit().add(accountDebit);
                pl_cum_swChargesGel = genLedgAccount.getPl_cum_swCharges().add(swChargesGel);
                pl_cum_fMoneyChargesGel = genLedgAccount.getPl_cum_fMoneyCharges().add(fMoneyChargesGel);

                //save to gen ledger...
                logger.info(String.format("Saving to General Ledger Table"));
                GenLedgAccount genLedger = new GenLedgAccount(fMoneyTransId, phonenumber,
                        fundingType, accountCredit, balance, bookBalance, accountCreditCum,
                        pl_cum_AccountCredit, accountDebit, accountDebitCum, pl_cum_AccountDebit, swChargesGel, swChargesCumGel, pl_cum_swChargesGel,
                        fMoneyChargesGel, pl_cum_fMoneyChargesGel, fMoneyChargesCumGel,
                        narration, merchantBookedBalance,
                        rq.getProductCode(), rq.getProductName(), countProductCodeTrans,
                        productCodeFeeCum, rq.getPhoneNumberProductCode(), balancePhnProCode, accountCreditCumPhnProCode, accountDebitCumPhnProCode,
                        swChargesCumGelPhnProCode, fMoneyChargesCumGelPhnProCode,
                        bookBalancePhnProCode, merchantBookedBalancePhnProCode);
                _genLedgAccountRepo.save(genLedger);

                //save to WalletFundSucInfo for Indirect Funding
                WalletFundSucInfo wallSucessInfoResult = walletFundingSucessInfoRepo.findTopByOrderByIdDesc();
                p_amountPaidInCum = wallSucessInfoResult.getAmountPaidInCum().add(dataVerifyateGetAmount);
                p_swChargesCum = wallSucessInfoResult.getSwChargesCum().add(getVerifyChargeAmount);
                p_fMoneyChargesCum = wallSucessInfoResult.getDemoPayChargesCum().add(fMoneyCharges);
                p_amtCreToWalletCum = wallSucessInfoResult.getAmtCreToWalletCum().add(balForWallet);
                logger.info(String.format("Saving to WalletFundSucInfo Table"));

                WalletFundSucInfo wallSucessInfo = new WalletFundSucInfo(
                        dataVerifyateGetAmount, w_amountPaidInCum, p_amountPaidInCum,
                        getVerifyChargeAmount, w_swChargesCum, p_swChargesCum,
                        fMoneyCharges, w_fMoneyChargesCum, p_fMoneyChargesCum,
                        balForWallet, w_amtCreToWalletCum, p_amtCreToWalletCum,
                        phonenumber, fMoneyTransId,
                        rq.getSwRefrenceNumber(), "No Validation from Bank", rq.getSwRefrenceNumber(), rq.getSwRefrenceNumber()
                );
                walletFundingSucessInfoRepo.save(wallSucessInfo);
                //save to gen ledger cummulative...
                GenLedgAccountCum genLedgAccountCum;

                if (_genLedgAccountCumRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Saving to GenLedgAccountCum Table phoneNumber exists"));
                    Optional<GenLedgAccountCum> getRecordGenLedgAccountCumCum = _genLedgAccountCumRepo.findByPhoneNumber(phonenumber);
                    genLedgAccountCum = getRecordGenLedgAccountCumCum.get();
                    //wen they finally release I will increase count

                    genLedgAccountCum.setCountSuccessTrans(genLedgAccountCum.getCountSuccessTrans() + 1);
                    genLedgAccountCum.setTotalAmountCredited(genLedgAccountCum.getTotalAmountCredited().add(accountCredit));
                    genLedgAccountCum.setTotalAmountDebited(genLedgAccountCum.getTotalAmountDebited().add(accountDebit));
                    genLedgAccountCum.setTotalPayCharge(genLedgAccountCum.getTotalPayCharge().add(fMoneyChargesGel));
                    genLedgAccountCum.setTotalSwCharges(genLedgAccountCum.getTotalSwCharges().add(swChargesGel));
                    genLedgAccountCum.setTotalBalance(genLedgAccountCum.getTotalBalance().add(accountCredit));
                    genLedgAccountCum.setTotalBookBalance(genLedgAccountCum.getTotalBookBalance().subtract(rq.getTransAmount()));
                    genLedgAccountCum.setTotalMerchantBookedBalance(genLedgAccountCum.getTotalMerchantBookedBalance());

                    _genLedgAccountCumRepo.save(genLedgAccountCum);

                }

                if (_genLedgAccountCumRepo.existsByPhnProductCode(rq.getPhoneNumber() + rq.getProductCode())) {
                    logger.info(String.format("Saving to GenLedgAccountCum Table phone-productcode exists"));

                    Optional<GenLedgAccountCum> getRecordGenLedgAccountCumCum = _genLedgAccountCumRepo.findByPhnProductCode(phnProdCode);
                    genLedgAccountCum = getRecordGenLedgAccountCumCum.get();
                    //wen they finally release I will increase count

                    genLedgAccountCum.setCountSuccTransPhnProCode(genLedgAccountCum.getCountSuccTransPhnProCode() + 1);
                    genLedgAccountCum.setTotalAmtCreditedPhnProCode(genLedgAccountCum.getTotalAmtCreditedPhnProCode().add(accountCredit));
                    genLedgAccountCum.setTotalAmtDebitedPhnProCode(genLedgAccountCum.getTotalAmtDebitedPhnProCode().add(accountDebit));
                    genLedgAccountCum.setTotalPayChargePhnProCode(genLedgAccountCum.getTotalPayChargePhnProCode().add(fMoneyChargesGel));
                    genLedgAccountCum.setTotalSwChargesPhnProCode(genLedgAccountCum.getTotalSwChargesPhnProCode().add(swChargesGel));
                    genLedgAccountCum.setTotalBalancePhnProCode(genLedgAccountCum.getTotalBalancePhnProCode().add(accountCredit));
                    genLedgAccountCum.setTotalBookBalancePhnProCode(genLedgAccountCum.getTotalBookBalancePhnProCode().subtract(rq.getTransAmount()));
                    genLedgAccountCum.setTotalMerBookedBalPhnProCode(genLedgAccountCum.getTotalMerBookedBalPhnProCode());

                    _genLedgAccountCumRepo.save(genLedgAccountCum);

                } else {

                    logger.info(String.format("Saving to genLedgAccountCum Table phoneNumber first entry"));
                    GenLedgAccountCum genLedgAcct = new GenLedgAccountCum(
                            phonenumber, phonenumber, 1, accountCredit, accountCredit, BigDecimal.ZERO, accountDebit,
                            swChargesGel, fMoneyChargesGel, BigDecimal.ZERO, phnProdCode, 1, accountCredit, accountCredit, BigDecimal.ZERO, accountDebit,
                            swChargesGel, fMoneyChargesGel, BigDecimal.ZERO
                    );
                    _genLedgAccountCumRepo.save(genLedgAcct);
                }

                WalletFundingInfoCum walletFundingInfoCum;

                if (walletFundingInfoCumRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Saving to WalletFundingInfoCum Table phoneNumber exists"));
                    Optional<WalletFundingInfoCum> getRecordWalletFundingInfoCumRe = walletFundingInfoCumRepo.findByPhoneNumber(phonenumber);

                    walletFundingInfoCum = getRecordWalletFundingInfoCumRe.get();
                    // wen they finally release I will increase count

                    walletFundingInfoCum.setCountSuccessTrans(walletFundingInfoCum.getCountSuccessTrans() + 1);
                    walletFundingInfoCum.setTotalAmountPaidIn(walletFundingInfoCum.getTotalAmountPaidIn().add(dataVerifyateGetAmount));
                    walletFundingInfoCum.setTotalAmtCreToWallet(walletFundingInfoCum.getTotalAmtCreToWallet().add(balForWallet));
                    walletFundingInfoCum.setTotalFMoneyChrge(walletFundingInfoCum.getTotalFMoneyChrge().add(fMoneyCharges));
                    walletFundingInfoCum.setTotalSwCharges(walletFundingInfoCum.getTotalSwCharges().add(getVerifyChargeAmount));
                    walletFundingInfoCumRepo.save(walletFundingInfoCum);
                    //send SMS to subscriber
                    // responseModel.setDescription("Request initiated successfully");
                    responseModel.setStatusCode(200);
                } else {
                    logger.info(String.format("Saving to WalletFundingInfoCum Table phoneNumber first entry"));
                    WalletFundingInfoCum wallFundingCum = new WalletFundingInfoCum(
                            phonenumber, phonenumber, 1,
                            dataVerifyateGetAmount, getVerifyChargeAmount, fMoneyCharges,
                            balForWallet
                    );
                    walletFundingInfoCumRepo.save(wallFundingCum);
                    //send SMS to subscriber

                    responseModel.setStatusCode(200);
                }

            } else {

                logger.info(String.format("Data does not exist in WalletFundSucInfo and GenLedgAccount Table"));

                //save to gen ledger...
                logger.info(String.format("Saving to General-Ledger Table"));
                GenLedgAccount genLedger = new GenLedgAccount(fMoneyTransId,
                        phonenumber, "In-Direct Wallet Funding", accountCredit,
                        accountCredit, BigDecimal.ZERO, accountCredit,
                        accountCredit, accountDebit, accountDebit, accountDebit, swChargesGel, swChargesGel, swChargesGel,
                        fMoneyChargesGel, fMoneyChargesGel, fMoneyChargesGel, narration, BigDecimal.ZERO,
                        rq.getProductCode(), rq.getProductName(), 1,
                        fMoneyChargesGel, rq.getPhoneNumberProductCode(), balancePhnProCode, accountCreditCumPhnProCode, accountDebitCumPhnProCode,
                        swChargesCumGelPhnProCode, fMoneyChargesCumGelPhnProCode, bookBalancePhnProCode, merchantBookedBalancePhnProCode);
                _genLedgAccountRepo.save(genLedger);

                logger.info(String.format("Saving to WalletFundSucInfo Table"));
                WalletFundSucInfo wallSucessInfo = new WalletFundSucInfo(
                        dataVerifyateGetAmount, dataVerifyateGetAmount, dataVerifyateGetAmount,
                        getVerifyChargeAmount, getVerifyChargeAmount, getVerifyChargeAmount,
                        fMoneyCharges, fMoneyCharges, fMoneyCharges,
                        balForWallet, balForWallet, balForWallet,
                        phonenumber, fMoneyTransId,
                        rq.getSwRefrenceNumber(), "No Validation from Bank", rq.getSwRefrenceNumber(), rq.getSwRefrenceNumber()
                );
                walletFundingSucessInfoRepo.save(wallSucessInfo);

                WalletFundingInfoCum walletFundingInfoCum;

                if (walletFundingInfoCumRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Saving to WalletFundingInfoCum Table phoneNumber exists"));
                    Optional<WalletFundingInfoCum> getRecordWalletFundingInfoCumRe = walletFundingInfoCumRepo.findByPhoneNumber(phonenumber);

                    walletFundingInfoCum = getRecordWalletFundingInfoCumRe.get();
                    // wen they finally release I will increase count

                    walletFundingInfoCum.setCountSuccessTrans(walletFundingInfoCum.getCountSuccessTrans() + 1);
                    walletFundingInfoCum.setTotalAmountPaidIn(walletFundingInfoCum.getTotalAmountPaidIn().add(dataVerifyateGetAmount));
                    walletFundingInfoCum.setTotalAmtCreToWallet(walletFundingInfoCum.getTotalAmtCreToWallet().add(balForWallet));
                    walletFundingInfoCum.setTotalFMoneyChrge(walletFundingInfoCum.getTotalFMoneyChrge().add(fMoneyCharges));
                    walletFundingInfoCum.setTotalSwCharges(walletFundingInfoCum.getTotalSwCharges().add(getVerifyChargeAmount));
                    walletFundingInfoCumRepo.save(walletFundingInfoCum);
                    //send SMS to subscriber
                    responseModel.setStatusCode(200);
                } else {
                    logger.info(String.format("Saving to WalletFundingInfoCum Table phoneNumber first entry"));
                    WalletFundingInfoCum wallFundingCum = new WalletFundingInfoCum(
                            phonenumber, phonenumber, 1,
                            dataVerifyateGetAmount, getVerifyChargeAmount, fMoneyCharges,
                            balForWallet
                    );
                    walletFundingInfoCumRepo.save(wallFundingCum);
                    //send SMS to subscriber
                    responseModel.setStatusCode(200);
                }
                GenLedgAccountCum genLedgAccountCum;

                if (_genLedgAccountCumRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Saving to GenLedgAccountCum Table phoneNumber exists"));
                    Optional<GenLedgAccountCum> getRecordGenLedgAccountCumCum = _genLedgAccountCumRepo.findByPhoneNumber(phonenumber);
                    genLedgAccountCum = getRecordGenLedgAccountCumCum.get();
                    // wen they finally release I will increase count

                    genLedgAccountCum.setCountSuccessTrans(genLedgAccountCum.getCountSuccessTrans() + 1);
                    genLedgAccountCum.setTotalAmountCredited(genLedgAccountCum.getTotalAmountCredited().add(accountCredit));
                    genLedgAccountCum.setTotalAmountDebited(genLedgAccountCum.getTotalAmountDebited().add(accountDebit));
                    genLedgAccountCum.setTotalPayCharge(genLedgAccountCum.getTotalPayCharge().add(fMoneyChargesGel));
                    genLedgAccountCum.setTotalSwCharges(genLedgAccountCum.getTotalSwCharges().add(swChargesGel));
                    genLedgAccountCum.setTotalBalance(genLedgAccountCum.getTotalBalance().add(accountCredit));
                    genLedgAccountCum.setTotalBookBalance(genLedgAccountCum.getTotalBookBalance().subtract(rq.getTransAmount()));
                    genLedgAccountCum.setTotalMerchantBookedBalance(genLedgAccountCum.getTotalMerchantBookedBalance());

                    _genLedgAccountCumRepo.save(genLedgAccountCum);

                }

                if (_genLedgAccountCumRepo.existsByPhnProductCode(rq.getPhoneNumber() + rq.getProductCode())) {
                    logger.info(String.format("Saving to GenLedgAccountCum Table phone-productcode exists"));

                    Optional<GenLedgAccountCum> getRecordGenLedgAccountCumCum = _genLedgAccountCumRepo.findByPhnProductCode(phnProdCode);
                    genLedgAccountCum = getRecordGenLedgAccountCumCum.get();
                    // wen they finally release I will increase count

                    genLedgAccountCum.setCountSuccTransPhnProCode(genLedgAccountCum.getCountSuccTransPhnProCode() + 1);
                    genLedgAccountCum.setTotalAmtCreditedPhnProCode(genLedgAccountCum.getTotalAmtCreditedPhnProCode().add(accountCredit));
                    genLedgAccountCum.setTotalAmtDebitedPhnProCode(genLedgAccountCum.getTotalAmtDebitedPhnProCode().add(accountDebit));
                    genLedgAccountCum.setTotalPayChargePhnProCode(genLedgAccountCum.getTotalPayChargePhnProCode().add(fMoneyChargesGel));
                    genLedgAccountCum.setTotalSwChargesPhnProCode(genLedgAccountCum.getTotalSwChargesPhnProCode().add(swChargesGel));
                    genLedgAccountCum.setTotalBalancePhnProCode(genLedgAccountCum.getTotalBalancePhnProCode().add(accountCredit));
                    genLedgAccountCum.setTotalBookBalancePhnProCode(genLedgAccountCum.getTotalBookBalancePhnProCode().subtract(rq.getTransAmount()));
                    genLedgAccountCum.setTotalMerBookedBalPhnProCode(genLedgAccountCum.getTotalMerBookedBalPhnProCode());

                    _genLedgAccountCumRepo.save(genLedgAccountCum);

                } else {

                    logger.info(String.format("Saving to genLedgAccountCum Table phoneNumber first entry"));
                    GenLedgAccountCum genLedgAcct = new GenLedgAccountCum(
                            phonenumber, phonenumber, 1, accountCredit, accountCredit, BigDecimal.ZERO, accountDebit,
                            swChargesGel, fMoneyChargesGel, BigDecimal.ZERO, phnProdCode, 1, accountCredit, accountCredit, BigDecimal.ZERO, accountDebit,
                            swChargesGel, fMoneyChargesGel, BigDecimal.ZERO
                    );
                    _genLedgAccountCumRepo.save(genLedgAcct);
                }
                responseModel.setStatusCode(200);
            }
        } catch (Exception ex) {
            responseModel.setDescription(statusMessage);
            responseModel.setStatusCode(statusCode);
            ex.printStackTrace();
        }

        return responseModel;

    }

    // escrowCreditLedgerOneTimeRollBackSeller
    public BaseResponse escrowCreditLedgerOneTimeRollBackSeller(ProcLedgerRequestCreditOneTime rq) {

        BaseResponse responseModel = new BaseResponse();
        int statusCode = 500;
        String statusMessage = "An error occured,please try again";
        try {
            statusCode = 400;

            BigDecimal fMoneyCharges = rq.getKulFees();
            String phonenumber = rq.getPhoneNumber();
            String fundingType = rq.getFundingType();
            String narration = rq.getNarration();
            String fMoneyTransId = rq.getKulTransactionId();
            BigDecimal dataVerifyateGetAmount = BigDecimal.ZERO; //deduct rq.getTransAmount() here 
            BigDecimal getVerifyChargeAmount = rq.getSwFees();
            BigDecimal amountLeftBeforeFMoneyCharges = BigDecimal.ZERO;
            BigDecimal w_amountPaidInCum = BigDecimal.ZERO;
            BigDecimal w_swChargesCum = BigDecimal.ZERO;
            BigDecimal w_fMoneyChargesCum = BigDecimal.ZERO;
            BigDecimal w_amtCreToWalletCum = BigDecimal.ZERO;

            BigDecimal p_swChargesCum = BigDecimal.ZERO;
            BigDecimal p_fMoneyChargesCum = BigDecimal.ZERO;
            BigDecimal p_amtCreToWalletCum = BigDecimal.ZERO;
            BigDecimal p_amountPaidInCum = BigDecimal.ZERO;
            BigDecimal balForWallet = BigDecimal.ZERO;

            BigDecimal accountCredit = BigDecimal.ZERO;
            BigDecimal accountCreditCum = BigDecimal.ZERO;
            BigDecimal pl_cum_AccountCredit = BigDecimal.ZERO;
            BigDecimal accountDebit = BigDecimal.ZERO;
            BigDecimal accountDebitCum = BigDecimal.ZERO;
            BigDecimal pl_cum_AccountDebit = BigDecimal.ZERO;
            BigDecimal swChargesGel = rq.getSwFees();
            BigDecimal swChargesCumGel = BigDecimal.ZERO;
            BigDecimal pl_cum_swChargesGel = BigDecimal.ZERO;
            BigDecimal fMoneyChargesGel = fMoneyCharges;
            BigDecimal pl_cum_fMoneyChargesGel = BigDecimal.ZERO;
            BigDecimal fMoneyChargesCumGel = BigDecimal.ZERO;
            BigDecimal productCodeFeeCum = BigDecimal.ZERO;
            int countProductCodeTrans = 0;

            BigDecimal balancePhnProCode = BigDecimal.ZERO;
            BigDecimal accountCreditCumPhnProCode = BigDecimal.ZERO;
            BigDecimal accountDebitCumPhnProCode = BigDecimal.ZERO;
            BigDecimal swChargesCumGelPhnProCode = BigDecimal.ZERO;
            BigDecimal fMoneyChargesCumGelPhnProCode = BigDecimal.ZERO;
            BigDecimal bookBalancePhnProCode = BigDecimal.ZERO;
            BigDecimal merchantBookedBalancePhnProCode = BigDecimal.ZERO;
            String phnProdCode = rq.getPhoneNumber() + rq.getProductCode();

            if (walletFundingSucessInfoRepo.findTopByOrderByIdDesc() != null) {
                BigDecimal balance = BigDecimal.ZERO;
                BigDecimal bookBalance = BigDecimal.ZERO;
                BigDecimal merchantBookedBalance = BigDecimal.ZERO;

                if (walletFundingSucessInfoRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Getting phonenumber to update wallet's cummulatives", phonenumber));
                    WalletFundSucInfo result;

                    Optional<WalletFundSucInfo> getTrans = walletFundingSucessInfoRepo.findByPhoneNumber(phonenumber);
                    result = getTrans.get();
                    w_amountPaidInCum = result.getAmountPaidInCum().add(dataVerifyateGetAmount);
                    w_swChargesCum = result.getSwChargesCum().add(getVerifyChargeAmount);
                    w_fMoneyChargesCum = result.getDemoPayChargesCum().add(fMoneyCharges);
                    w_amtCreToWalletCum = result.getAmtCreToWalletCum().add(balForWallet);

                }
                logger.info(String.format("Data exist in WalletFundSucInfo Table"));

                List<GenLedgAccount> getDeee = _genLedgAccountRepo.findByPhoneNumberProdCode(rq.getPhoneNumber(), rq.getProductCode());

                if (getDeee.size() > 0) {

                    logger.info(String.format("Getting phonenumber to update Gen Ledger Cummulative", phonenumber));
                    GenLedgAccount genLedResult;
                    Optional<GenLedgAccount> getGenLedgerTrans = _genLedgAccountRepo.findByOptPhoneNumberProdCode(rq.getPhoneNumberProductCode());
                    genLedResult = getGenLedgerTrans.get();

                    accountCreditCumPhnProCode = genLedResult.getAccountCreditCum().add(accountCredit);
                    accountDebitCumPhnProCode = genLedResult.getAccountDebitCum().add(accountDebit);
                    swChargesCumGelPhnProCode = genLedResult.getSwChargesCum().add(swChargesGel);
                    fMoneyChargesCumGelPhnProCode = genLedResult.getDemoPayChargesCum().add(fMoneyChargesGel);
                    balancePhnProCode = genLedResult.getBalance().add(accountCredit);
                    bookBalancePhnProCode = genLedResult.getBookBalance().subtract(rq.getTransAmount());
                    merchantBookedBalancePhnProCode = genLedResult.getMerchantBookedBalance().add(merchantBookedBalance);

                }

                if (_genLedgAccountRepo.existsByPhoneNumber(phonenumber)) {

                    logger.info(String.format("Getting phonenumber to update Gen Ledger Cummulative", phonenumber));
                    GenLedgAccount genLedResult;
                    Optional<GenLedgAccount> getGenLedgerTrans = _genLedgAccountRepo.findByPhoneNumber(phonenumber);
                    genLedResult = getGenLedgerTrans.get();

                    accountCreditCum = genLedResult.getAccountCreditCum().add(accountCredit);

                    accountDebitCum = genLedResult.getAccountDebitCum().add(accountDebit);
                    swChargesCumGel = genLedResult.getSwChargesCum().add(swChargesGel);
                    fMoneyChargesCumGel = genLedResult.getDemoPayChargesCum().add(fMoneyChargesGel);
                    balance = genLedResult.getBalance().add(accountCredit);

                    bookBalance = genLedResult.getBookBalance().subtract(rq.getTransAmount());
                    merchantBookedBalance = genLedResult.getMerchantBookedBalance().add(merchantBookedBalance);

                }

                if (_genLedgAccountRepo.existsByProductCode(rq.getProductCode())) {

                    logger.info(String.format("Getting productCode to update Gen Ledger Cummulative"));
                    GenLedgAccount genLedResult;
                    Optional<GenLedgAccount> getGenLedgerTrans = _genLedgAccountRepo.findByProductCode(rq.getProductCode());
                    genLedResult = getGenLedgerTrans.get();
                    productCodeFeeCum = genLedResult.getDemoPayChargesCum().add(fMoneyChargesGel);
                    countProductCodeTrans = genLedResult.getCountProductCodeTrans() + 1;

                }

                logger.info(String.format("Data exist in GenLedgAccount Table"));

                GenLedgAccount genLedgAccount = _genLedgAccountRepo.findTopByOrderByIdDesc();
                pl_cum_AccountCredit = genLedgAccount.getPl_cum_AccountCredit().add(accountCredit);
                pl_cum_AccountDebit = genLedgAccount.getPl_cum_AccountDebit().add(accountDebit);
                pl_cum_swChargesGel = genLedgAccount.getPl_cum_swCharges().add(swChargesGel);
                pl_cum_fMoneyChargesGel = genLedgAccount.getPl_cum_fMoneyCharges().add(fMoneyChargesGel);

                //save to gen ledger...
                logger.info(String.format("Saving to General Ledger Table"));
                GenLedgAccount genLedger = new GenLedgAccount(fMoneyTransId,
                        phonenumber, fundingType, accountCredit, balance, bookBalance, accountCreditCum,
                        pl_cum_AccountCredit, accountDebit, accountDebitCum, pl_cum_AccountDebit, swChargesGel, swChargesCumGel, pl_cum_swChargesGel,
                        fMoneyChargesGel, pl_cum_fMoneyChargesGel, fMoneyChargesCumGel, narration,
                        merchantBookedBalance,
                        rq.getProductCode(), rq.getProductName(), countProductCodeTrans,
                        productCodeFeeCum, rq.getPhoneNumberProductCode(), balancePhnProCode, accountCreditCumPhnProCode, accountDebitCumPhnProCode,
                        swChargesCumGelPhnProCode, fMoneyChargesCumGelPhnProCode,
                        bookBalancePhnProCode, merchantBookedBalancePhnProCode);
                _genLedgAccountRepo.save(genLedger);

                //save to WalletFundSucInfo for Indirect Funding
                WalletFundSucInfo wallSucessInfoResult = walletFundingSucessInfoRepo.findTopByOrderByIdDesc();
                p_amountPaidInCum = wallSucessInfoResult.getAmountPaidInCum().add(dataVerifyateGetAmount);
                p_swChargesCum = wallSucessInfoResult.getSwChargesCum().add(getVerifyChargeAmount);
                p_fMoneyChargesCum = wallSucessInfoResult.getDemoPayChargesCum().add(fMoneyCharges);
                p_amtCreToWalletCum = wallSucessInfoResult.getAmtCreToWalletCum().add(balForWallet);
                logger.info(String.format("Saving to WalletFundSucInfo Table"));

                WalletFundSucInfo wallSucessInfo = new WalletFundSucInfo(
                        dataVerifyateGetAmount, w_amountPaidInCum, p_amountPaidInCum,
                        getVerifyChargeAmount, w_swChargesCum, p_swChargesCum,
                        fMoneyCharges, w_fMoneyChargesCum, p_fMoneyChargesCum,
                        balForWallet, w_amtCreToWalletCum, p_amtCreToWalletCum,
                        phonenumber, fMoneyTransId,
                        rq.getSwRefrenceNumber(), "No Validation from Bank", rq.getSwRefrenceNumber(), rq.getSwRefrenceNumber()
                );
                walletFundingSucessInfoRepo.save(wallSucessInfo);
                //save to gen ledger cummulative...
                GenLedgAccountCum genLedgAccountCum;

                if (_genLedgAccountCumRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Saving to GenLedgAccountCum Table phoneNumber exists"));
                    Optional<GenLedgAccountCum> getRecordGenLedgAccountCumCum = _genLedgAccountCumRepo.findByPhoneNumber(phonenumber);
                    genLedgAccountCum = getRecordGenLedgAccountCumCum.get();
                    //roll back do not increase count

                    genLedgAccountCum.setCountSuccessTrans(genLedgAccountCum.getCountSuccessTrans());
                    genLedgAccountCum.setTotalAmountCredited(genLedgAccountCum.getTotalAmountCredited().add(accountCredit));
                    genLedgAccountCum.setTotalAmountDebited(genLedgAccountCum.getTotalAmountDebited().add(accountDebit));
                    genLedgAccountCum.setTotalPayCharge(genLedgAccountCum.getTotalPayCharge().add(fMoneyChargesGel));
                    genLedgAccountCum.setTotalSwCharges(genLedgAccountCum.getTotalSwCharges().add(swChargesGel));
                    genLedgAccountCum.setTotalBalance(genLedgAccountCum.getTotalBalance().add(accountCredit));
                    genLedgAccountCum.setTotalBookBalance(genLedgAccountCum.getTotalBookBalance().subtract(rq.getTransAmount()));
                    genLedgAccountCum.setTotalMerchantBookedBalance(genLedgAccountCum.getTotalMerchantBookedBalance());

                    _genLedgAccountCumRepo.save(genLedgAccountCum);

                }
                if (_genLedgAccountCumRepo.existsByPhnProductCode(rq.getPhoneNumber() + rq.getProductCode())) {
                    logger.info(String.format("Saving to GenLedgAccountCum Table phone-productcode exists"));

                    Optional<GenLedgAccountCum> getRecordGenLedgAccountCumCum = _genLedgAccountCumRepo.findByPhnProductCode(phnProdCode);
                    genLedgAccountCum = getRecordGenLedgAccountCumCum.get();
                    //roll back do not increase count

                    genLedgAccountCum.setCountSuccTransPhnProCode(genLedgAccountCum.getCountSuccTransPhnProCode());
                    genLedgAccountCum.setTotalAmtCreditedPhnProCode(genLedgAccountCum.getTotalAmtCreditedPhnProCode().add(accountCredit));
                    genLedgAccountCum.setTotalAmtDebitedPhnProCode(genLedgAccountCum.getTotalAmtDebitedPhnProCode().add(accountDebit));
                    genLedgAccountCum.setTotalPayChargePhnProCode(genLedgAccountCum.getTotalPayChargePhnProCode().add(fMoneyChargesGel));
                    genLedgAccountCum.setTotalSwChargesPhnProCode(genLedgAccountCum.getTotalSwChargesPhnProCode().add(swChargesGel));
                    genLedgAccountCum.setTotalBalancePhnProCode(genLedgAccountCum.getTotalBalancePhnProCode().add(accountCredit));
                    genLedgAccountCum.setTotalBookBalancePhnProCode(genLedgAccountCum.getTotalBookBalancePhnProCode().subtract(rq.getTransAmount()));
                    genLedgAccountCum.setTotalMerBookedBalPhnProCode(genLedgAccountCum.getTotalMerBookedBalPhnProCode());

                    _genLedgAccountCumRepo.save(genLedgAccountCum);

                } else {

                    logger.info(String.format("Saving to genLedgAccountCum Table phoneNumber first entry"));
                    GenLedgAccountCum genLedgAcct = new GenLedgAccountCum(
                            phonenumber, phonenumber, 1, accountCredit, accountCredit, BigDecimal.ZERO, accountDebit,
                            swChargesGel, fMoneyChargesGel, BigDecimal.ZERO, phnProdCode, 1, accountCredit, accountCredit, BigDecimal.ZERO, accountDebit,
                            swChargesGel, fMoneyChargesGel, BigDecimal.ZERO
                    );
                    _genLedgAccountCumRepo.save(genLedgAcct);
                }

                WalletFundingInfoCum walletFundingInfoCum;

                if (walletFundingInfoCumRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Saving to WalletFundingInfoCum Table phoneNumber exists"));
                    Optional<WalletFundingInfoCum> getRecordWalletFundingInfoCumRe = walletFundingInfoCumRepo.findByPhoneNumber(phonenumber);

                    walletFundingInfoCum = getRecordWalletFundingInfoCumRe.get();
                    //roll back do not increase count

                    walletFundingInfoCum.setCountSuccessTrans(walletFundingInfoCum.getCountSuccessTrans());
                    walletFundingInfoCum.setTotalAmountPaidIn(walletFundingInfoCum.getTotalAmountPaidIn().add(dataVerifyateGetAmount));
                    walletFundingInfoCum.setTotalAmtCreToWallet(walletFundingInfoCum.getTotalAmtCreToWallet().add(balForWallet));
                    walletFundingInfoCum.setTotalFMoneyChrge(walletFundingInfoCum.getTotalFMoneyChrge().add(fMoneyCharges));
                    walletFundingInfoCum.setTotalSwCharges(walletFundingInfoCum.getTotalSwCharges().add(getVerifyChargeAmount));
                    walletFundingInfoCumRepo.save(walletFundingInfoCum);
                    //send SMS to subscriber
                    // responseModel.setDescription("Request initiated successfully");
                    responseModel.setStatusCode(200);
                } else {
                    logger.info(String.format("Saving to WalletFundingInfoCum Table phoneNumber first entry"));
                    WalletFundingInfoCum wallFundingCum = new WalletFundingInfoCum(
                            phonenumber, phonenumber, 1,
                            dataVerifyateGetAmount, getVerifyChargeAmount, fMoneyCharges,
                            balForWallet
                    );
                    walletFundingInfoCumRepo.save(wallFundingCum);
                    //send SMS to subscriber

                    responseModel.setStatusCode(200);
                }

            } else {

                logger.info(String.format("Data does not exist in WalletFundSucInfo and GenLedgAccount Table"));

                //save to gen ledger...
                logger.info(String.format("Saving to General-Ledger Table"));
                GenLedgAccount genLedger = new GenLedgAccount(fMoneyTransId, phonenumber, "In-Direct Wallet Funding", accountCredit,
                        accountCredit, BigDecimal.ZERO, accountCredit,
                        accountCredit, accountDebit, accountDebit, accountDebit, swChargesGel, swChargesGel, swChargesGel,
                        fMoneyChargesGel, fMoneyChargesGel, fMoneyChargesGel, narration, BigDecimal.ZERO,
                        rq.getProductCode(), rq.getProductName(), 1,
                        fMoneyChargesGel, rq.getPhoneNumberProductCode(), balancePhnProCode, accountCreditCumPhnProCode, accountDebitCumPhnProCode,
                        swChargesCumGelPhnProCode, fMoneyChargesCumGelPhnProCode, bookBalancePhnProCode, merchantBookedBalancePhnProCode);
                _genLedgAccountRepo.save(genLedger);

                logger.info(String.format("Saving to WalletFundSucInfo Table"));
                WalletFundSucInfo wallSucessInfo = new WalletFundSucInfo(
                        dataVerifyateGetAmount, dataVerifyateGetAmount, dataVerifyateGetAmount,
                        getVerifyChargeAmount, getVerifyChargeAmount, getVerifyChargeAmount,
                        fMoneyCharges, fMoneyCharges, fMoneyCharges,
                        balForWallet, balForWallet, balForWallet,
                        phonenumber, fMoneyTransId,
                        rq.getSwRefrenceNumber(), "No Validation from Bank", rq.getSwRefrenceNumber(), rq.getSwRefrenceNumber()
                );
                walletFundingSucessInfoRepo.save(wallSucessInfo);

                WalletFundingInfoCum walletFundingInfoCum;

                if (walletFundingInfoCumRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Saving to WalletFundingInfoCum Table phoneNumber exists"));
                    Optional<WalletFundingInfoCum> getRecordWalletFundingInfoCumRe = walletFundingInfoCumRepo.findByPhoneNumber(phonenumber);

                    walletFundingInfoCum = getRecordWalletFundingInfoCumRe.get();
                    //roll back do not increase count

                    walletFundingInfoCum.setCountSuccessTrans(walletFundingInfoCum.getCountSuccessTrans());
                    walletFundingInfoCum.setTotalAmountPaidIn(walletFundingInfoCum.getTotalAmountPaidIn().add(dataVerifyateGetAmount));
                    walletFundingInfoCum.setTotalAmtCreToWallet(walletFundingInfoCum.getTotalAmtCreToWallet().add(balForWallet));
                    walletFundingInfoCum.setTotalFMoneyChrge(walletFundingInfoCum.getTotalFMoneyChrge().add(fMoneyCharges));
                    walletFundingInfoCum.setTotalSwCharges(walletFundingInfoCum.getTotalSwCharges().add(getVerifyChargeAmount));
                    walletFundingInfoCumRepo.save(walletFundingInfoCum);
                    //send SMS to subscriber
                    responseModel.setStatusCode(200);
                } else {
                    logger.info(String.format("Saving to WalletFundingInfoCum Table phoneNumber first entry"));
                    WalletFundingInfoCum wallFundingCum = new WalletFundingInfoCum(
                            phonenumber, phonenumber, 1,
                            dataVerifyateGetAmount, getVerifyChargeAmount, fMoneyCharges,
                            balForWallet
                    );
                    walletFundingInfoCumRepo.save(wallFundingCum);
                    //send SMS to subscriber
                    responseModel.setStatusCode(200);
                }
                GenLedgAccountCum genLedgAccountCum;

                if (_genLedgAccountCumRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Saving to GenLedgAccountCum Table phoneNumber exists"));
                    Optional<GenLedgAccountCum> getRecordGenLedgAccountCumCum = _genLedgAccountCumRepo.findByPhoneNumber(phonenumber);
                    genLedgAccountCum = getRecordGenLedgAccountCumCum.get();
                    //roll back do not increase count

                    genLedgAccountCum.setCountSuccessTrans(genLedgAccountCum.getCountSuccessTrans());
                    genLedgAccountCum.setTotalAmountCredited(genLedgAccountCum.getTotalAmountCredited().add(accountCredit));
                    genLedgAccountCum.setTotalAmountDebited(genLedgAccountCum.getTotalAmountDebited().add(accountDebit));
                    genLedgAccountCum.setTotalPayCharge(genLedgAccountCum.getTotalPayCharge().add(fMoneyChargesGel));
                    genLedgAccountCum.setTotalSwCharges(genLedgAccountCum.getTotalSwCharges().add(swChargesGel));
                    genLedgAccountCum.setTotalBalance(genLedgAccountCum.getTotalBalance().add(accountCredit));
                    genLedgAccountCum.setTotalBookBalance(genLedgAccountCum.getTotalBookBalance().subtract(rq.getTransAmount()));
                    genLedgAccountCum.setTotalMerchantBookedBalance(genLedgAccountCum.getTotalMerchantBookedBalance());

                    _genLedgAccountCumRepo.save(genLedgAccountCum);

                }

                if (_genLedgAccountCumRepo.existsByPhnProductCode(rq.getPhoneNumber() + rq.getProductCode())) {
                    logger.info(String.format("Saving to GenLedgAccountCum Table phone-productcode exists"));

                    Optional<GenLedgAccountCum> getRecordGenLedgAccountCumCum = _genLedgAccountCumRepo.findByPhnProductCode(phnProdCode);
                    genLedgAccountCum = getRecordGenLedgAccountCumCum.get();
                    //roll back do not increase count

                    genLedgAccountCum.setCountSuccTransPhnProCode(genLedgAccountCum.getCountSuccTransPhnProCode());
                    genLedgAccountCum.setTotalAmtCreditedPhnProCode(genLedgAccountCum.getTotalAmtCreditedPhnProCode().add(accountCredit));
                    genLedgAccountCum.setTotalAmtDebitedPhnProCode(genLedgAccountCum.getTotalAmtDebitedPhnProCode().add(accountDebit));
                    genLedgAccountCum.setTotalPayChargePhnProCode(genLedgAccountCum.getTotalPayChargePhnProCode().add(fMoneyChargesGel));
                    genLedgAccountCum.setTotalSwChargesPhnProCode(genLedgAccountCum.getTotalSwChargesPhnProCode().add(swChargesGel));
                    genLedgAccountCum.setTotalBalancePhnProCode(genLedgAccountCum.getTotalBalancePhnProCode().add(accountCredit));
                    genLedgAccountCum.setTotalBookBalancePhnProCode(genLedgAccountCum.getTotalBookBalancePhnProCode().subtract(rq.getTransAmount()));
                    genLedgAccountCum.setTotalMerBookedBalPhnProCode(genLedgAccountCum.getTotalMerBookedBalPhnProCode());

                    _genLedgAccountCumRepo.save(genLedgAccountCum);

                } else {

                    logger.info(String.format("Saving to genLedgAccountCum Table phoneNumber first entry"));
                    GenLedgAccountCum genLedgAcct = new GenLedgAccountCum(
                            phonenumber, phonenumber, 1, accountCredit, accountCredit, BigDecimal.ZERO, accountDebit,
                            swChargesGel, fMoneyChargesGel, BigDecimal.ZERO, phnProdCode, 1, accountCredit, accountCredit, BigDecimal.ZERO, accountDebit,
                            swChargesGel, fMoneyChargesGel, BigDecimal.ZERO
                    );
                    _genLedgAccountCumRepo.save(genLedgAcct);
                }
                responseModel.setStatusCode(200);
            }
        } catch (Exception ex) {
            responseModel.setDescription(statusMessage);
            responseModel.setStatusCode(statusCode);
            ex.printStackTrace();
        }

        return responseModel;

    }

    // escrowCreditLedgerOneTimeRollBackPayer
    public BaseResponse escrowCreditLedgerOneTimeRollBackPayer(ProcLedgerRequestCreditOneTime rq) {

        BaseResponse responseModel = new BaseResponse();
        int statusCode = 500;
        String statusMessage = "An error occured,please try again";
        try {
            statusCode = 400;

            BigDecimal fMoneyCharges = BigDecimal.ZERO;
            String phonenumber = rq.getPhoneNumber();
            String fundingType = rq.getFundingType();
            String narration = rq.getNarration();
            String fMoneyTransId = rq.getKulTransactionId();
            BigDecimal dataVerifyateGetAmount = BigDecimal.ZERO; //add rq.getTransAmount() here 
            BigDecimal getVerifyChargeAmount = rq.getSwFees();
            BigDecimal amountLeftBeforeFMoneyCharges = BigDecimal.ZERO;
            BigDecimal w_amountPaidInCum = BigDecimal.ZERO;
            BigDecimal w_swChargesCum = BigDecimal.ZERO;
            BigDecimal w_fMoneyChargesCum = BigDecimal.ZERO;
            BigDecimal w_amtCreToWalletCum = BigDecimal.ZERO;

            BigDecimal p_swChargesCum = BigDecimal.ZERO;
            BigDecimal p_fMoneyChargesCum = BigDecimal.ZERO;
            BigDecimal p_amtCreToWalletCum = BigDecimal.ZERO;
            BigDecimal p_amountPaidInCum = BigDecimal.ZERO;
            BigDecimal balForWallet = BigDecimal.ZERO;

            BigDecimal accountCredit = BigDecimal.ZERO;;
            BigDecimal accountCreditCum = BigDecimal.ZERO;
            BigDecimal pl_cum_AccountCredit = BigDecimal.ZERO;
            BigDecimal accountDebit = BigDecimal.ZERO;
            BigDecimal accountDebitCum = BigDecimal.ZERO;
            BigDecimal pl_cum_AccountDebit = BigDecimal.ZERO;
            BigDecimal swChargesGel = rq.getSwFees();
            BigDecimal swChargesCumGel = BigDecimal.ZERO;
            BigDecimal pl_cum_swChargesGel = BigDecimal.ZERO;
            BigDecimal fMoneyChargesGel = fMoneyCharges;
            BigDecimal pl_cum_fMoneyChargesGel = BigDecimal.ZERO;
            BigDecimal fMoneyChargesCumGel = BigDecimal.ZERO;
            BigDecimal productCodeFeeCum = BigDecimal.ZERO;
            int countProductCodeTrans = 0;

            BigDecimal balancePhnProCode = BigDecimal.ZERO;
            BigDecimal accountCreditCumPhnProCode = BigDecimal.ZERO;
            BigDecimal accountDebitCumPhnProCode = BigDecimal.ZERO;
            BigDecimal swChargesCumGelPhnProCode = BigDecimal.ZERO;
            BigDecimal fMoneyChargesCumGelPhnProCode = BigDecimal.ZERO;
            BigDecimal bookBalancePhnProCode = BigDecimal.ZERO;
            BigDecimal merchantBookedBalancePhnProCode = BigDecimal.ZERO;
            String phnProdCode = rq.getPhoneNumber() + rq.getProductCode();

            if (walletFundingSucessInfoRepo.findTopByOrderByIdDesc() != null) {
                BigDecimal balance = BigDecimal.ZERO;
                BigDecimal bookBalance = BigDecimal.ZERO;
                BigDecimal merchantBookedBalance = BigDecimal.ZERO;

                if (walletFundingSucessInfoRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Getting phonenumber to update wallet's cummulatives", phonenumber));
                    WalletFundSucInfo result;

                    Optional<WalletFundSucInfo> getTrans = walletFundingSucessInfoRepo.findByPhoneNumber(phonenumber);
                    result = getTrans.get();
                    w_amountPaidInCum = result.getAmountPaidInCum().add(dataVerifyateGetAmount);
                    w_swChargesCum = result.getSwChargesCum().add(getVerifyChargeAmount);
                    w_fMoneyChargesCum = result.getDemoPayChargesCum().add(fMoneyCharges);
                    w_amtCreToWalletCum = result.getAmtCreToWalletCum().add(balForWallet);

                }
                logger.info(String.format("Data exist in WalletFundSucInfo Table"));

                List<GenLedgAccount> getDeee = _genLedgAccountRepo.findByPhoneNumberProdCode(rq.getPhoneNumber(), rq.getProductCode());

                if (getDeee.size() > 0) {

                    logger.info(String.format("Getting phonenumber to update Gen Ledger Cummulative", phonenumber));
                    GenLedgAccount genLedResult;
                    Optional<GenLedgAccount> getGenLedgerTrans = _genLedgAccountRepo.findByOptPhoneNumberProdCode(rq.getPhoneNumberProductCode());
                    genLedResult = getGenLedgerTrans.get();

                    accountCreditCumPhnProCode = genLedResult.getAccountCreditCum().add(accountCredit);
                    accountDebitCumPhnProCode = genLedResult.getAccountDebitCum().add(accountDebit);
                    swChargesCumGelPhnProCode = genLedResult.getSwChargesCum().add(swChargesGel);
                    fMoneyChargesCumGelPhnProCode = genLedResult.getDemoPayChargesCum().add(fMoneyChargesGel);
                    balancePhnProCode = genLedResult.getBalance().add(accountCredit);
                    bookBalancePhnProCode = genLedResult.getBookBalance().subtract(rq.getTransAmount());
                    merchantBookedBalancePhnProCode = genLedResult.getMerchantBookedBalance().add(merchantBookedBalance);

                }

                if (_genLedgAccountRepo.existsByPhoneNumber(phonenumber)) {

                    logger.info(String.format("Getting phonenumber to update Gen Ledger Cummulative", phonenumber));
                    GenLedgAccount genLedResult;
                    Optional<GenLedgAccount> getGenLedgerTrans = _genLedgAccountRepo.findByPhoneNumber(phonenumber);
                    genLedResult = getGenLedgerTrans.get();

                    accountCreditCum = genLedResult.getAccountCreditCum().add(accountCredit);

                    accountDebitCum = genLedResult.getAccountDebitCum().add(accountDebit);
                    swChargesCumGel = genLedResult.getSwChargesCum().add(swChargesGel);
                    fMoneyChargesCumGel = genLedResult.getDemoPayChargesCum().add(fMoneyChargesGel);
                    balance = genLedResult.getBalance().add(rq.getTransAmount());

                    bookBalance = genLedResult.getBookBalance().subtract(rq.getTransAmount());
                    merchantBookedBalance = genLedResult.getMerchantBookedBalance().add(merchantBookedBalance);

                }

                if (_genLedgAccountRepo.existsByProductCode(rq.getProductCode())) {

                    logger.info(String.format("Getting productCode to update Gen Ledger Cummulative"));
                    GenLedgAccount genLedResult;
                    Optional<GenLedgAccount> getGenLedgerTrans = _genLedgAccountRepo.findByProductCode(rq.getProductCode());
                    genLedResult = getGenLedgerTrans.get();
                    productCodeFeeCum = genLedResult.getDemoPayChargesCum().add(fMoneyChargesGel);
                    countProductCodeTrans = genLedResult.getCountProductCodeTrans() + 1;

                }

                logger.info(String.format("Data exist in GenLedgAccount Table"));

                GenLedgAccount genLedgAccount = _genLedgAccountRepo.findTopByOrderByIdDesc();
                pl_cum_AccountCredit = genLedgAccount.getPl_cum_AccountCredit().add(accountCredit);
                pl_cum_AccountDebit = genLedgAccount.getPl_cum_AccountDebit().add(accountDebit);
                pl_cum_swChargesGel = genLedgAccount.getPl_cum_swCharges().add(swChargesGel);
                pl_cum_fMoneyChargesGel = genLedgAccount.getPl_cum_fMoneyCharges().add(fMoneyChargesGel);

                //save to gen ledger...
                logger.info(String.format("Saving to General Ledger Table"));
                GenLedgAccount genLedger = new GenLedgAccount(fMoneyTransId, phonenumber,
                        fundingType, accountCredit, balance, bookBalance, accountCreditCum,
                        pl_cum_AccountCredit, accountDebit, accountDebitCum, pl_cum_AccountDebit, swChargesGel, swChargesCumGel, pl_cum_swChargesGel,
                        fMoneyChargesGel, pl_cum_fMoneyChargesGel, fMoneyChargesCumGel, narration, merchantBookedBalance,
                        rq.getProductCode(), rq.getProductName(), countProductCodeTrans,
                        productCodeFeeCum, rq.getPhoneNumberProductCode(), balancePhnProCode, accountCreditCumPhnProCode, accountDebitCumPhnProCode,
                        swChargesCumGelPhnProCode, fMoneyChargesCumGelPhnProCode,
                        bookBalancePhnProCode, merchantBookedBalancePhnProCode);
                _genLedgAccountRepo.save(genLedger);

                //save to WalletFundSucInfo for Indirect Funding
                WalletFundSucInfo wallSucessInfoResult = walletFundingSucessInfoRepo.findTopByOrderByIdDesc();
                p_amountPaidInCum = wallSucessInfoResult.getAmountPaidInCum().add(dataVerifyateGetAmount);
                p_swChargesCum = wallSucessInfoResult.getSwChargesCum().add(getVerifyChargeAmount);
                p_fMoneyChargesCum = wallSucessInfoResult.getDemoPayChargesCum().add(fMoneyCharges);
                p_amtCreToWalletCum = wallSucessInfoResult.getAmtCreToWalletCum().add(balForWallet);
                logger.info(String.format("Saving to WalletFundSucInfo Table"));

                WalletFundSucInfo wallSucessInfo = new WalletFundSucInfo(
                        dataVerifyateGetAmount, w_amountPaidInCum, p_amountPaidInCum,
                        getVerifyChargeAmount, w_swChargesCum, p_swChargesCum,
                        fMoneyCharges, w_fMoneyChargesCum, p_fMoneyChargesCum,
                        balForWallet, w_amtCreToWalletCum, p_amtCreToWalletCum,
                        phonenumber, fMoneyTransId,
                        rq.getSwRefrenceNumber(), "No Validation from Bank", rq.getSwRefrenceNumber(), rq.getSwRefrenceNumber()
                );
                walletFundingSucessInfoRepo.save(wallSucessInfo);
                //save to gen ledger cummulative...
                GenLedgAccountCum genLedgAccountCum;

                if (_genLedgAccountCumRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Saving to GenLedgAccountCum Table phoneNumber exists"));
                    Optional<GenLedgAccountCum> getRecordGenLedgAccountCumCum = _genLedgAccountCumRepo.findByPhoneNumber(phonenumber);
                    genLedgAccountCum = getRecordGenLedgAccountCumCum.get();
                    //roll back do not decrease count

                    genLedgAccountCum.setCountSuccessTrans(genLedgAccountCum.getCountSuccessTrans() - 1);
                    genLedgAccountCum.setTotalAmountCredited(genLedgAccountCum.getTotalAmountCredited().add(accountCredit));
                    genLedgAccountCum.setTotalAmountDebited(genLedgAccountCum.getTotalAmountDebited().add(accountDebit));
                    genLedgAccountCum.setTotalPayCharge(genLedgAccountCum.getTotalPayCharge().add(fMoneyChargesGel));
                    genLedgAccountCum.setTotalSwCharges(genLedgAccountCum.getTotalSwCharges().add(swChargesGel));
                    genLedgAccountCum.setTotalBalance(genLedgAccountCum.getTotalBalance().add(rq.getTransAmount()));
                    genLedgAccountCum.setTotalBookBalance(genLedgAccountCum.getTotalBookBalance().subtract(rq.getTransAmount()));
                    genLedgAccountCum.setTotalMerchantBookedBalance(genLedgAccountCum.getTotalMerchantBookedBalance());

                    _genLedgAccountCumRepo.save(genLedgAccountCum);

                }

                if (_genLedgAccountCumRepo.existsByPhnProductCode(rq.getPhoneNumber() + rq.getProductCode())) {
                    logger.info(String.format("Saving to GenLedgAccountCum Table phone-productcode exists"));

                    Optional<GenLedgAccountCum> getRecordGenLedgAccountCumCum = _genLedgAccountCumRepo.findByPhnProductCode(rq.getPhoneNumber() + rq.getProductCode());
                    genLedgAccountCum = getRecordGenLedgAccountCumCum.get();

                    //roll back do not decrease count
                    genLedgAccountCum.setCountSuccTransPhnProCode(genLedgAccountCum.getCountSuccTransPhnProCode() - 1);
                    genLedgAccountCum.setTotalAmtCreditedPhnProCode(genLedgAccountCum.getTotalAmtCreditedPhnProCode().add(accountCredit));
                    genLedgAccountCum.setTotalAmtDebitedPhnProCode(genLedgAccountCum.getTotalAmtDebitedPhnProCode().add(accountDebit));
                    genLedgAccountCum.setTotalPayChargePhnProCode(genLedgAccountCum.getTotalPayChargePhnProCode().add(fMoneyChargesGel));
                    genLedgAccountCum.setTotalSwChargesPhnProCode(genLedgAccountCum.getTotalSwChargesPhnProCode().add(swChargesGel));
                    genLedgAccountCum.setTotalBalancePhnProCode(genLedgAccountCum.getTotalBalancePhnProCode().add(rq.getTransAmount()));
                    genLedgAccountCum.setTotalBookBalancePhnProCode(genLedgAccountCum.getTotalBookBalancePhnProCode().subtract(rq.getTransAmount()));
                    genLedgAccountCum.setTotalMerBookedBalPhnProCode(genLedgAccountCum.getTotalMerBookedBalPhnProCode());

                    _genLedgAccountCumRepo.save(genLedgAccountCum);

                } else {

                    logger.info(String.format("Saving to genLedgAccountCum Table phoneNumber first entry"));
                    GenLedgAccountCum genLedgAcct = new GenLedgAccountCum(
                            phonenumber, phonenumber, 1, accountCredit, accountCredit, BigDecimal.ZERO, accountDebit,
                            swChargesGel, fMoneyChargesGel, BigDecimal.ZERO, phnProdCode, 1, accountCredit, accountCredit, BigDecimal.ZERO, accountDebit,
                            swChargesGel, fMoneyChargesGel, BigDecimal.ZERO
                    );
                    _genLedgAccountCumRepo.save(genLedgAcct);
                }

                WalletFundingInfoCum walletFundingInfoCum;

                if (walletFundingInfoCumRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Saving to WalletFundingInfoCum Table phoneNumber exists"));
                    Optional<WalletFundingInfoCum> getRecordWalletFundingInfoCumRe = walletFundingInfoCumRepo.findByPhoneNumber(phonenumber);

                    walletFundingInfoCum = getRecordWalletFundingInfoCumRe.get();
                    //roll back do not decrease count

                    walletFundingInfoCum.setCountSuccessTrans(walletFundingInfoCum.getCountSuccessTrans() - 1);
                    walletFundingInfoCum.setTotalAmountPaidIn(walletFundingInfoCum.getTotalAmountPaidIn().add(dataVerifyateGetAmount));
                    walletFundingInfoCum.setTotalAmtCreToWallet(walletFundingInfoCum.getTotalAmtCreToWallet().add(balForWallet));
                    walletFundingInfoCum.setTotalFMoneyChrge(walletFundingInfoCum.getTotalFMoneyChrge().add(fMoneyCharges));
                    walletFundingInfoCum.setTotalSwCharges(walletFundingInfoCum.getTotalSwCharges().add(getVerifyChargeAmount));
                    walletFundingInfoCumRepo.save(walletFundingInfoCum);
                    //send SMS to subscriber
                    // responseModel.setDescription("Request initiated successfully");
                    responseModel.setStatusCode(200);
                } else {
                    logger.info(String.format("Saving to WalletFundingInfoCum Table phoneNumber first entry"));
                    WalletFundingInfoCum wallFundingCum = new WalletFundingInfoCum(
                            phonenumber, phonenumber, 1,
                            dataVerifyateGetAmount, getVerifyChargeAmount, fMoneyCharges,
                            balForWallet
                    );
                    walletFundingInfoCumRepo.save(wallFundingCum);
                    //send SMS to subscriber

                    responseModel.setStatusCode(200);
                }

            } else {

                logger.info(String.format("Data does not exist in WalletFundSucInfo and GenLedgAccount Table"));

                //save to gen ledger...
                logger.info(String.format("Saving to General-Ledger Table"));
                GenLedgAccount genLedger = new GenLedgAccount(fMoneyTransId, phonenumber, "In-Direct Wallet Funding", accountCredit,
                        accountCredit, BigDecimal.ZERO, accountCredit,
                        accountCredit, accountDebit, accountDebit, accountDebit, swChargesGel, swChargesGel, swChargesGel,
                        fMoneyChargesGel, fMoneyChargesGel, fMoneyChargesGel, narration, BigDecimal.ZERO,
                        rq.getProductCode(), rq.getProductName(), 1,
                        fMoneyChargesGel, rq.getPhoneNumberProductCode(), balancePhnProCode, accountCreditCumPhnProCode, accountDebitCumPhnProCode,
                        swChargesCumGelPhnProCode, fMoneyChargesCumGelPhnProCode, bookBalancePhnProCode, merchantBookedBalancePhnProCode);
                _genLedgAccountRepo.save(genLedger);

                logger.info(String.format("Saving to WalletFundSucInfo Table"));
                WalletFundSucInfo wallSucessInfo = new WalletFundSucInfo(
                        dataVerifyateGetAmount, dataVerifyateGetAmount, dataVerifyateGetAmount,
                        getVerifyChargeAmount, getVerifyChargeAmount, getVerifyChargeAmount,
                        fMoneyCharges, fMoneyCharges, fMoneyCharges,
                        balForWallet, balForWallet, balForWallet,
                        phonenumber, fMoneyTransId,
                        rq.getSwRefrenceNumber(), "No Validation from Bank", rq.getSwRefrenceNumber(), rq.getSwRefrenceNumber()
                );
                walletFundingSucessInfoRepo.save(wallSucessInfo);

                WalletFundingInfoCum walletFundingInfoCum;

                if (walletFundingInfoCumRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Saving to WalletFundingInfoCum Table phoneNumber exists"));
                    Optional<WalletFundingInfoCum> getRecordWalletFundingInfoCumRe = walletFundingInfoCumRepo.findByPhoneNumber(phonenumber);

                    walletFundingInfoCum = getRecordWalletFundingInfoCumRe.get();
                    //roll back do not decrease count

                    walletFundingInfoCum.setCountSuccessTrans(walletFundingInfoCum.getCountSuccessTrans() - 1);
                    walletFundingInfoCum.setTotalAmountPaidIn(walletFundingInfoCum.getTotalAmountPaidIn().add(dataVerifyateGetAmount));
                    walletFundingInfoCum.setTotalAmtCreToWallet(walletFundingInfoCum.getTotalAmtCreToWallet().add(balForWallet));
                    walletFundingInfoCum.setTotalFMoneyChrge(walletFundingInfoCum.getTotalFMoneyChrge().add(fMoneyCharges));
                    walletFundingInfoCum.setTotalSwCharges(walletFundingInfoCum.getTotalSwCharges().add(getVerifyChargeAmount));
                    walletFundingInfoCumRepo.save(walletFundingInfoCum);
                    //send SMS to subscriber
                    responseModel.setStatusCode(200);
                } else {
                    logger.info(String.format("Saving to WalletFundingInfoCum Table phoneNumber first entry"));
                    WalletFundingInfoCum wallFundingCum = new WalletFundingInfoCum(
                            phonenumber, phonenumber, 1,
                            dataVerifyateGetAmount, getVerifyChargeAmount, fMoneyCharges,
                            balForWallet
                    );
                    walletFundingInfoCumRepo.save(wallFundingCum);
                    //send SMS to subscriber
                    responseModel.setStatusCode(200);
                }
                GenLedgAccountCum genLedgAccountCum;

                if (_genLedgAccountCumRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Saving to GenLedgAccountCum Table phoneNumber exists"));
                    Optional<GenLedgAccountCum> getRecordGenLedgAccountCumCum = _genLedgAccountCumRepo.findByPhoneNumber(phonenumber);
                    genLedgAccountCum = getRecordGenLedgAccountCumCum.get();
                    //roll back do not decrease count

                    genLedgAccountCum.setCountSuccessTrans(genLedgAccountCum.getCountSuccessTrans() - 1);
                    genLedgAccountCum.setTotalAmountCredited(genLedgAccountCum.getTotalAmountCredited().add(accountCredit));
                    genLedgAccountCum.setTotalAmountDebited(genLedgAccountCum.getTotalAmountDebited().add(accountDebit));
                    genLedgAccountCum.setTotalPayCharge(genLedgAccountCum.getTotalPayCharge().add(fMoneyChargesGel));
                    genLedgAccountCum.setTotalSwCharges(genLedgAccountCum.getTotalSwCharges().add(swChargesGel));
                    genLedgAccountCum.setTotalBalance(genLedgAccountCum.getTotalBalance().add(rq.getTransAmount()));
                    genLedgAccountCum.setTotalBookBalance(genLedgAccountCum.getTotalBookBalance().subtract(rq.getTransAmount()));
                    genLedgAccountCum.setTotalMerchantBookedBalance(genLedgAccountCum.getTotalMerchantBookedBalance());

                    _genLedgAccountCumRepo.save(genLedgAccountCum);

                }

                if (_genLedgAccountCumRepo.existsByPhnProductCode(rq.getPhoneNumber() + rq.getProductCode())) {
                    logger.info(String.format("Saving to GenLedgAccountCum Table phone-productcode exists"));

                    Optional<GenLedgAccountCum> getRecordGenLedgAccountCumCum = _genLedgAccountCumRepo.findByPhnProductCode(rq.getPhoneNumber() + rq.getProductCode());
                    genLedgAccountCum = getRecordGenLedgAccountCumCum.get();
                    genLedgAccountCum.setCountSuccTransPhnProCode(genLedgAccountCum.getCountSuccTransPhnProCode() - 1);
                    genLedgAccountCum.setTotalAmtCreditedPhnProCode(genLedgAccountCum.getTotalAmtCreditedPhnProCode().add(accountCredit));
                    genLedgAccountCum.setTotalAmtDebitedPhnProCode(genLedgAccountCum.getTotalAmtDebitedPhnProCode().add(accountDebit));
                    genLedgAccountCum.setTotalPayChargePhnProCode(genLedgAccountCum.getTotalPayChargePhnProCode().add(fMoneyChargesGel));
                    genLedgAccountCum.setTotalSwChargesPhnProCode(genLedgAccountCum.getTotalSwChargesPhnProCode().add(swChargesGel));
                    genLedgAccountCum.setTotalBalancePhnProCode(genLedgAccountCum.getTotalBalancePhnProCode().add(rq.getTransAmount()));
                    genLedgAccountCum.setTotalBookBalancePhnProCode(genLedgAccountCum.getTotalBookBalancePhnProCode().subtract(rq.getTransAmount()));
                    genLedgAccountCum.setTotalMerBookedBalPhnProCode(genLedgAccountCum.getTotalMerBookedBalPhnProCode());

                    _genLedgAccountCumRepo.save(genLedgAccountCum);

                } else {

                    logger.info(String.format("Saving to genLedgAccountCum Table phoneNumber first entry"));
                    GenLedgAccountCum genLedgAcct = new GenLedgAccountCum(
                            phonenumber, phonenumber, 1, accountCredit, accountCredit, BigDecimal.ZERO, accountDebit,
                            swChargesGel, fMoneyChargesGel, BigDecimal.ZERO, phnProdCode, 1, accountCredit, accountCredit, BigDecimal.ZERO, accountDebit,
                            swChargesGel, fMoneyChargesGel, BigDecimal.ZERO
                    );
                    _genLedgAccountCumRepo.save(genLedgAcct);
                }
                responseModel.setStatusCode(200);
            }
        } catch (Exception ex) {
            responseModel.setDescription(statusMessage);
            responseModel.setStatusCode(statusCode);
            ex.printStackTrace();
        }

        return responseModel;

    }

    //escrowCreditLedgerMerchantBookedBalance
    public BaseResponse escrowCreditLedgerMerchantBookedBalance(ProcLedgerRequestCreditOneTime rq) {

        BaseResponse responseModel = new BaseResponse();
        int statusCode = 500;
        String statusMessage = "An error occured,please try again";
        try {
            statusCode = 400;

            BigDecimal fMoneyCharges = BigDecimal.ZERO;
            String phonenumber = rq.getPhoneNumber();
            String fundingType = rq.getFundingType();
            String narration = rq.getNarration();
            String fMoneyTransId = rq.getKulTransactionId();
            BigDecimal dataVerifyateGetAmount = BigDecimal.ZERO; //add rq.getTransAmount() here 
            BigDecimal getVerifyChargeAmount = rq.getSwFees();
            BigDecimal amountLeftBeforeFMoneyCharges = BigDecimal.ZERO;
            BigDecimal w_amountPaidInCum = BigDecimal.ZERO;
            BigDecimal w_swChargesCum = BigDecimal.ZERO;
            BigDecimal w_fMoneyChargesCum = BigDecimal.ZERO;
            BigDecimal w_amtCreToWalletCum = BigDecimal.ZERO;

            BigDecimal p_swChargesCum = BigDecimal.ZERO;
            BigDecimal p_fMoneyChargesCum = BigDecimal.ZERO;
            BigDecimal p_amtCreToWalletCum = BigDecimal.ZERO;
            BigDecimal p_amountPaidInCum = BigDecimal.ZERO;
            BigDecimal balForWallet = BigDecimal.ZERO;

            BigDecimal accountCredit = BigDecimal.ZERO;;
            BigDecimal accountCreditCum = BigDecimal.ZERO;
            BigDecimal pl_cum_AccountCredit = BigDecimal.ZERO;
            BigDecimal accountDebit = BigDecimal.ZERO;
            BigDecimal accountDebitCum = BigDecimal.ZERO;
            BigDecimal pl_cum_AccountDebit = BigDecimal.ZERO;
            BigDecimal swChargesGel = rq.getSwFees();
            BigDecimal swChargesCumGel = BigDecimal.ZERO;
            BigDecimal pl_cum_swChargesGel = BigDecimal.ZERO;
            BigDecimal fMoneyChargesGel = fMoneyCharges;
            BigDecimal pl_cum_fMoneyChargesGel = BigDecimal.ZERO;
            BigDecimal fMoneyChargesCumGel = BigDecimal.ZERO;
            BigDecimal productCodeFeeCum = BigDecimal.ZERO;
            int countProductCodeTrans = 0;

            BigDecimal balancePhnProCode = BigDecimal.ZERO;
            BigDecimal accountCreditCumPhnProCode = BigDecimal.ZERO;
            BigDecimal accountDebitCumPhnProCode = BigDecimal.ZERO;
            BigDecimal swChargesCumGelPhnProCode = BigDecimal.ZERO;
            BigDecimal fMoneyChargesCumGelPhnProCode = BigDecimal.ZERO;
            BigDecimal bookBalancePhnProCode = BigDecimal.ZERO;
            BigDecimal merchantBookedBalancePhnProCode = BigDecimal.ZERO;
            String phnProdCode = rq.getPhoneNumber() + rq.getProductCode();

            if (walletFundingSucessInfoRepo.findTopByOrderByIdDesc() != null) {
                BigDecimal balance = BigDecimal.ZERO;
                BigDecimal bookBalance = BigDecimal.ZERO;
                BigDecimal merchantBookedBalance = BigDecimal.ZERO;

                if (walletFundingSucessInfoRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Getting phonenumber to update wallet's cummulatives", phonenumber));
                    WalletFundSucInfo result;

                    Optional<WalletFundSucInfo> getTrans = walletFundingSucessInfoRepo.findByPhoneNumber(phonenumber);
                    result = getTrans.get();
                    w_amountPaidInCum = result.getAmountPaidInCum().add(dataVerifyateGetAmount);
                    w_swChargesCum = result.getSwChargesCum().add(getVerifyChargeAmount);
                    w_fMoneyChargesCum = result.getDemoPayChargesCum().add(fMoneyCharges);
                    w_amtCreToWalletCum = result.getAmtCreToWalletCum().add(balForWallet);

                }
                logger.info(String.format("Data exist in WalletFundSucInfo Table"));

                List<GenLedgAccount> getDeee = _genLedgAccountRepo.findByPhoneNumberProdCode(rq.getPhoneNumber(), rq.getProductCode());

                if (getDeee.size() > 0) {

                    logger.info(String.format("Getting phonenumber to update Gen Ledger Cummulative", phonenumber));
                    GenLedgAccount genLedResult;
                    Optional<GenLedgAccount> getGenLedgerTrans = _genLedgAccountRepo.findByOptPhoneNumberProdCode(rq.getPhoneNumberProductCode());
                    genLedResult = getGenLedgerTrans.get();

                    accountCreditCumPhnProCode = genLedResult.getAccountCreditCum().add(accountCredit);
                    accountDebitCumPhnProCode = genLedResult.getAccountDebitCum().add(accountDebit);
                    swChargesCumGelPhnProCode = genLedResult.getSwChargesCum().add(swChargesGel);
                    fMoneyChargesCumGelPhnProCode = genLedResult.getDemoPayChargesCum().add(fMoneyChargesGel);
                    balancePhnProCode = genLedResult.getBalance().add(BigDecimal.ZERO);
                    bookBalancePhnProCode = genLedResult.getBookBalance().subtract(rq.getTransAmount());
                    merchantBookedBalancePhnProCode = genLedResult.getMerchantBookedBalance().add(rq.getTransAmount());

                } else {

                    merchantBookedBalancePhnProCode = rq.getTransAmount();

                }

                if (_genLedgAccountRepo.existsByPhoneNumber(phonenumber)) {

                    logger.info(String.format("Getting phonenumber to update Gen Ledger Cummulative", phonenumber));
                    GenLedgAccount genLedResult;
                    Optional<GenLedgAccount> getGenLedgerTrans = _genLedgAccountRepo.findByPhoneNumber(phonenumber);
                    genLedResult = getGenLedgerTrans.get();

                    accountCreditCum = genLedResult.getAccountCreditCum().add(accountCredit);

                    accountDebitCum = genLedResult.getAccountDebitCum().add(accountDebit);
                    swChargesCumGel = genLedResult.getSwChargesCum().add(swChargesGel);
                    fMoneyChargesCumGel = genLedResult.getDemoPayChargesCum().add(fMoneyChargesGel);
                    balance = genLedResult.getBalance().add(BigDecimal.ZERO);

                    bookBalance = genLedResult.getBookBalance().subtract(BigDecimal.ZERO);
                    merchantBookedBalance = genLedResult.getMerchantBookedBalance().add(rq.getTransAmount());

                } else {

                    merchantBookedBalance = rq.getTransAmount();

                }
                if (_genLedgAccountRepo.existsByProductCode(rq.getProductCode())) {

                    logger.info(String.format("Getting productCode to update Gen Ledger Cummulative"));
                    GenLedgAccount genLedResult;
                    Optional<GenLedgAccount> getGenLedgerTrans = _genLedgAccountRepo.findByProductCode(rq.getProductCode());
                    genLedResult = getGenLedgerTrans.get();
                    productCodeFeeCum = genLedResult.getDemoPayChargesCum().add(fMoneyChargesGel);
                    countProductCodeTrans = genLedResult.getCountProductCodeTrans() + 1;

                } else {

                    productCodeFeeCum = fMoneyChargesGel;
                    countProductCodeTrans = +1;
                }

                logger.info(String.format("Data exist in GenLedgAccount Table"));

                GenLedgAccount genLedgAccount = _genLedgAccountRepo.findTopByOrderByIdDesc();
                pl_cum_AccountCredit = genLedgAccount.getPl_cum_AccountCredit().add(accountCredit);
                pl_cum_AccountDebit = genLedgAccount.getPl_cum_AccountDebit().add(accountDebit);
                pl_cum_swChargesGel = genLedgAccount.getPl_cum_swCharges().add(swChargesGel);
                pl_cum_fMoneyChargesGel = genLedgAccount.getPl_cum_fMoneyCharges().add(fMoneyChargesGel);

                //save to gen ledger...
                logger.info(String.format("Saving to General Ledger Table"));
                GenLedgAccount genLedger = new GenLedgAccount(fMoneyTransId, phonenumber,
                        fundingType, accountCredit, balance, bookBalance, accountCreditCum,
                        pl_cum_AccountCredit, accountDebit, accountDebitCum, pl_cum_AccountDebit,
                        swChargesGel, swChargesCumGel, pl_cum_swChargesGel,
                        fMoneyChargesGel, pl_cum_fMoneyChargesGel, fMoneyChargesCumGel, narration,
                        merchantBookedBalance, rq.getProductCode(), rq.getProductName(), countProductCodeTrans,
                        productCodeFeeCum, rq.getPhoneNumberProductCode(), balancePhnProCode, accountCreditCumPhnProCode, accountDebitCumPhnProCode,
                        swChargesCumGelPhnProCode, fMoneyChargesCumGelPhnProCode,
                        bookBalancePhnProCode, merchantBookedBalancePhnProCode);
                _genLedgAccountRepo.save(genLedger);

                //save to WalletFundSucInfo for Indirect Funding
                WalletFundSucInfo wallSucessInfoResult = walletFundingSucessInfoRepo.findTopByOrderByIdDesc();
                p_amountPaidInCum = wallSucessInfoResult.getAmountPaidInCum().add(dataVerifyateGetAmount);
                p_swChargesCum = wallSucessInfoResult.getSwChargesCum().add(getVerifyChargeAmount);
                p_fMoneyChargesCum = wallSucessInfoResult.getDemoPayChargesCum().add(fMoneyCharges);
                p_amtCreToWalletCum = wallSucessInfoResult.getAmtCreToWalletCum().add(balForWallet);
                logger.info(String.format("Saving to WalletFundSucInfo Table"));

                WalletFundSucInfo wallSucessInfo = new WalletFundSucInfo(
                        dataVerifyateGetAmount, w_amountPaidInCum, p_amountPaidInCum,
                        getVerifyChargeAmount, w_swChargesCum, p_swChargesCum,
                        fMoneyCharges, w_fMoneyChargesCum, p_fMoneyChargesCum,
                        balForWallet, w_amtCreToWalletCum, p_amtCreToWalletCum,
                        phonenumber, fMoneyTransId,
                        rq.getSwRefrenceNumber(), "No Validation from Bank",
                        rq.getSwRefrenceNumber(), rq.getSwRefrenceNumber()
                );
                walletFundingSucessInfoRepo.save(wallSucessInfo);
                //save to gen ledger cummulative...
                GenLedgAccountCum genLedgAccountCum;

                if (_genLedgAccountCumRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Saving to GenLedgAccountCum Table phoneNumber exists"));
                    Optional<GenLedgAccountCum> getRecordGenLedgAccountCumCum = _genLedgAccountCumRepo.findByPhoneNumber(phonenumber);
                    genLedgAccountCum = getRecordGenLedgAccountCumCum.get();
                    //roll back do not decrease count

                    genLedgAccountCum.setCountSuccessTrans(genLedgAccountCum.getCountSuccessTrans() - 1);
                    genLedgAccountCum.setTotalAmountCredited(genLedgAccountCum.getTotalAmountCredited().add(accountCredit));
                    genLedgAccountCum.setTotalAmountDebited(genLedgAccountCum.getTotalAmountDebited().add(accountDebit));
                    genLedgAccountCum.setTotalPayCharge(genLedgAccountCum.getTotalPayCharge().add(fMoneyChargesGel));
                    genLedgAccountCum.setTotalSwCharges(genLedgAccountCum.getTotalSwCharges().add(swChargesGel));
                    genLedgAccountCum.setTotalBalance(genLedgAccountCum.getTotalBalance().add(BigDecimal.ZERO));
                    genLedgAccountCum.setTotalBookBalance(genLedgAccountCum.getTotalBookBalance().subtract(BigDecimal.ZERO));
                    genLedgAccountCum.setTotalMerchantBookedBalance(genLedgAccountCum.getTotalMerchantBookedBalance().add(rq.getTransAmount()));

                    _genLedgAccountCumRepo.save(genLedgAccountCum);

                }

                if (_genLedgAccountCumRepo.existsByPhnProductCode(rq.getPhoneNumber() + rq.getProductCode())) {
                    logger.info(String.format("Saving to GenLedgAccountCum Table phone-productcode exists"));

                    Optional<GenLedgAccountCum> getRecordGenLedgAccountCumCum = _genLedgAccountCumRepo.findByPhnProductCode(rq.getPhoneNumber() + rq.getProductCode());
                    genLedgAccountCum = getRecordGenLedgAccountCumCum.get();
                    //roll back do not decrease count

                    genLedgAccountCum.setCountSuccTransPhnProCode(genLedgAccountCum.getCountSuccTransPhnProCode() - 1);
                    genLedgAccountCum.setTotalAmtCreditedPhnProCode(genLedgAccountCum.getTotalAmtCreditedPhnProCode().add(accountCredit));
                    genLedgAccountCum.setTotalAmtDebitedPhnProCode(genLedgAccountCum.getTotalAmtDebitedPhnProCode().add(accountDebit));
                    genLedgAccountCum.setTotalPayChargePhnProCode(genLedgAccountCum.getTotalPayChargePhnProCode().add(fMoneyChargesGel));
                    genLedgAccountCum.setTotalSwChargesPhnProCode(genLedgAccountCum.getTotalSwChargesPhnProCode().add(swChargesGel));
                    genLedgAccountCum.setTotalBalancePhnProCode(genLedgAccountCum.getTotalBalancePhnProCode().add(BigDecimal.ZERO));
                    genLedgAccountCum.setTotalBookBalancePhnProCode(genLedgAccountCum.getTotalBookBalancePhnProCode().subtract(BigDecimal.ZERO));
                    genLedgAccountCum.setTotalMerBookedBalPhnProCode(genLedgAccountCum.getTotalMerBookedBalPhnProCode().add(rq.getTransAmount()));

                    _genLedgAccountCumRepo.save(genLedgAccountCum);

                } else {

                    logger.info(String.format("Saving to genLedgAccountCum Table phoneNumber first entry"));
                    GenLedgAccountCum genLedgAcct = new GenLedgAccountCum(
                            phonenumber, phonenumber, 1, accountCredit, accountCredit, bookBalance, accountDebit,
                            swChargesGel, fMoneyChargesGel, rq.getTransAmount(), phnProdCode, 1, accountCredit, accountCredit, bookBalance, accountDebit,
                            swChargesGel, fMoneyChargesGel, rq.getTransAmount()
                    );
                    _genLedgAccountCumRepo.save(genLedgAcct);
                }

                WalletFundingInfoCum walletFundingInfoCum;

                if (walletFundingInfoCumRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Saving to WalletFundingInfoCum Table phoneNumber exists"));
                    Optional<WalletFundingInfoCum> getRecordWalletFundingInfoCumRe = walletFundingInfoCumRepo.findByPhoneNumber(phonenumber);

                    walletFundingInfoCum = getRecordWalletFundingInfoCumRe.get();
                    //roll back do not decrease count

                    walletFundingInfoCum.setCountSuccessTrans(walletFundingInfoCum.getCountSuccessTrans() - 1);
                    walletFundingInfoCum.setTotalAmountPaidIn(walletFundingInfoCum.getTotalAmountPaidIn().add(dataVerifyateGetAmount));
                    walletFundingInfoCum.setTotalAmtCreToWallet(walletFundingInfoCum.getTotalAmtCreToWallet().add(balForWallet));
                    walletFundingInfoCum.setTotalFMoneyChrge(walletFundingInfoCum.getTotalFMoneyChrge().add(fMoneyCharges));
                    walletFundingInfoCum.setTotalSwCharges(walletFundingInfoCum.getTotalSwCharges().add(getVerifyChargeAmount));
                    walletFundingInfoCumRepo.save(walletFundingInfoCum);
                    //send SMS to subscriber
                    // responseModel.setDescription("Request initiated successfully");
                    responseModel.setStatusCode(200);
                } else {
                    logger.info(String.format("Saving to WalletFundingInfoCum Table phoneNumber first entry"));
                    WalletFundingInfoCum wallFundingCum = new WalletFundingInfoCum(
                            phonenumber, phonenumber, 1,
                            dataVerifyateGetAmount, getVerifyChargeAmount, fMoneyCharges,
                            balForWallet
                    );
                    walletFundingInfoCumRepo.save(wallFundingCum);
                    //send SMS to subscriber

                    responseModel.setStatusCode(200);
                }

            } else {

                logger.info(String.format("Data does not exist in WalletFundSucInfo and GenLedgAccount Table"));

                //save to gen ledger...
                logger.info(String.format("Saving to General-Ledger Table"));
                GenLedgAccount genLedger = new GenLedgAccount(fMoneyTransId, phonenumber, "In-Direct Wallet Funding", accountCredit,
                        accountCredit, BigDecimal.ZERO, accountCredit,
                        accountCredit, accountDebit, accountDebit, accountDebit, swChargesGel, swChargesGel, swChargesGel,
                        fMoneyChargesGel, fMoneyChargesGel, fMoneyChargesGel, narration, rq.getTransAmount(),
                        rq.getProductCode(), rq.getProductName(), 1,
                        fMoneyChargesGel, rq.getPhoneNumberProductCode(), balancePhnProCode, accountCreditCumPhnProCode, accountDebitCumPhnProCode,
                        swChargesCumGelPhnProCode, fMoneyChargesCumGelPhnProCode, bookBalancePhnProCode, merchantBookedBalancePhnProCode);
                _genLedgAccountRepo.save(genLedger);

                logger.info(String.format("Saving to WalletFundSucInfo Table"));
                WalletFundSucInfo wallSucessInfo = new WalletFundSucInfo(
                        dataVerifyateGetAmount, dataVerifyateGetAmount, dataVerifyateGetAmount,
                        getVerifyChargeAmount, getVerifyChargeAmount, getVerifyChargeAmount,
                        fMoneyCharges, fMoneyCharges, fMoneyCharges,
                        balForWallet, balForWallet, balForWallet,
                        phonenumber, fMoneyTransId,
                        rq.getSwRefrenceNumber(), "No Validation from Bank",
                        rq.getSwRefrenceNumber(), rq.getSwRefrenceNumber()
                );
                walletFundingSucessInfoRepo.save(wallSucessInfo);

                WalletFundingInfoCum walletFundingInfoCum;

                if (walletFundingInfoCumRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Saving to WalletFundingInfoCum Table phoneNumber exists"));
                    Optional<WalletFundingInfoCum> getRecordWalletFundingInfoCumRe = walletFundingInfoCumRepo.findByPhoneNumber(phonenumber);

                    walletFundingInfoCum = getRecordWalletFundingInfoCumRe.get();
                    //roll back do not decrease count

                    walletFundingInfoCum.setCountSuccessTrans(walletFundingInfoCum.getCountSuccessTrans() - 1);
                    walletFundingInfoCum.setTotalAmountPaidIn(walletFundingInfoCum.getTotalAmountPaidIn().add(dataVerifyateGetAmount));
                    walletFundingInfoCum.setTotalAmtCreToWallet(walletFundingInfoCum.getTotalAmtCreToWallet().add(balForWallet));
                    walletFundingInfoCum.setTotalFMoneyChrge(walletFundingInfoCum.getTotalFMoneyChrge().add(fMoneyCharges));
                    walletFundingInfoCum.setTotalSwCharges(walletFundingInfoCum.getTotalSwCharges().add(getVerifyChargeAmount));
                    walletFundingInfoCumRepo.save(walletFundingInfoCum);
                    //send SMS to subscriber
                    responseModel.setStatusCode(200);
                } else {
                    logger.info(String.format("Saving to WalletFundingInfoCum Table phoneNumber first entry"));
                    WalletFundingInfoCum wallFundingCum = new WalletFundingInfoCum(
                            phonenumber, phonenumber, 1,
                            dataVerifyateGetAmount, getVerifyChargeAmount, fMoneyCharges,
                            balForWallet
                    );
                    walletFundingInfoCumRepo.save(wallFundingCum);
                    //send SMS to subscriber
                    responseModel.setStatusCode(200);
                }
                GenLedgAccountCum genLedgAccountCum;

                if (_genLedgAccountCumRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Saving to GenLedgAccountCum Table phoneNumber exists"));
                    Optional<GenLedgAccountCum> getRecordGenLedgAccountCumCum = _genLedgAccountCumRepo.findByPhoneNumber(phonenumber);
                    genLedgAccountCum = getRecordGenLedgAccountCumCum.get();
                    //roll back do not decrease count

                    genLedgAccountCum.setCountSuccessTrans(genLedgAccountCum.getCountSuccessTrans() - 1);
                    genLedgAccountCum.setTotalAmountCredited(genLedgAccountCum.getTotalAmountCredited().add(accountCredit));
                    genLedgAccountCum.setTotalAmountDebited(genLedgAccountCum.getTotalAmountDebited().add(accountDebit));
                    genLedgAccountCum.setTotalPayCharge(genLedgAccountCum.getTotalPayCharge().add(fMoneyChargesGel));
                    genLedgAccountCum.setTotalSwCharges(genLedgAccountCum.getTotalSwCharges().add(swChargesGel));
                    genLedgAccountCum.setTotalBalance(genLedgAccountCum.getTotalBalance().add(BigDecimal.ZERO));
                    genLedgAccountCum.setTotalBookBalance(genLedgAccountCum.getTotalBookBalance().subtract(BigDecimal.ZERO));
                    genLedgAccountCum.setTotalMerchantBookedBalance(genLedgAccountCum.getTotalMerchantBookedBalance().add(rq.getTransAmount()));

                    _genLedgAccountCumRepo.save(genLedgAccountCum);

                }
                if (_genLedgAccountCumRepo.existsByPhnProductCode(rq.getPhoneNumber() + rq.getProductCode())) {
                    logger.info(String.format("Saving to GenLedgAccountCum Table phone-productcode exists"));

                    Optional<GenLedgAccountCum> getRecordGenLedgAccountCumCum = _genLedgAccountCumRepo.findByPhnProductCode(rq.getPhoneNumber() + rq.getProductCode());
                    genLedgAccountCum = getRecordGenLedgAccountCumCum.get();
                    genLedgAccountCum.setCountSuccTransPhnProCode(genLedgAccountCum.getCountSuccTransPhnProCode() - 1);
                    genLedgAccountCum.setTotalAmtCreditedPhnProCode(genLedgAccountCum.getTotalAmtCreditedPhnProCode().add(accountCredit));
                    genLedgAccountCum.setTotalAmtDebitedPhnProCode(genLedgAccountCum.getTotalAmtDebitedPhnProCode().add(accountDebit));
                    genLedgAccountCum.setTotalPayChargePhnProCode(genLedgAccountCum.getTotalPayChargePhnProCode().add(fMoneyChargesGel));
                    genLedgAccountCum.setTotalSwChargesPhnProCode(genLedgAccountCum.getTotalSwChargesPhnProCode().add(swChargesGel));
                    genLedgAccountCum.setTotalBalancePhnProCode(genLedgAccountCum.getTotalBalancePhnProCode().add(BigDecimal.ZERO));
                    genLedgAccountCum.setTotalBookBalancePhnProCode(genLedgAccountCum.getTotalBookBalancePhnProCode().subtract(BigDecimal.ZERO));
                    genLedgAccountCum.setTotalMerBookedBalPhnProCode(genLedgAccountCum.getTotalMerBookedBalPhnProCode().add(rq.getTransAmount()));

                    _genLedgAccountCumRepo.save(genLedgAccountCum);

                    //roll back do not decrease count
                } else {

                    logger.info(String.format("Saving to genLedgAccountCum Table phoneNumber first entry"));
                    GenLedgAccountCum genLedgAcct = new GenLedgAccountCum(
                            phonenumber, phonenumber, 1, accountCredit, accountCredit, BigDecimal.ZERO, accountDebit,
                            swChargesGel, fMoneyChargesGel, rq.getTransAmount(), phnProdCode, 1, accountCredit, accountCredit, BigDecimal.ZERO, accountDebit,
                            swChargesGel, fMoneyChargesGel, rq.getTransAmount()
                    );
                    _genLedgAccountCumRepo.save(genLedgAcct);
                }
                responseModel.setStatusCode(200);
            }
        } catch (Exception ex) {
            responseModel.setDescription(statusMessage);
            responseModel.setStatusCode(statusCode);
            ex.printStackTrace();
        }

        return responseModel;

    }

    public BaseResponse escrowDebitLedgerMerchantBookedBalance(
            ProcLedgerRequestDebitOneTime rq) {

        String transactionId = rq.getTransactionId();
        String phonenumber = rq.getPhonenumber();
        String description = rq.getDescription();
        String finalCharges = "0";
        String fees = rq.getFees();
        String narration = rq.getNarration();

        BaseResponse responseModel = new BaseResponse();
        int statusCode = 500;
        String statusMessage = "An error occured,please try again";
        try {
            statusCode = 400;

            BigDecimal accountCredit = BigDecimal.ZERO;
            BigDecimal accountDebitCum = BigDecimal.ZERO;
            BigDecimal swChargesCumGel = BigDecimal.ZERO;
            BigDecimal fMoneyChargesCumGel = BigDecimal.ZERO;
            BigDecimal balance = BigDecimal.ZERO;
            BigDecimal bookBalance = BigDecimal.ZERO;
            BigDecimal swChargesGel = BigDecimal.ZERO;
            BigDecimal accountCreditCum = accountCredit;
            BigDecimal pl_cum_AccountCredit = BigDecimal.ZERO;
            BigDecimal pl_cum_AccountDebit = BigDecimal.ZERO;
            BigDecimal pl_cum_swChargesGel = BigDecimal.ZERO;
            BigDecimal pl_cum_fMoneyChargesGel = BigDecimal.ZERO;
            BigDecimal merchantBookedBalance = BigDecimal.ZERO;
            BigDecimal productCodeFeeCum = BigDecimal.ZERO;
            int countProductCodeTrans = 0;

            BigDecimal balancePhnProCode = BigDecimal.ZERO;
            BigDecimal accountCreditCumPhnProCode = BigDecimal.ZERO;
            BigDecimal accountDebitCumPhnProCode = BigDecimal.ZERO;
            BigDecimal swChargesCumGelPhnProCode = BigDecimal.ZERO;
            BigDecimal fMoneyChargesCumGelPhnProCode = BigDecimal.ZERO;
            BigDecimal bookBalancePhnProCode = BigDecimal.ZERO;
            BigDecimal merchantBookedBalancePhnProCode = BigDecimal.ZERO;
            String phnProdCode = rq.getPhonenumber() + rq.getProductCode();

            if (_genLedgAccountRepo.findTopByOrderByIdDesc() != null) {

                List<GenLedgAccount> getDeee = _genLedgAccountRepo.findByPhoneNumberProdCode(rq.getPhonenumber(), rq.getProductCode());

                if (getDeee.size() > 0) {

                    logger.info(String.format("Getting phonenumber to update Gen Ledger Cummulative", phonenumber));
                    GenLedgAccount genLedResult;
                    Optional<GenLedgAccount> getGenLedgerTrans = _genLedgAccountRepo.findByOptPhoneNumberProdCode(rq.getPhoneNumberProductCode());
                    genLedResult = getGenLedgerTrans.get();

                    accountCreditCumPhnProCode = genLedResult.getAccountCreditCum().add(accountCredit);
                    accountDebitCumPhnProCode = genLedResult.getAccountDebitCum().add(new BigDecimal(finalCharges));
                    swChargesCumGelPhnProCode = genLedResult.getSwChargesCum().add(swChargesGel);
                    fMoneyChargesCumGelPhnProCode = genLedResult.getDemoPayChargesCum().add(new BigDecimal(fees));
                    balancePhnProCode = genLedResult.getBalance().subtract(new BigDecimal(finalCharges));
                    bookBalancePhnProCode = genLedResult.getBookBalance().subtract(new BigDecimal(finalCharges));
                    merchantBookedBalancePhnProCode = genLedResult.getMerchantBookedBalance().subtract(new BigDecimal(rq.getFinalCharges()));

                }

                if (_genLedgAccountRepo.existsByPhoneNumber(phonenumber)) {

                    logger.info(String.format("Getting phonenumber to update Gen Ledger Cummulative", phonenumber));
                    GenLedgAccount genLedResult;
                    Optional<GenLedgAccount> getGenLedgerTrans = _genLedgAccountRepo.findByPhoneNumber(phonenumber);
                    genLedResult = getGenLedgerTrans.get();

                    accountCreditCum = genLedResult.getAccountCreditCum().add(accountCredit);

                    accountDebitCum = genLedResult.getAccountDebitCum().add(new BigDecimal(finalCharges));
                    swChargesCumGel = genLedResult.getSwChargesCum().add(swChargesGel);
                    fMoneyChargesCumGel = genLedResult.getDemoPayChargesCum().add(new BigDecimal(fees));
                    balance = genLedResult.getBalance().subtract(new BigDecimal(finalCharges));
                    bookBalance = genLedResult.getBookBalance().subtract(new BigDecimal(finalCharges));
                    merchantBookedBalance = genLedResult.getMerchantBookedBalance().subtract(new BigDecimal(rq.getFinalCharges()));

                }

                if (_genLedgAccountRepo.existsByProductCode(rq.getProductCode())) {

                    logger.info(String.format("Getting productCode to update Gen Ledger Cummulative"));
                    GenLedgAccount genLedResult;
                    Optional<GenLedgAccount> getGenLedgerTrans = _genLedgAccountRepo.findByProductCode(rq.getProductCode());
                    genLedResult = getGenLedgerTrans.get();
                    productCodeFeeCum = genLedResult.getDemoPayChargesCum().add(new BigDecimal(fees));
                    countProductCodeTrans = genLedResult.getCountProductCodeTrans() + 1;

                }

                logger.info(String.format("Data exist in GenLedgAccount Table"));

                GenLedgAccount genLedgAccount = _genLedgAccountRepo.findTopByOrderByIdDesc();
                pl_cum_AccountCredit = genLedgAccount.getPl_cum_AccountCredit().add(accountCredit);
                pl_cum_AccountDebit = genLedgAccount.getPl_cum_AccountDebit().add(new BigDecimal(finalCharges));
                pl_cum_swChargesGel = genLedgAccount.getPl_cum_swCharges().add(swChargesGel);
                pl_cum_fMoneyChargesGel = genLedgAccount.getPl_cum_fMoneyCharges().add(new BigDecimal(fees));

                //save to gen ledger...
                logger.info(String.format("Saving to General Ledger Table"));
                GenLedgAccount genLedger = new GenLedgAccount(transactionId, phonenumber, description,
                        accountCredit, balance, bookBalance, accountCreditCum,
                        pl_cum_AccountCredit,
                        new BigDecimal(finalCharges),
                        accountDebitCum, pl_cum_AccountDebit, swChargesGel, swChargesCumGel, pl_cum_swChargesGel,
                        new BigDecimal(fees), pl_cum_fMoneyChargesGel, fMoneyChargesCumGel, narration,
                        merchantBookedBalance, rq.getProductCode(), rq.getProductName(), countProductCodeTrans,
                        productCodeFeeCum, rq.getPhoneNumberProductCode(), balancePhnProCode, accountCreditCumPhnProCode, accountDebitCumPhnProCode,
                        swChargesCumGelPhnProCode, fMoneyChargesCumGelPhnProCode,
                        bookBalancePhnProCode, merchantBookedBalancePhnProCode);
                _genLedgAccountRepo.save(genLedger);

                //save to gen ledger cummulative...
                GenLedgAccountCum genLedgAccountCum;

                if (_genLedgAccountCumRepo.existsByPhoneNumber(phonenumber)) {
                    logger.info(String.format("Saving to GenLedgAccountCum Table phoneNumber exists"));
                    Optional<GenLedgAccountCum> getRecordGenLedgAccountCumCum = _genLedgAccountCumRepo.findByPhoneNumber(phonenumber);
                    genLedgAccountCum = getRecordGenLedgAccountCumCum.get();
                    //wen they finally release I will not increase count

                    genLedgAccountCum.setCountSuccessTrans(genLedgAccountCum.getCountSuccessTrans());
                    genLedgAccountCum.setTotalAmountCredited(genLedgAccountCum.getTotalAmountCredited().add(accountCredit));
                    genLedgAccountCum.setTotalAmountDebited(genLedgAccountCum.getTotalAmountDebited().add(new BigDecimal(finalCharges)));
                    genLedgAccountCum.setTotalPayCharge(genLedgAccountCum.getTotalPayCharge().add(new BigDecimal(fees)));
                    genLedgAccountCum.setTotalSwCharges(genLedgAccountCum.getTotalSwCharges().add(swChargesGel));
                    genLedgAccountCum.setTotalBalance(genLedgAccountCum.getTotalBalance().subtract(new BigDecimal(finalCharges)));
                    genLedgAccountCum.setTotalBookBalance(genLedgAccountCum.getTotalBookBalance().subtract(new BigDecimal(finalCharges)));
                    genLedgAccountCum.setTotalMerchantBookedBalance(genLedgAccountCum.getTotalMerchantBookedBalance().subtract(new BigDecimal(rq.getFinalCharges())));

                    _genLedgAccountCumRepo.save(genLedgAccountCum);

                }

                if (_genLedgAccountCumRepo.existsByPhnProductCode(rq.getPhonenumber() + rq.getProductCode())) {
                    logger.info(String.format("Saving to GenLedgAccountCum Table phone-productcode exists"));

                    Optional<GenLedgAccountCum> getRecordGenLedgAccountCumCum = _genLedgAccountCumRepo.findByPhnProductCode(rq.getPhonenumber() + rq.getProductCode());
                    genLedgAccountCum = getRecordGenLedgAccountCumCum.get();

                    //wen they finally release I will not increase count
                    genLedgAccountCum.setCountSuccTransPhnProCode(genLedgAccountCum.getCountSuccTransPhnProCode());
                    genLedgAccountCum.setTotalAmtCreditedPhnProCode(genLedgAccountCum.getTotalAmtCreditedPhnProCode().add(accountCredit));
                    genLedgAccountCum.setTotalAmtDebitedPhnProCode(genLedgAccountCum.getTotalAmtDebitedPhnProCode().add(new BigDecimal(finalCharges)));
                    genLedgAccountCum.setTotalPayChargePhnProCode(genLedgAccountCum.getTotalPayChargePhnProCode().add(new BigDecimal(fees)));
                    genLedgAccountCum.setTotalSwChargesPhnProCode(genLedgAccountCum.getTotalSwChargesPhnProCode().add(swChargesGel));
                    genLedgAccountCum.setTotalBalancePhnProCode(genLedgAccountCum.getTotalBalancePhnProCode().subtract(new BigDecimal(finalCharges)));
                    genLedgAccountCum.setTotalBookBalancePhnProCode(genLedgAccountCum.getTotalBookBalancePhnProCode().subtract(new BigDecimal(finalCharges)));
                    genLedgAccountCum.setTotalMerBookedBalPhnProCode(genLedgAccountCum.getTotalMerBookedBalPhnProCode().subtract(new BigDecimal(rq.getFinalCharges())));

                    _genLedgAccountCumRepo.save(genLedgAccountCum);

                }

                responseModel.setDescription("Account debited sucessfully.");
                responseModel.setStatusCode(200);
            } else {
                responseModel.setDescription("Account does not exist in the Ledger.");
                responseModel.setStatusCode(statusCode);
            }

        } catch (Exception ex) {
            responseModel.setDescription(statusMessage);
            responseModel.setStatusCode(statusCode);
            ex.printStackTrace();
        }

        return responseModel;

    }

}
