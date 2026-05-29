/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smart.core.centralized.wallet.generalledger.services;

import com.smart.core.centralized.wallet.generalledger.domains.GenLedgAccountCum;
import com.smart.core.centralized.wallet.generalledger.domains.GlobalLimitConfig;
import com.smart.core.centralized.wallet.generalledger.domains.Onboarded;
import com.smart.core.centralized.wallet.generalledger.domains.ProcessorFailedTransInfo;
import com.smart.core.centralized.wallet.generalledger.domains.RequestCreditAcctLog;
import com.smart.core.centralized.wallet.generalledger.domains.RequestDebitAcctLog;
import com.smart.core.centralized.wallet.generalledger.domains.UserLimitConfig;
import com.smart.core.centralized.wallet.generalledger.models.AddNewUserToLimit;
import com.smart.core.centralized.wallet.generalledger.models.AddWalletNo;
import com.smart.core.centralized.wallet.generalledger.models.BaseResponse;
import com.smart.core.centralized.wallet.generalledger.models.CheckWallet;
import com.smart.core.centralized.wallet.generalledger.models.CreditWallet;
import com.smart.core.centralized.wallet.generalledger.models.DebitWallet;
import com.smart.core.centralized.wallet.generalledger.models.ProcLedgerRequestCreditOneTime;
import com.smart.core.centralized.wallet.generalledger.models.ProcLedgerRequestDebitOneTime;
import com.smart.core.centralized.wallet.generalledger.models.RequestDebitWallet;
import com.smart.core.centralized.wallet.generalledger.models.UpgradeUserToLimit;
import com.smart.core.centralized.wallet.generalledger.models.UpgradeWalletNo;
import com.smart.core.centralized.wallet.generalledger.models.WalletInfo;
import com.smart.core.centralized.wallet.generalledger.proxies.UtilitiesProxy;
import com.smart.core.centralized.wallet.generalledger.repository.GenLedgAccountCumRepo;
import com.smart.core.centralized.wallet.generalledger.repository.GenLedgAccountRepo;
import com.smart.core.centralized.wallet.generalledger.repository.GlobalLimitConfigRepo;
import com.smart.core.centralized.wallet.generalledger.repository.OnboardedRepo;
import com.smart.core.centralized.wallet.generalledger.repository.ProcessorFailedTransInfoRepo;
import com.smart.core.centralized.wallet.generalledger.repository.RequestCreditAcctLogRepo;
import com.smart.core.centralized.wallet.generalledger.repository.RequestDebitAcctLogRepo;
import com.smart.core.centralized.wallet.generalledger.repository.UserLimitConfigRepo;
import com.smart.core.centralized.wallet.generalledger.utils.DecodedJWTToken;
import com.smart.core.centralized.wallet.generalledger.utils.GlobalMethods;
import com.smart.core.centralized.wallet.generalledger.utils.UttilityMethods;
import com.google.gson.Gson;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

/**
 *
 * @author SmartCore Contributors
 */
@Service
public class GenLedgerUtilityService {

    private final OnboardedRepo onboardedRepo;
    private final ProcessorFailedTransInfoRepo processorFailedTransInfoRepo;
    private final UtilitiesProxy utilitiesProxy;
    private final UttilityMethods utilMeth;
    private final GenLedgAccountCumRepo _genLedgAccountCumRepo;
    private final UserLimitConfigRepo userLimitConfigRepo;
    private final GlobalLimitConfigRepo globalLimitConfigRepo;
    private final RequestDebitAcctLogRepo requestDebitAcctLogRepo;

    private final RequestCreditAcctLogRepo requestCreditAcctLogRepo;
    @Autowired
    GenLedgerServices genLedgerServices;

    public GenLedgerUtilityService(OnboardedRepo onboardedRepo,
            ProcessorFailedTransInfoRepo processorFailedTransInfoRepo,
            UtilitiesProxy utilitiesProxy, UttilityMethods utilMeth,
            GenLedgAccountCumRepo _genLedgAccountCumRepo,
            UserLimitConfigRepo userLimitConfigRepo,
            GlobalLimitConfigRepo globalLimitConfigRepo,
            RequestDebitAcctLogRepo requestDebitAcctLogRepo,
            RequestCreditAcctLogRepo requestCreditAcctLogRepo) {
        this.onboardedRepo = onboardedRepo;
        this.processorFailedTransInfoRepo = processorFailedTransInfoRepo;
        this.utilitiesProxy = utilitiesProxy;
        this.utilMeth = utilMeth;
        this._genLedgAccountCumRepo = _genLedgAccountCumRepo;
        this.userLimitConfigRepo = userLimitConfigRepo;
        this.globalLimitConfigRepo = globalLimitConfigRepo;
        this.requestDebitAcctLogRepo = requestDebitAcctLogRepo;
        this.requestCreditAcctLogRepo = requestCreditAcctLogRepo;

    }

    public BaseResponse checkIfWalletExists(CheckWallet rq, String auth) {
        BaseResponse responseModel = new BaseResponse();
        int statusCode = 500;
        String statusMessage = "An error occured,please try again";
        try {

            DecodedJWTToken getDecoded = DecodedJWTToken.getDecoded(auth);

            statusCode = 400;

            if (!getDecoded.productCode.equals(rq.getProductCode())) {

                ProcessorFailedTransInfo procFailedTrans = new ProcessorFailedTransInfo("check-wallet-no",
                        "Invalid product code!",
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getWalletNo(), "", "General-Ledger-Service", getDecoded.productCode);

                processorFailedTransInfoRepo.save(procFailedTrans);
                responseModel.setDescription("Invalid product code!");
                responseModel.setStatusCode(statusCode);

                return responseModel;

            }

            if (onboardedRepo.existsByWalletNo(rq.getWalletNo()) == false) {

                ProcessorFailedTransInfo procFailedTrans = new ProcessorFailedTransInfo("check-wallet-no",
                        "Wallet Number does not exist!",
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getWalletNo(), "", "General-Ledger-Service", getDecoded.productCode);

                processorFailedTransInfoRepo.save(procFailedTrans);
                responseModel.setDescription("Wallet Number does not exist!");
                responseModel.setStatusCode(statusCode);

                return responseModel;

            }

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

    public BaseResponse addWalletNo(AddWalletNo rq, String auth) {
        BaseResponse responseModel = new BaseResponse();
        int statusCode = 500;
        String statusMessage = "An error occured,please try again";
        try {

            statusCode = 400;

            DecodedJWTToken getDecoded = DecodedJWTToken.getDecoded(auth);

            if (!getDecoded.productCode.equals(rq.getProductCode())) {

                ProcessorFailedTransInfo procFailedTrans = new ProcessorFailedTransInfo("check-wallet-no",
                        "Invalid product code!",
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getWalletNo(), "", "General-Ledger-Service", getDecoded.productCode);

                processorFailedTransInfoRepo.save(procFailedTrans);
                responseModel.setDescription("Invalid product code!");
                responseModel.setStatusCode(statusCode);

                return responseModel;

            }

            /* List<Onboarded> getOnbord = onboardedRepo.findByWalletNoProductCode(rq.getWalletNo(), rq.getProductCode());

            if (getOnbord.size() > 0) {

                ProcessorFailedTransInfo procFailedTrans = new ProcessorFailedTransInfo("check-wallet-no",
                        "Wallet Number already exists!",
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getWalletNo(), "", "General-Ledger-Service", getDecoded.productCode);

                processorFailedTransInfoRepo.save(procFailedTrans);
                responseModel.setDescription("Wallet Number already exists");
                responseModel.setStatusCode(statusCode);

                return responseModel;

            }*/
            //add user to limit
            AddNewUserToLimit addd = new AddNewUserToLimit();
            addd.setCategory(utilMeth.getTier1());
            addd.setPhoneNumberProductCode(rq.getWalletNo() + rq.getProductCode());
            addd.setProductCode(rq.getProductCode());
            addd.setProductName(getDecoded.productName);
            addd.setWalletNumber(rq.getWalletNo());

            Onboarded ob = new Onboarded();
            ob.setCreatedDate(Instant.now());
            ob.setProductCode(rq.getProductCode());
            ob.setProductName(getDecoded.productName);
            ob.setStatus("1");
            ob.setWalletNo(rq.getWalletNo());
            ob.setPhoneNumbProductCode(rq.getWalletNo() + rq.getProductCode());
            ob.setCategory(utilMeth.getTier1());

            List<Onboarded> getUserConfig = onboardedRepo.findByWalletNoProductCode(rq.getWalletNo(), rq.getProductCode());
            if (getUserConfig.size() > 0) {
                Onboarded getUserConfigup = onboardedRepo.findByWalletNoProductCodeUpdate(rq.getWalletNo(), rq.getProductCode());
                getUserConfigup.setLastModifiedDate(Instant.now());
                onboardedRepo.save(getUserConfigup);

            } else {
                onboardedRepo.save(ob);
            }

            BaseResponse addUserToTier = utilitiesProxy.addNewUserToLimit(addd);
            if (addUserToTier.getStatusCode() != 200) {

                ProcessorFailedTransInfo procFailedTrans = new ProcessorFailedTransInfo("check-wallet-no",
                        addUserToTier.getDescription(),
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getWalletNo(), "", "General-Ledger-Service", getDecoded.productCode);

                processorFailedTransInfoRepo.save(procFailedTrans);
                responseModel.setDescription(addUserToTier.getDescription());
                responseModel.setStatusCode(statusCode);

                return responseModel;

            }

            responseModel.setDescription("Wallet Number added successfully");
            responseModel.setStatusCode(200);

            return responseModel;

        } catch (Exception ex) {
            responseModel.setDescription(statusMessage);
            responseModel.setStatusCode(statusCode);
            ex.printStackTrace();
        }

        return responseModel;

    }

    public BaseResponse updateWalletNoTier(UpgradeWalletNo rq, String auth) {
        BaseResponse responseModel = new BaseResponse();
        int statusCode = 500;
        String statusMessage = "An error occured,please try again";
        try {

            statusCode = 400;

            DecodedJWTToken getDecoded = DecodedJWTToken.getDecoded(auth);

            if (!getDecoded.productCode.equals(rq.getProductCode())) {

                ProcessorFailedTransInfo procFailedTrans = new ProcessorFailedTransInfo("check-wallet-no",
                        "Invalid product code!",
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getWalletNo(), "", "General-Ledger-Service", getDecoded.productCode);

                processorFailedTransInfoRepo.save(procFailedTrans);
                responseModel.setDescription("Invalid product code!");
                responseModel.setStatusCode(statusCode);

                return responseModel;

            }

            List<Onboarded> getOnbord = onboardedRepo.findByWalletNoProductCode(rq.getWalletNo(), rq.getProductCode());

            if (getOnbord.size() <= 0) {

                ProcessorFailedTransInfo procFailedTrans = new ProcessorFailedTransInfo("check-wallet-no",
                        "Wallet Number does not exists!",
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getWalletNo(), "", "General-Ledger-Service", getDecoded.productCode);

                processorFailedTransInfoRepo.save(procFailedTrans);
                responseModel.setDescription("Wallet Number does not exists!");
                responseModel.setStatusCode(statusCode);

                return responseModel;

            }

            //add user to limit
            UpgradeUserToLimit addd = new UpgradeUserToLimit();
            addd.setCategory(rq.getUserTier());
            addd.setPhoneNumberProductCode(rq.getWalletNo() + rq.getProductCode());
            addd.setProductCode(rq.getProductCode());
            addd.setWalletNumber(rq.getWalletNo());

            BaseResponse addUserToTier = utilitiesProxy.upgradeUserLimit(addd);
            if (addUserToTier.getStatusCode() != 200) {

                ProcessorFailedTransInfo procFailedTrans = new ProcessorFailedTransInfo("check-wallet-no",
                        addUserToTier.getDescription(),
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getWalletNo(), "", "General-Ledger-Service", getDecoded.productCode);

                processorFailedTransInfoRepo.save(procFailedTrans);
                responseModel.setDescription(addUserToTier.getDescription());
                responseModel.setStatusCode(statusCode);

                return responseModel;

            }
            Onboarded ob = onboardedRepo.findByWalletNoProductCodeUpdate(rq.getWalletNo(), rq.getProductCode());
            ob.setCategory(rq.getUserTier());
            ob.setLastModifiedDate(Instant.now());
            onboardedRepo.save(ob);

            responseModel.setDescription("Wallet Number added successfully");
            responseModel.setStatusCode(200);

            return responseModel;

        } catch (Exception ex) {
            responseModel.setDescription(statusMessage);
            responseModel.setStatusCode(statusCode);
            ex.printStackTrace();
        }

        return responseModel;

    }

    public BaseResponse saveGenLedgersDebitAccountOneTime(RequestDebitWallet rq, String auth) {

        BaseResponse responseModel = new BaseResponse();
        int statusCode = 500;
        String statusMessage = "An error occured,please try again";

        try {

            statusCode = 400;

            DecodedJWTToken getDecoded = DecodedJWTToken.getDecoded(auth);

            if (!getDecoded.productCode.equals(rq.getProductCode())) {

                ProcessorFailedTransInfo procFailedTrans = new ProcessorFailedTransInfo("save-GenLedgersDebitAcct",
                        "Invalid product code!",
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getPhoneNumber(), "", "General-Ledger-Service", getDecoded.productCode);

                processorFailedTransInfoRepo.save(procFailedTrans);
                responseModel.setDescription("Invalid product code!");
                responseModel.setStatusCode(statusCode);

                return responseModel;

            }

            if (!"Withdrawal".equals(rq.getTransType())) {

                ProcessorFailedTransInfo procFailedTrans = new ProcessorFailedTransInfo("save-GenLedgersDebitAcct",
                        "Invalid transaction type!",
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getPhoneNumber(), "", "General-Ledger-Service", getDecoded.productCode);

                processorFailedTransInfoRepo.save(procFailedTrans);
                responseModel.setDescription("Invalid transaction type!");
                responseModel.setStatusCode(statusCode);

                return responseModel;

            }
            //check if wallet exist

            String phoProCode = rq.getPhoneNumber() + rq.getProductCode();

            List<Onboarded> getOnbord = onboardedRepo.findByWalletNoProductCode(rq.getPhoneNumber(), rq.getProductCode());

            if (getOnbord.size() <= 0) {

                ProcessorFailedTransInfo procFailedTrans = new ProcessorFailedTransInfo("check-wallet-no",
                        "Wallet Number does not exist!",
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getPhoneNumber(), "", "General-Ledger-Service", getDecoded.productCode);

                processorFailedTransInfoRepo.save(procFailedTrans);
                responseModel.setDescription("Wallet Number does not exist");
                responseModel.setStatusCode(statusCode);

                return responseModel;

            }

            System.out.println("saveGenLedgersDebitAccountOneTime api req   ::::::::::::::::               ::::: %S  " + new Gson().toJson(rq));
            int res = new BigDecimal(rq.getFinalCHarges()).compareTo(new BigDecimal(rq.getFees()).add(new BigDecimal(rq.getTransAmount())));
            //check if finalCharges = rq.getFees() + rq.getTransAmount();
            if (res != 0) {

                ProcessorFailedTransInfo procFailedTrans = new ProcessorFailedTransInfo("check-wallet-no",
                        "The Final-Charges is not equal to the total amount plus fees",
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getPhoneNumber(), "", "General-Ledger-Service", getDecoded.productCode);

                processorFailedTransInfoRepo.save(procFailedTrans);

                responseModel.setStatusCode(400);
                responseModel.setDescription("The Final-Charges is not equal to the total amount plus fees");
                return responseModel;

            }
            //check account balance
            //get the acct bal
            List<GenLedgAccountCum> genLedCum = _genLedgAccountCumRepo.findByPhnProductCodeDe(getOnbord.get(0).getPhoneNumbProductCode());
            if (genLedCum.size() <= 0) {

                ProcessorFailedTransInfo procFailedTrans = new ProcessorFailedTransInfo("check-wallet-no",
                        "Wallet Number has not funded account!",
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getPhoneNumber(), "", "General-Ledger-Service", getDecoded.productCode);

                processorFailedTransInfoRepo.save(procFailedTrans);
                responseModel.setDescription("Wallet Number has not funded account!");
                responseModel.setStatusCode(statusCode);

                return responseModel;
            }
            List<UserLimitConfig> userLimit = userLimitConfigRepo.findByPhoneNumberProductCode(getOnbord.get(0).getPhoneNumbProductCode());
            List<GlobalLimitConfig> getG = globalLimitConfigRepo.findByLimitCategory(userLimit.get(0).getTierCategory());

            //check if customer has enough bal
            BigDecimal accountBal = genLedCum.get(0).getTotalBalancePhnProCode();
            BigDecimal totalTransAmt = new BigDecimal(rq.getTransAmount()).add(new BigDecimal(rq.getFees()));
            boolean checkFCharges = new BigDecimal(getG.get(0).getMinimumBalance()).compareTo(accountBal.subtract(totalTransAmt)) == 1;
            if (checkFCharges == true) {

                ProcessorFailedTransInfo procFailedTrans = new ProcessorFailedTransInfo("check-wallet-no",
                        "Sorry, minimum account balance must be: N" + getG.get(0).getMinimumBalance(),
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getPhoneNumber(), "", "General-Ledger-Service", getDecoded.productCode);

                processorFailedTransInfoRepo.save(procFailedTrans);

                responseModel.setStatusCode(400);
                responseModel.setDescription("Sorry, minimum account balance must be: N" + getG.get(0).getMinimumBalance());
                return responseModel;

            }
            //check single debit limit

            if (new BigDecimal(getG.get(0).getWithdrawalSingleTransaction()).compareTo(totalTransAmt) == -1) {

                ProcessorFailedTransInfo procFailedTrans = new ProcessorFailedTransInfo("check-wallet-no",
                        "Sorry, your single maximum withdrwal is: N" + getG.get(0).getWithdrawalSingleTransaction(),
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getPhoneNumber(), "", "General-Ledger-Service", getDecoded.productCode);

                processorFailedTransInfoRepo.save(procFailedTrans);

                responseModel.setStatusCode(400);
                responseModel.setDescription("Sorry, your single maximum withdrwal is: N" + getG.get(0).getWithdrawalSingleTransaction());
                return responseModel;

            }
            //check total withdrawal limit

            if (new BigDecimal(getG.get(0).getWithdrawal()).compareTo(totalTransAmt) == -1) {

                ProcessorFailedTransInfo procFailedTrans = new ProcessorFailedTransInfo("check-wallet-no",
                        "Sorry, your maximum withdrwal is: N" + getG.get(0).getWithdrawal(),
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getPhoneNumber(), "", "General-Ledger-Service", getDecoded.productCode);

                processorFailedTransInfoRepo.save(procFailedTrans);

                responseModel.setStatusCode(400);
                responseModel.setDescription("Sorry, your maximum withdrwal is: N" + getG.get(0).getWithdrawal());
                return responseModel;

            }

            //generate transactionId
            String transactionId = String.valueOf(GlobalMethods.generateTransactionId());

            //log client request
            RequestDebitAcctLog rlog = new RequestDebitAcctLog();
            rlog.setDescription(rq.getNarration());
            rlog.setFees(rq.getFees());
            rlog.setFinalCharges(rq.getFinalCHarges());
            rlog.setNarration(rq.getNarration());
            rlog.setPhnNumbProductCode(phoProCode);
            rlog.setPhonenumber(rq.getPhoneNumber());
            rlog.setProductCode(rq.getProductCode());
            rlog.setProductName(getDecoded.productName);
            rlog.setTransactionId(transactionId);
            rlog.setTransRequestId(rq.getTransactionId());
            rlog.setCreatedDate(Instant.now());

            //call the debit method
            ProcLedgerRequestDebitOneTime rqq = new ProcLedgerRequestDebitOneTime();
            rqq.setDescription(rq.getNarration());
            rqq.setFees(rq.getFees());
            rqq.setFinalCharges(rq.getFinalCHarges());
            rqq.setNarration(rq.getNarration());
            rqq.setPhoneNumberProductCode(phoProCode);
            rqq.setPhonenumber(rq.getPhoneNumber());
            rqq.setProductCode(rq.getProductCode());
            rqq.setProductName(getDecoded.productName);
            rqq.setTransactionId(transactionId);

            BaseResponse getDebitRes = genLedgerServices.saveGenLedgersDebitAccountOneTime(rqq);

            rlog.setGenLedResDesc(getDebitRes.getDescription());
            rlog.setGenLedResCode(getDebitRes.getStatusCode());
            if (getDebitRes.getStatusCode() == 200) {
                rlog.setTransStatus("successful");
                rlog.setTransStatusCode(1);

                List<GenLedgAccountCum> genLedCumNew = _genLedgAccountCumRepo.findByPhnProductCodeDe(getOnbord.get(0).getPhoneNumbProductCode());

                BigDecimal accountBalnew = genLedCumNew.get(0).getTotalBalancePhnProCode();
                Map mp = new HashMap();
                String retunBal = accountBalnew.toString();
                mp.put("accountBalance", retunBal);
                responseModel.setData(mp);
                responseModel.setStatusCode(getDebitRes.getStatusCode());
                responseModel.setDescription("Debit transaction request was " + rlog.getTransStatus() + ".");

            } else {
                rlog.setTransStatus("failed");
                rlog.setTransStatusCode(0);

                responseModel.setStatusCode(getDebitRes.getStatusCode());
                responseModel.setDescription("Debit transaction request " + rlog.getTransStatus() + ".");

            }
            requestDebitAcctLogRepo.save(rlog);

        } catch (Exception ex) {
            responseModel.setDescription(statusMessage);
            responseModel.setStatusCode(statusCode);
            ex.printStackTrace();
        }

        return responseModel;

    }
    //credit account
    //processCreditLedgerOneTime

    public BaseResponse processCreditLedgerOneTime(CreditWallet rq, String auth) {

        BaseResponse responseModel = new BaseResponse();
        int statusCode = 500;
        String statusMessage = "An error occured,please try again";

        try {

            statusCode = 400;

            System.out.println("processCreditLedgerOneTime api req   ::::::::::::::::               ::::: %S  " + new Gson().toJson(rq));

            DecodedJWTToken getDecoded = DecodedJWTToken.getDecoded(auth);
            BigDecimal accountBal = BigDecimal.ZERO;

            if (!getDecoded.productCode.equals(rq.getProductCode())) {

                ProcessorFailedTransInfo procFailedTrans = new ProcessorFailedTransInfo("save-GenLedgersDebitAcct",
                        "Invalid product code!",
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getPhoneNumber(), "", "General-Ledger-Service", getDecoded.productCode);

                processorFailedTransInfoRepo.save(procFailedTrans);
                responseModel.setDescription("Invalid product code!");
                responseModel.setStatusCode(statusCode);

                return responseModel;

            }
            if (!"Deposit".equals(rq.getTransType())) {

                ProcessorFailedTransInfo procFailedTrans = new ProcessorFailedTransInfo("save-GenLedgersDebitAcct",
                        "Invalid transaction type!",
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getPhoneNumber(), "", "General-Ledger-Service", getDecoded.productCode);

                processorFailedTransInfoRepo.save(procFailedTrans);
                responseModel.setDescription("Invalid transaction type!");
                responseModel.setStatusCode(statusCode);

                return responseModel;

            }

            //check if wallet exist
            String phoProCode = rq.getPhoneNumber() + rq.getProductCode();

            List<Onboarded> getOnbord = onboardedRepo.findByWalletNoProductCode(rq.getPhoneNumber(), rq.getProductCode());

            if (getOnbord.size() <= 0) {

                ProcessorFailedTransInfo procFailedTrans = new ProcessorFailedTransInfo("check-wallet-no",
                        "Wallet Number does not exist!",
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getPhoneNumber(), "", "General-Ledger-Service", getDecoded.productCode);

                processorFailedTransInfoRepo.save(procFailedTrans);
                responseModel.setDescription("Wallet Number does not exist");
                responseModel.setStatusCode(statusCode);

                return responseModel;

            }

            List<GenLedgAccountCum> genLedCum = _genLedgAccountCumRepo.findByPhnProductCodeDe(getOnbord.get(0).getPhoneNumbProductCode());
            if (genLedCum.size() > 0) {

                /*  BigDecimal bg1, bg2;

      bg1 = new BigDecimal("10");
      bg2 = new BigDecimal("20");

      //create int object
      int res;

      res = bg1.compareTo(bg2); // compare bg1 with bg2

      String str1 = "Both values are equal ";
      String str2 = "First Value is greater ";
      String str3 = "Second value is greater";

      if( res == 0 )
         System.out.println( str1 );
      else if( res == 1 )
         System.out.println( str2 );
      else if( res == -1 )
         System.out.println( str3 );*/
                accountBal = genLedCum.get(0).getTotalBalancePhnProCode();
            }

            //BigDecimal totalTransAmt = new BigDecimal(rq.getTransAmount()).add(new BigDecimal(rq.getFees()));
            List<UserLimitConfig> userLimit = userLimitConfigRepo.findByPhoneNumberProductCode(getOnbord.get(0).getPhoneNumbProductCode());
            List<GlobalLimitConfig> getG = globalLimitConfigRepo.findByLimitCategory(userLimit.get(0).getTierCategory());

            System.out.println("new BigDecimal(rq.getTransAmount()) api req   ::::::::::::::::               ::::: %S  " + new BigDecimal(rq.getTransAmount()));
            System.out.println("new BigDecimal(getG.get(0).getWalletSingleDeposit()) api req   ::::::::::::::::               ::::: %S  " + new BigDecimal(getG.get(0).getWalletSingleDeposit()));
            int resCompare;
            resCompare = new BigDecimal(rq.getTransAmount()).compareTo(new BigDecimal(getG.get(0).getWalletSingleDeposit()));
            System.out.println("resCompare  ::::::::::::::::               ::::: %S  " + resCompare);

            //check single debit limit
            if (resCompare == 1) {

                ProcessorFailedTransInfo procFailedTrans = new ProcessorFailedTransInfo("check-wallet-no",
                        "Sorry, your single maximum deposit is: N" + getG.get(0).getWalletSingleDeposit(),
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getPhoneNumber(), "", "General-Ledger-Service", getDecoded.productCode);

                processorFailedTransInfoRepo.save(procFailedTrans);

                responseModel.setStatusCode(400);
                responseModel.setDescription("Sorry, your single maximum deposit is: N" + getG.get(0).getWalletSingleDeposit());
                return responseModel;

            }

            if (new BigDecimal(rq.getTransAmount()).add(accountBal).compareTo(new BigDecimal(getG.get(0).getMaximumBalance())) == 1) {

                ProcessorFailedTransInfo procFailedTrans = new ProcessorFailedTransInfo("check-wallet-no",
                        "Sorry, your maximum account balance is: N" + getG.get(0).getMaximumBalance(),
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getPhoneNumber(), "", "General-Ledger-Service", getDecoded.productCode);

                processorFailedTransInfoRepo.save(procFailedTrans);

                responseModel.setStatusCode(400);
                responseModel.setDescription("Sorry, your maximum account balance is: N" + getG.get(0).getMaximumBalance());
                return responseModel;

            }

            //generate transactionId
            String transactionId = String.valueOf(GlobalMethods.generateTransactionId());

            ProcLedgerRequestCreditOneTime rqq = new ProcLedgerRequestCreditOneTime();

            rqq.setFundingType(rq.getTransType());
            rqq.setKulFees(new BigDecimal(rq.getFees()));
            rqq.setKulTransactionId(transactionId);
            rqq.setNarration(rq.getNarration());
            rqq.setPhoneNumber(rq.getPhoneNumber());
            rqq.setPhoneNumberProductCode(phoProCode);
            rqq.setProductCode(rq.getProductCode());
            rqq.setProductName(getDecoded.productName);
            rqq.setSwFees(BigDecimal.ZERO);
            rqq.setSwRefrenceNumber(transactionId);
            rqq.setTransAmount(new BigDecimal(rq.getTransAmount()));

            RequestCreditAcctLog creLog = new RequestCreditAcctLog();
            creLog.setFees(rq.getFees());
            creLog.setTransAmount(rq.getFinalCHarges());
            creLog.setNarration(rq.getNarration());
            creLog.setPhnNumbProductCode(phoProCode);
            creLog.setPhoneNumber(rq.getPhoneNumber());
            creLog.setProductCode(rq.getProductCode());
            creLog.setProductName(getDecoded.productName);
            creLog.setTransactionId(transactionId);
            creLog.setTransRequestId(rq.getTransactionId());
            creLog.setCreatedDate(Instant.now());
            creLog.setFundingType(rq.getTransType());
            creLog.setSwRefrenceNumber(transactionId);
            creLog.setSwFees(BigDecimal.ZERO.toString());

            BaseResponse getDebitRes = genLedgerServices.processCreditLedgerOneTime(rqq);

            creLog.setGenLedResDesc(getDebitRes.getDescription());
            creLog.setGenLedResCode(getDebitRes.getStatusCode());
            if (getDebitRes.getStatusCode() == 200) {
                creLog.setTransStatus("successful");
                creLog.setTransStatusCode(1);

                List<GenLedgAccountCum> genLedCumNew = _genLedgAccountCumRepo.findByPhnProductCodeDe(getOnbord.get(0).getPhoneNumbProductCode());

                BigDecimal accountBalnew = genLedCumNew.get(0).getTotalBalancePhnProCode();
                String retunBal = accountBalnew.toString();
                Map mp = new HashMap();
                mp.put("accountBalance", retunBal);
                responseModel.setData(mp);

                responseModel.setStatusCode(getDebitRes.getStatusCode());
                responseModel.setDescription("Credit transaction request was " + creLog.getTransStatus() + ".");

            } else {
                creLog.setTransStatus("failed");
                creLog.setTransStatusCode(0);

                responseModel.setStatusCode(getDebitRes.getStatusCode());
                responseModel.setDescription("Credit transaction request " + creLog.getTransStatus() + ".");

            }
            requestCreditAcctLogRepo.save(creLog);

        } catch (Exception ex) {
            responseModel.setDescription(statusMessage);
            responseModel.setStatusCode(statusCode);
            ex.printStackTrace();
        }

        return responseModel;
    }

    //get wallet info: account balance, tier, 
    public BaseResponse getAccountBalance(WalletInfo rq, String auth) {

        BaseResponse responseModel = new BaseResponse();
        int statusCode = 500;
        String statusMessage = "An error occured,please try again";

        try {

            statusCode = 400;

            DecodedJWTToken getDecoded = DecodedJWTToken.getDecoded(auth);

            if (!getDecoded.productCode.equals(rq.getProductCode())) {

                ProcessorFailedTransInfo procFailedTrans = new ProcessorFailedTransInfo("save-GenLedgersDebitAcct",
                        "Invalid product code!",
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getPhoneNumber(), "", "General-Ledger-Service", getDecoded.productCode);

                processorFailedTransInfoRepo.save(procFailedTrans);
                responseModel.setDescription("Invalid product code!");
                responseModel.setStatusCode(statusCode);

                return responseModel;
            }

            List<Onboarded> getOnbord = onboardedRepo.findByWalletNoProductCode(rq.getPhoneNumber(), rq.getProductCode());
            if (getOnbord == null || getOnbord.isEmpty()) {

                ProcessorFailedTransInfo procFailedTrans = new ProcessorFailedTransInfo("check-wallet-no",
                        "Wallet Number does not exist!",
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getPhoneNumber(), "", "General-Ledger-Service", getDecoded.productCode);

                processorFailedTransInfoRepo.save(procFailedTrans);
                responseModel.setDescription("Wallet Number does not exist");
                responseModel.setStatusCode(statusCode);

                return responseModel;

            }

            String phoneProductCode = getOnbord.get(0).getPhoneNumbProductCode();
            if (phoneProductCode == null || phoneProductCode.trim().isEmpty()) {
                phoneProductCode = rq.getPhoneNumber() + rq.getProductCode();
            }

            List<GenLedgAccountCum> genLedCum = _genLedgAccountCumRepo.findByPhnProductCodeDe(phoneProductCode);
            if (genLedCum == null || genLedCum.isEmpty()) {
                responseModel.addData("accountBalance", BigDecimal.ZERO);
                responseModel.addData("phoneNumber", rq.getPhoneNumber());
                responseModel.addData("productCode", getDecoded.productCode);
                responseModel.setDescription("Wallet Number exists, but account not yet funded.");
                responseModel.setStatusCode(200);

                return responseModel;
            }

            BigDecimal accountBal = genLedCum.get(0).getTotalBalancePhnProCode();

            responseModel.addData("accountBalance", accountBal);
            responseModel.addData("phoneNumber", rq.getPhoneNumber());
            responseModel.addData("productCode", getDecoded.productCode);
            responseModel.setDescription("Wallet Number exists.");
            responseModel.setStatusCode(200);
            System.out.println("getAccountBalance responseModel   ::::::::::::::::               ::::: %S  " + new Gson().toJson(responseModel));

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

            DecodedJWTToken getDecoded = DecodedJWTToken.getDecoded(auth);

            if (!getDecoded.productCode.equals(rq.getProductCode())) {

                ProcessorFailedTransInfo procFailedTrans = new ProcessorFailedTransInfo("save-GenLedgersDebitAcct",
                        "Invalid product code!",
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getPhoneNumber(), "", "General-Ledger-Service", getDecoded.productCode);

                processorFailedTransInfoRepo.save(procFailedTrans);
                responseModel.setDescription("Invalid product code!");
                responseModel.setStatusCode(statusCode);

                return responseModel;
            }

            List<Onboarded> getOnbord = onboardedRepo.findByWalletNoProductCode(rq.getPhoneNumber(), rq.getProductCode());

            if (getOnbord.size() <= 0) {

                ProcessorFailedTransInfo procFailedTrans = new ProcessorFailedTransInfo("check-wallet-no",
                        "Wallet Number does not exist!",
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getPhoneNumber(), "", "General-Ledger-Service", getDecoded.productCode);

                processorFailedTransInfoRepo.save(procFailedTrans);
                responseModel.setDescription("Wallet Number does not exist");
                responseModel.setStatusCode(statusCode);

                return responseModel;

            }

            /* List<GenLedgAccountCum> genLedCum = _genLedgAccountCumRepo.findByPhnProductCodeDe(getOnbord.get(0).getPhoneNumbProductCode());
            if (genLedCum.size() <= 0) {

                ProcessorFailedTransInfo procFailedTrans = new ProcessorFailedTransInfo("check-wallet-no",
                        "Wallet Number has not funded account!",
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getPhoneNumber(), "", "General-Ledger-Service", getDecoded.productCode);

                processorFailedTransInfoRepo.save(procFailedTrans);
                responseModel.setDescription("Wallet Number has not funded account!");
                responseModel.setStatusCode(statusCode);

                return responseModel;
            }*/
            List<UserLimitConfig> userLimit = userLimitConfigRepo.findByPhoneNumberProductCode(getOnbord.get(0).getPhoneNumbProductCode());
            List<GlobalLimitConfig> getG = globalLimitConfigRepo.findByLimitCategory(userLimit.get(0).getTierCategory());

            //check single debit limit
            responseModel.addData("maxSingleDeposit", new BigDecimal(getG.get(0).getWalletSingleDeposit()));
            responseModel.addData("phoneNumber", rq.getPhoneNumber());
            responseModel.addData("productCode", getDecoded.productCode);
            responseModel.setDescription("Wallet Number exists.");
            responseModel.setStatusCode(200);
            System.out.println("getMaxSingleDeposit responseModel   ::::::::::::::::               ::::: %S  " + new Gson().toJson(responseModel));

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

            DecodedJWTToken getDecoded = DecodedJWTToken.getDecoded(auth);

            if (!getDecoded.productCode.equals(rq.getProductCode())) {

                ProcessorFailedTransInfo procFailedTrans = new ProcessorFailedTransInfo("save-GenLedgersDebitAcct",
                        "Invalid product code!",
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getPhoneNumber(), "", "General-Ledger-Service", getDecoded.productCode);

                processorFailedTransInfoRepo.save(procFailedTrans);
                responseModel.setDescription("Invalid product code!");
                responseModel.setStatusCode(statusCode);

                return responseModel;
            }

            List<Onboarded> getOnbord = onboardedRepo.findByWalletNoProductCode(rq.getPhoneNumber(), rq.getProductCode());

            if (getOnbord.size() <= 0) {

                ProcessorFailedTransInfo procFailedTrans = new ProcessorFailedTransInfo("check-wallet-no",
                        "Wallet Number does not exist!",
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getPhoneNumber(), "", "General-Ledger-Service", getDecoded.productCode);

                processorFailedTransInfoRepo.save(procFailedTrans);
                responseModel.setDescription("Wallet Number does not exist");
                responseModel.setStatusCode(statusCode);

                return responseModel;

            }

            /* List<GenLedgAccountCum> genLedCum = _genLedgAccountCumRepo.findByPhnProductCodeDe(getOnbord.get(0).getPhoneNumbProductCode());
            if (genLedCum.size() <= 0) {

                ProcessorFailedTransInfo procFailedTrans = new ProcessorFailedTransInfo("check-wallet-no",
                        "Wallet Number has not funded account!",
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getPhoneNumber(), "", "General-Ledger-Service", getDecoded.productCode);

                processorFailedTransInfoRepo.save(procFailedTrans);
                responseModel.setDescription("Wallet Number has not funded account!");
                responseModel.setStatusCode(statusCode);

                return responseModel;
            }*/
            List<UserLimitConfig> userLimit = userLimitConfigRepo.findByPhoneNumberProductCode(getOnbord.get(0).getPhoneNumbProductCode());
            List<GlobalLimitConfig> getG = globalLimitConfigRepo.findByLimitCategory(userLimit.get(0).getTierCategory());

            //check single debit limit
            responseModel.addData("maxAcctBalance", new BigDecimal(getG.get(0).getMaximumBalance()));
            responseModel.addData("phoneNumber", rq.getPhoneNumber());
            responseModel.addData("productCode", getDecoded.productCode);
            responseModel.setDescription("Wallet Number exists.");
            responseModel.setStatusCode(200);
            System.out.println("getMaxAcctBalance responseModel   ::::::::::::::::               ::::: %S  " + new Gson().toJson(responseModel));

        } catch (Exception ex) {
            responseModel.setDescription(statusMessage);
            responseModel.setStatusCode(statusCode);
            ex.printStackTrace();
        }

        return responseModel;
    }

}
