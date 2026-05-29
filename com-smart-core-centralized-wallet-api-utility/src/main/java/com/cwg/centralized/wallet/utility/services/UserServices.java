/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwg.centralized.wallet.utility.services;

import com.cwg.centralized.wallet.utility.domain.GlobalLimitConfig;
import com.cwg.centralized.wallet.utility.domain.Onboarded;
import com.cwg.centralized.wallet.utility.domain.ProcessorUserFailedTransInfo;
import com.cwg.centralized.wallet.utility.domain.UserDetails;
import com.cwg.centralized.wallet.utility.domain.UserLimitConfig;
import com.cwg.centralized.wallet.utility.domain.WalletTierVerifyBvn;
import com.cwg.centralized.wallet.utility.identity.domain.BvnIdentityLog;
import com.cwg.centralized.wallet.utility.identity.repo.BvnIdentityLogRepository;
import com.cwg.centralized.wallet.utility.models.AddNewUserToLimit;
import com.cwg.centralized.wallet.utility.models.ApiResponseModel;
import com.cwg.centralized.wallet.utility.models.AuthUserRequest;
import com.cwg.centralized.wallet.utility.models.BaseResponse;
import com.cwg.centralized.wallet.utility.models.BvnIdentity;
import com.cwg.centralized.wallet.utility.models.BvnRequest;
import com.cwg.centralized.wallet.utility.models.BvnResponseModel;
import com.cwg.centralized.wallet.utility.models.CheckUserLimit;
import com.cwg.centralized.wallet.utility.models.GetBvnDetailRp;
import com.cwg.centralized.wallet.utility.models.InitiateValidateBvnForTier2;
import com.cwg.centralized.wallet.utility.models.OtpRequest;
import com.cwg.centralized.wallet.utility.models.OtpValidateRequest;
import com.cwg.centralized.wallet.utility.models.UpgradeUserToLimit;
import com.cwg.centralized.wallet.utility.models.ValidateBvnForTier2;
import com.cwg.centralized.wallet.utility.repo.GlobalLimitConfigRepo;
import com.cwg.centralized.wallet.utility.repo.OnboardedRepo;
import com.cwg.centralized.wallet.utility.repo.ProcessorUserFailedTransInfoRepo;
import com.cwg.centralized.wallet.utility.repo.UserDetailsRepo;
import com.cwg.centralized.wallet.utility.repo.UserLimitConfigRepo;
import com.cwg.centralized.wallet.utility.repo.WalletTierVerifyBvnRepo;
import com.cwg.centralized.wallet.utility.util.DecodedJWTToken;
import com.cwg.centralized.wallet.utility.util.GlobalMethods;
import com.cwg.centralized.wallet.utility.util.UttilityMethods;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 *
 * @author SmartCore Contributors
 */
@Service
@Transactional
public class UserServices {

    private final GlobalLimitConfigRepo globalLimitConfigRepo;
    private final ProcessorUserFailedTransInfoRepo procFailedRepo;
    GlobalLimitConfig gLimitResult = new GlobalLimitConfig();
    private final UserLimitConfigRepo userLimitConfigRepo;
    private final OnboardedRepo onboardedRepo;
    private final UttilityMethods utilMeth;
    @Value("${spring.profiles.active}")
    private String environment;
    private final BvnIdentityLogRepository bvnIdentityLogRepository;
    private final IdentityPassClientService identityPassClientService;
    private final UserDetailsRepo userDeRepo;
    @Value("${gen.otp.encrypt.key}")
    private String encryptionKey;
    private static final String LOGIN_SUCCESSFUL = "Login Successful";

    OtpService otpService;

    private final Logger logger = LoggerFactory.getLogger(UserServices.class);

    private final WalletTierVerifyBvnRepo walletTierVerifyBvnRepo;

    UserDetails userDetailsResult = new UserDetails();

    public UserServices(GlobalLimitConfigRepo globalLimitConfigRepo,
            ProcessorUserFailedTransInfoRepo procFailedRepo,
            UserLimitConfigRepo userLimitConfigRepo,
            OnboardedRepo onboardedRepo,
            UttilityMethods utilMeth,
            WalletTierVerifyBvnRepo walletTierVerifyBvnRepo,
            BvnIdentityLogRepository bvnIdentityLogRepository,
            IdentityPassClientService identityPassClientService,
            UserDetailsRepo userDeRepo) {
        this.globalLimitConfigRepo = globalLimitConfigRepo;
        this.procFailedRepo = procFailedRepo;
        this.userLimitConfigRepo = userLimitConfigRepo;
        this.onboardedRepo = onboardedRepo;
        this.utilMeth = utilMeth;
        this.walletTierVerifyBvnRepo = walletTierVerifyBvnRepo;
        this.bvnIdentityLogRepository = bvnIdentityLogRepository;
        this.identityPassClientService = identityPassClientService;
        this.userDeRepo = userDeRepo;

    }

    public BaseResponse addTierToWallet(AddNewUserToLimit rq) {
        BaseResponse responseModel = new BaseResponse();
        int statusCode = 500;
        String statusMessage = "An error occured,please try again";
        try {
            statusCode = 400;
            UserLimitConfig userLimit = new UserLimitConfig();

            System.out.println("addTierToWallet" + "  ::::::::::::::::::::: " + rq);

            if (!rq.getPhoneNumberProductCode().equals(rq.getWalletNumber() + rq.getProductCode())) {

                ProcessorUserFailedTransInfo procFailedTrans = new ProcessorUserFailedTransInfo("add-user-tier",
                        "Plaese check Product-Code and Wallet-Number entries!",
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getWalletNumber(), "", "Utilities-Service");

                procFailedRepo.save(procFailedTrans);
                responseModel.setDescription("Plaese check Product-Code and Wallet-Number entries!");
                responseModel.setStatusCode(statusCode);

                return responseModel;

            }

            System.out.println("addTierToWallet" + "  ::::::::::::::::::::: " + "GOT HERE");

            List<Onboarded> getOnbord = onboardedRepo.findByWalletNoProductCode(rq.getWalletNumber(), rq.getProductCode());

            if (getOnbord.size() <= 0) {

                ProcessorUserFailedTransInfo procFailedTrans = new ProcessorUserFailedTransInfo("add-user-tier",
                        "Product-Code and Wallet-Number mismatch!",
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getWalletNumber(), "", "Utilities-Service");

                procFailedRepo.save(procFailedTrans);
                responseModel.setDescription("Product-Code and Wallet-Number mismatch!");
                responseModel.setStatusCode(statusCode);

                return responseModel;

            }
            
              System.out.println(" List<Onboarded> getOnbord" + "  ::::::::::::::::::::: " + "GOT HERE");


            List<GlobalLimitConfig> glocalConfig = globalLimitConfigRepo.findByLimitCategoryProductCode(rq.getCategory());

            if (glocalConfig.size() <= 0) {

                ProcessorUserFailedTransInfo procFailedTrans = new ProcessorUserFailedTransInfo(
                        "add-user-tier", "Add User Tier, Limit Category does not exist!",
                        String.valueOf(GlobalMethods.generateTransactionId()), "", "", "Utilities-Service"
                );

                responseModel.setDescription("Add User Tier, Limit Category does not exist!");
                responseModel.setStatusCode(statusCode);

                procFailedRepo.save(procFailedTrans);
                return responseModel;

            }

            gLimitResult = glocalConfig.get(0);

            List<UserLimitConfig> getUserConfig = userLimitConfigRepo.findByPhoneNumberProductCode(rq.getPhoneNumberProductCode());

            if (getUserConfig.size() > 0) {

                UserLimitConfig getUserConfigUp = userLimitConfigRepo.findByPhoneNumberProductCodeQuery(rq.getPhoneNumberProductCode());
                getUserConfigUp.setLastModifiedDate(Instant.now());
                userLimitConfigRepo.save(getUserConfigUp);

                ProcessorUserFailedTransInfo procFailedTrans = new ProcessorUserFailedTransInfo(
                        "add-user-tier", "Add User Tier, UserLimit already exist!",
                        String.valueOf(GlobalMethods.generateTransactionId()), "", "", "Utilities-Service"
                );

                responseModel.setDescription("Add User Tier, UserLimit already exist!");
                responseModel.setStatusCode(statusCode);
                responseModel.setStatusCode(200);

                procFailedRepo.save(procFailedTrans);
                return responseModel;

            }

            userLimit.setTierCategory(gLimitResult.getCategory());
            userLimit.setLastModifiedDate(Instant.now());
            userLimit.setCreatedDate(Instant.now());
            userLimit.setPhoneNumber(rq.getWalletNumber());
            userLimit.setPhoneNumberProductCode(rq.getPhoneNumberProductCode());
            userLimit.setProductCode(rq.getProductCode());
            userLimit.setProductName(rq.getProductName());
            //  userLimit.setTierCategory(rq.getCategory());

            userLimitConfigRepo.save(userLimit);

            responseModel.setStatusCode(HttpServletResponse.SC_OK);
            responseModel.setDescription("User limit Category created successfully.");

        } catch (Exception ex) {
            responseModel.setDescription(statusMessage);
            responseModel.setStatusCode(statusCode);

            ex.printStackTrace();
        }

        return responseModel;
    }

    public BaseResponse upGradeWalletTier(UpgradeUserToLimit rq) {
        BaseResponse responseModel = new BaseResponse();
        int statusCode = 500;
        String statusMessage = "An error occured,please try again";
        try {
            statusCode = 400;

            if (!rq.getPhoneNumberProductCode().equals(rq.getWalletNumber() + rq.getProductCode())) {

                ProcessorUserFailedTransInfo procFailedTrans = new ProcessorUserFailedTransInfo("upgrade-user-tier",
                        "Plaese check Product-Code and Wallet-Number enties!",
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getWalletNumber(), "", "Utilities-Service");

                procFailedRepo.save(procFailedTrans);
                responseModel.setDescription("Plaese check Product-Code and Wallet-Number enties!");
                responseModel.setStatusCode(statusCode);

                return responseModel;

            }

            List<Onboarded> getOnbord = onboardedRepo.findByWalletNoProductCode(rq.getWalletNumber(), rq.getProductCode());

            if (getOnbord.size() <= 0) {

                ProcessorUserFailedTransInfo procFailedTrans = new ProcessorUserFailedTransInfo("upgrade-user-tier",
                        "Product-Code and Wallet-Number mismatch!",
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getWalletNumber(), "", "Utilities-Service");

                procFailedRepo.save(procFailedTrans);
                responseModel.setDescription("Product-Code and Wallet-Number mismatch!");
                responseModel.setStatusCode(statusCode);

                return responseModel;

            }

            List<GlobalLimitConfig> glocalConfig = globalLimitConfigRepo.findByLimitCategoryProductCode(rq.getCategory());

            if (glocalConfig.size() <= 0) {

                ProcessorUserFailedTransInfo procFailedTrans = new ProcessorUserFailedTransInfo(
                        "add-user-tier", "Update User Tier, Limit Category does not exist!",
                        String.valueOf(GlobalMethods.generateTransactionId()), "", "", "Utilities-Service"
                );

                responseModel.setDescription("Update User Tier, Limit Category does not exist!");
                responseModel.setStatusCode(statusCode);

                procFailedRepo.save(procFailedTrans);
                return responseModel;

            }

            gLimitResult = glocalConfig.get(0);

            List<UserLimitConfig> getUserConfig = userLimitConfigRepo.findByPhoneNumberProductCode(rq.getPhoneNumberProductCode());

            if (getUserConfig.size() <= 0) {

                ProcessorUserFailedTransInfo procFailedTrans = new ProcessorUserFailedTransInfo(
                        "add-user-tier", "Update User Tier, Wallet Number does not exist!",
                        String.valueOf(GlobalMethods.generateTransactionId()), "", "", "Utilities-Service"
                );

                responseModel.setDescription("Update User Tier, Wallet Number does not exist!");
                responseModel.setStatusCode(statusCode);

                procFailedRepo.save(procFailedTrans);
                return responseModel;

            }

            //check if user has completed bvn validation
            if (getUserConfig.get(0).getTierCategory().equals(rq.getCategory()) && !getUserConfig.get(0).getTierCategory().equals(utilMeth.getTier1())) {

                ProcessorUserFailedTransInfo procFailedTrans = new ProcessorUserFailedTransInfo(
                        "add-user-tier", "Update User Tier, UserLimit already exist!",
                        String.valueOf(GlobalMethods.generateTransactionId()), "", "", "Utilities-Service"
                );

                responseModel.setDescription("Update User Tier, UserLimit already exist!");
                responseModel.setStatusCode(statusCode);

                procFailedRepo.save(procFailedTrans);
                return responseModel;

            }

            List<WalletTierVerifyBvn> getBvnVal = walletTierVerifyBvnRepo.findByPhoneNumberProductCode(rq.getPhoneNumberProductCode());
            if (!getBvnVal.isEmpty()) {

                if (getBvnVal.get(0).getBvnVerificationStatus() != 3) {
                    ProcessorUserFailedTransInfo procFailedTrans = new ProcessorUserFailedTransInfo("upgrade-user-tier",
                            "User has not done BVN Validation, Thank you!",
                            String.valueOf(GlobalMethods.generateTransactionId()), rq.getWalletNumber(), "", "Utilities-Service");

                    procFailedRepo.save(procFailedTrans);

                    responseModel.setDescription("User has not done BVN Validation, Thank you!");
                    responseModel.setStatusCode(statusCode);
                    //responseModel.addData("bvnNumber", getBvnVal.get(0).getBvn());
                    responseModel.addData("requestId", getBvnVal.get(0).getRequestId());
                    responseModel.addData("phoneNumber", getBvnVal.get(0).getBvnPhoneNumber());
                    return responseModel;

                }
            }

            UserLimitConfig userLimit = userLimitConfigRepo.findByPhoneNumberProductCodeQuery(getUserConfig.get(0).getPhoneNumberProductCode());
            userLimit.setTierCategory(gLimitResult.getCategory());
            userLimit.setLastModifiedDate(Instant.now());

            userLimitConfigRepo.save(userLimit);

            responseModel.setDescription("Update User Tier, UserLimit set successfully.");
            responseModel.setStatusCode(200);

        } catch (Exception ex) {
            responseModel.setDescription(statusMessage);
            responseModel.setStatusCode(statusCode);

            ex.printStackTrace();
        }

        return responseModel;

    }

    public BaseResponse getMaxAcctBal(CheckUserLimit req) {
        BaseResponse response = new BaseResponse();
        int statusCode = 500;
        String statusMessage = "An error occured,please try again";
        try {

            List<UserLimitConfig> userLimit = userLimitConfigRepo.findByPhoneNumberProductCode(req.getPhoneNumberProductCode());
            List<GlobalLimitConfig> getG = globalLimitConfigRepo.findByLimitCategoryProductCode(userLimit.get(0).getTierCategory());

            response.setStatusCode(200);
            response.addData("userMaxAcctBalLimit", getG.get(0).getMaximumBalance());
            response.setDescription("Request for service was sucessful, Thank you.");

            return response;

        } catch (Exception ex) {
            ex.printStackTrace();
            response.setStatusCode(statusCode);
            response.setDescription(statusMessage);
        }

        return response;

    }

    public BaseResponse initiateValidateBvnForTier2(InitiateValidateBvnForTier2 rq, String channel, String auth) {
        //  log.info("initiateValidateBvnForTier2 request Payload: {}", rq.toString());
        BaseResponse responseModel = new BaseResponse();
        int statusCode = 500;
        String statusMessage = "An error occurred, please try again";
        try {
            statusCode = 400;
            DecodedJWTToken getDecoded = DecodedJWTToken.getDecoded(auth);

            String walletNo = rq.getWalletNumber();

            if (!rq.getPhoneNumberProductCode().equals(rq.getWalletNumber() + rq.getProductCode())) {

                ProcessorUserFailedTransInfo procFailedTrans = new ProcessorUserFailedTransInfo("add-user-tier",
                        "Plaese check Product-Code and Wallet-Number enties!",
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getWalletNumber(), "", "Utilities-Service");

                procFailedRepo.save(procFailedTrans);
                responseModel.setDescription("Plaese check Product-Code and Wallet-Number enties!");
                responseModel.setStatusCode(statusCode);

                return responseModel;

            }

            List<Onboarded> getOnbord = onboardedRepo.findByWalletNoProductCode(rq.getWalletNumber(), rq.getProductCode());

            if (getOnbord.size() <= 0) {

                ProcessorUserFailedTransInfo procFailedTrans = new ProcessorUserFailedTransInfo("add-user-tier",
                        "Product-Code and Wallet-Number mismatch!",
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getWalletNumber(), "", "Utilities-Service");

                procFailedRepo.save(procFailedTrans);
                responseModel.setDescription("Product-Code and Wallet-Number mismatch!");
                responseModel.setStatusCode(statusCode);

                return responseModel;

            }

            if (!utilMeth.isNumeric(rq.getNumber())) {

                ProcessorUserFailedTransInfo procFailedTrans = new ProcessorUserFailedTransInfo("upgrade-user-tier",
                        "The BVN provided is not valid!",
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getWalletNumber(), "", "Utilities-Service");

                procFailedRepo.save(procFailedTrans);
                responseModel.setDescription("The BVN-Number is not valid!");
                responseModel.setStatusCode(statusCode);
                return responseModel;

            }

            if (!utilMeth.isValid11Num(rq.getNumber())) {

                ProcessorUserFailedTransInfo procFailedTrans = new ProcessorUserFailedTransInfo("upgrade-user-tier",
                        "The BVN is not valid, kindly check number of digits!",
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getWalletNumber(), "", "Utilities-Service");

                procFailedRepo.save(procFailedTrans);

                responseModel.setDescription("The BVN provided is not valid, kindly check number of digits!");
                responseModel.setStatusCode(statusCode);
                return responseModel;

            }

            List<WalletTierVerifyBvn> getBvnVal = walletTierVerifyBvnRepo.findByPhoneNumberProductCode(rq.getPhoneNumberProductCode());
            if (!getBvnVal.isEmpty()) {

                if (getBvnVal.get(0).getBvnVerificationStatus() == 3) {
                    ProcessorUserFailedTransInfo procFailedTrans = new ProcessorUserFailedTransInfo("upgrade-user-tier",
                            "Bvn already validated, Thank you!",
                            String.valueOf(GlobalMethods.generateTransactionId()), rq.getWalletNumber(), "", "Utilities-Service");

                    procFailedRepo.save(procFailedTrans);

                    responseModel.setDescription("Bvn already validated, Thank you!");
                    responseModel.setStatusCode(statusCode);
                    //responseModel.addData("bvnNumber", getBvnVal.get(0).getBvn());
                    responseModel.addData("requestId", getBvnVal.get(0).getRequestId());
                    responseModel.addData("phoneNumber", getBvnVal.get(0).getBvnPhoneNumber());
                    return responseModel;

                }
            }

            //create BVN record
            WalletTierVerifyBvn walletTierBvnRecord = new WalletTierVerifyBvn();
            String getBvnTrimmed = rq.getNumber().trim();
            ApiResponseModel valBVNRes = getBvnDetail(getBvnTrimmed, rq.getDob(), rq.getPhoneNumberProductCode());
            if (valBVNRes.getStatusCode() != 200) {
                ProcessorUserFailedTransInfo procFailedTrans = new ProcessorUserFailedTransInfo("upgrade-user-tier",
                        valBVNRes.getDescription(),
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getWalletNumber(), "", "Utilities-Service");

                procFailedRepo.save(procFailedTrans);

                responseModel.setDescription(valBVNRes.getDescription());
                responseModel.setStatusCode(valBVNRes.getStatusCode());
                return responseModel;
            }

            // BvnObjects getObjs = (BvnObjects) valBVNRes.getData();
            String dataStr = new Gson().toJson(valBVNRes.getData());
            JsonObject trRp = new Gson().fromJson(dataStr, JsonObject.class);
            String bvnPhoneNumb = trRp.get("phoneNumber").getAsString();
            String firstName = trRp.get("firstName").getAsString();
            String lastName = trRp.get("lastName").getAsString();

            OtpRequest otp = new OtpRequest();
            otp.setPhoneNumber(bvnPhoneNumb);
            otp.setUserId(bvnPhoneNumb);
            otp.setServiceName("Upgrade-To-Tier2-Profiling-Service");
            otp.setEmailAddress(getDecoded.email);

            BaseResponse bRes = otpService.createAndSendOtpSMSOnly(otp);
            if (bRes.getStatusCode() != 200) {

                ProcessorUserFailedTransInfo procFailedTrans = new ProcessorUserFailedTransInfo("upgrade-user-tier",
                        "Register Wallet, " + responseModel.getDescription(),
                        String.valueOf(GlobalMethods.generateTransactionId()), rq.getWalletNumber(), "", "Utilities-Service");

                procFailedRepo.save(procFailedTrans);

                responseModel.setDescription("Register Wallet, " + responseModel.getDescription());
                responseModel.setStatusCode(400);
                return responseModel;
            }
            String rqId = (String) bRes.getData().get("requestId");

            // responseModel.addData("bvnNumber", getBvnTrimmed);
            responseModel.addData("requestId", rqId);
            responseModel.addData("phoneNumber", bvnPhoneNumb);
            BvnIdentityLog fBvn = bvnIdentityLogRepository.findByBvn(getBvnTrimmed);
            BvnIdentityLog updateBvnLog = bvnIdentityLogRepository.findByBvnLogId(fBvn.getLogId());
            //BvnNumberLog updateBvnLog = bvnNumberLogRepo.findByBvnLogId(fBvn.getId());
            updateBvnLog.setRequestId(rqId);
            updateBvnLog.setWalletNo(walletNo);
            bvnIdentityLogRepository.save(updateBvnLog);

            walletTierBvnRecord.setWalletNo(walletNo);
            walletTierBvnRecord.setBvn(getBvnTrimmed);
            walletTierBvnRecord.setRequestId(rqId);
            walletTierBvnRecord.setBvnPhoneNumber(bvnPhoneNumb);
            walletTierBvnRecord.setFirstName(firstName);
            walletTierBvnRecord.setLastName(lastName);
            walletTierBvnRecord.setPhoneNumberProductCode(rq.getPhoneNumberProductCode());
            walletTierBvnRecord.setCreatedDate(Instant.now());
            try {
                walletTierVerifyBvnRepo.save(walletTierBvnRecord);
            } catch (Exception e) {
                // log.warn("Warning persisting BVN: {} for wallet_no:{}, reason: {}", getBvnTrimmed, walletNo, e.getMessage());
            }

            responseModel.setStatusCode(200);
            responseModel.setDescription("BVN validation, Otp sent to Customer's Phonenumber.");

            return responseModel;

        } catch (JsonSyntaxException | UnsupportedEncodingException ex) {
            responseModel.setDescription(statusMessage);
            responseModel.setStatusCode(statusCode);
            ex.printStackTrace();
        }

        return responseModel;
    }

    public ApiResponseModel getBvnDetail(String bvn, String dateOfBirth, String phoProdCode) {
        ApiResponseModel responseModel = new ApiResponseModel();
        int statusCode = 500;
        String statusMessage = "An error occurred, please try again";
        try {

            BvnIdentityLog fBvn = bvnIdentityLogRepository.findByBvn(bvn);
            //List<BvnNumberLog> fBvn = bvnNumberLogRepo.findByBvn(bvn);

            if (fBvn != null) {
                BvnResponseModel bvnRes = new BvnResponseModel();
                bvnRes.setBvn(fBvn.getBvn());
                bvnRes.setBase64Image(fBvn.getBase64Image());
                bvnRes.setEmail(fBvn.getEmail());
                bvnRes.setPhoneNumber1(fBvn.getPhoneNumber1());
                bvnRes.setFirstName(fBvn.getFirstName());
                bvnRes.setLastName(fBvn.getLastName());
                GetBvnDetailRp getBvnDetailRp = new GetBvnDetailRp(bvnRes);
                statusCode = 200;
                statusMessage = "BVN Validated successfully.";
                responseModel.setData(getBvnDetailRp);
                responseModel.setStatusCode(statusCode);
                responseModel.setDescription(statusMessage);
                return responseModel;
            }

            BvnResponseModel bvnResponseModel = validateBvn(bvn, dateOfBirth);

            if (bvnResponseModel == null) {
                logger.info("Error invoking IdentityPass endpoint...");
                responseModel.setStatusCode(400);
                responseModel.setDescription("BVN Validation failed!");
                return responseModel;
            }

            boolean dobMatchBvnBirthDate = compareDatesOfBirth(dateOfBirth, bvnResponseModel.getDateOfBirth());
            /*
             boolean phoneNumberMatchBvnPhoneNumbers = comparePhoneNumbers(phoneNumber,
             bvnResponseModel.getPhoneNumber1(), bvnResponseModel.getPhoneNumber2());
             */
            if (dobMatchBvnBirthDate) {
                GetBvnDetailRp getBvnDetailRp = new GetBvnDetailRp(bvnResponseModel);
                BvnIdentityLog bvnLog = new BvnIdentityLog();
                bvnLog.setBase64Image(getBvnDetailRp.getBase64Image());
                bvnLog.setBvn(bvn);
                bvnLog.setCreatedDate(Instant.now());
                bvnLog.setDateOfBirth(getBvnDetailRp.getDateOfBirth());
                bvnLog.setEmail(getBvnDetailRp.getEmail());
                bvnLog.setFirstName(getBvnDetailRp.getFirstName());
                bvnLog.setGender(getBvnDetailRp.getGender());
                bvnLog.setLastModifiedDate(Instant.now());
                bvnLog.setLastName(getBvnDetailRp.getLastName());
                bvnLog.setLgaOfOrigin(getBvnDetailRp.getLgaOfOrigin());
                bvnLog.setLgaOfResidence(getBvnDetailRp.getLgaOfResidence());
                bvnLog.setMaritalStatus(getBvnDetailRp.getMaritalStatus());
                bvnLog.setMiddleName(getBvnDetailRp.getMiddleName());
                bvnLog.setNationality(getBvnDetailRp.getNationality());
                bvnLog.setPhoneNumber1(getBvnDetailRp.getPhoneNumber());
                bvnLog.setPhoneNumber2(getBvnDetailRp.getPhoneNumber2());
                bvnLog.setRegistrationDate(getBvnDetailRp.getRegistrationDate());
                bvnLog.setResidentialAddress(getBvnDetailRp.getResidentialAddress());
                bvnLog.setStateOfOrigin(getBvnDetailRp.getStateOfResidence());
                bvnLog.setStateOfOrigin(getBvnDetailRp.getStateOfOrigin());
                bvnLog.setPhoneNumberProductCode(phoProdCode);
                bvnIdentityLogRepository.save(bvnLog);

                statusCode = 200;
                statusMessage = "BVN Validated successfully.";
                responseModel.setData(getBvnDetailRp);
            } else {
                statusCode = 400;
                statusMessage = "BVN invalid!";
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        responseModel.setStatusCode(statusCode);
        responseModel.setDescription(statusMessage);
        return responseModel;
    }

    public BvnResponseModel validateBvn(String bvn, String dateOfBirth) {
        try {

            //gen email
            /*
             String email = firstName + "@" + "example.com"; if ((activeProfile.equals("staging")) ||
             (activeProfile.equals("dev")) || (activeProfile.equals("local"))) { return new BvnResponseModel(true, bvn,
             email, phoneGen, firstName, lastname, phoneGen2, "", "", "", "", null); }
             */
            BvnRequest bvnRequest = new BvnRequest();
            bvnRequest.setNumber(bvn);
            bvnRequest.setDob(dateOfBirth);

            ResponseEntity<?> response = identityPassClientService.identityVerificationBvnNoImage(bvnRequest);

            BvnIdentity bvnIdentity = null;
            if (response != null && response.hasBody()) {
                logger.info("BvnIdentity Httpstatus code: {}", response.getStatusCode().value());
                if (response.getBody() instanceof BvnIdentity) {
                    bvnIdentity = (BvnIdentity) response.getBody();
                    //         logger.info("BvnIdentity response: {}", bvnIdentity.isStatus());
                }
            }

            logger.info("BvnIdentity >>>>>>> => {}", bvnIdentity);
            BvnResponseModel idPassBvnIdentity = null;
            if (bvnIdentity != null && bvnIdentity.getDetail() != null
                    && bvnIdentity.getDetail().equalsIgnoreCase("Verification Successfull")) {
                //logger.info("bvnIdentity.isStatus()            >>>>>>> => {}", bvnIdentity.isStatus());
                BvnIdentity.BvnData bvnData = bvnIdentity.getBvn_data();
                idPassBvnIdentity = mapIdentityPassBvnResponseModel(bvnData);
                return idPassBvnIdentity;
            } else {
                return null;
            }

            //logger.info("return BVN  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  => ", idPassBvnIdentity);
        } catch (JsonSyntaxException ex) {
            ex.getMessage();
        }

        return null;
    }

    private boolean compareDatesOfBirth(String frontEndDateString, String bvnDateString) {
        boolean dobsMatch = false;
        String frontendDateFormat = "yyyy-MM-dd"; //1970-11-02
        String bvnDateFormat = "dd-MMM-yyyy";     //02-Nov-1970

        try {
            DateFormat dateFormatter = new SimpleDateFormat(frontendDateFormat);
            Date feDate = dateFormatter.parse(frontEndDateString);

            dateFormatter = new SimpleDateFormat(bvnDateFormat);
            Date bvnDate = dateFormatter.parse(bvnDateString);

            if (StringUtils.isEmpty(frontEndDateString) || StringUtils.isEmpty(bvnDateString)) {
                return dobsMatch;
            } else {
                return (feDate.compareTo(bvnDate) == 0);
            }

        } catch (ParseException e) {
            logger.error("Unable to Parse Dates of Birth: " + frontEndDateString + ", and " + bvnDateString, e);
            return dobsMatch;
        }
    }

    private BvnResponseModel mapIdentityPassBvnResponseModel(BvnIdentity.BvnData bvnData) {
        BvnResponseModel responseModel = new BvnResponseModel();
        responseModel.setBvn(bvnData.getBvn());

        responseModel.setNameOnCard(bvnData.getNameOnCard());
        responseModel.setEnrollmentBank(bvnData.getEnrollmentBank());
        responseModel.setEnrollmentBranch(bvnData.getEnrollmentBranch());
        responseModel.setLevelOfAccount(bvnData.getLevelOfAccount());
        responseModel.setNin(bvnData.getNin());
        responseModel.setRegistrationDate(bvnData.getRegistrationDate());
        responseModel.setWatchListed(bvnData.getWatchListed());

        responseModel.setGender(bvnData.getGender());
        responseModel.setFirstName(bvnData.getFirstName());
        responseModel.setLastName(bvnData.getLastName());
        responseModel.setMiddleName(bvnData.getMiddleName());
        responseModel.setDateOfBirth(bvnData.getDateOfBirth());
        responseModel.setNationality(bvnData.getNationality());
        responseModel.setLgaOfOrigin(bvnData.getLgaOfOrigin());
        responseModel.setEmail(bvnData.getEmail());
        responseModel.setMaritalStatus(bvnData.getMaritalStatus());
        responseModel.setStateOfOrigin(bvnData.getStateOfOrigin());
        responseModel.setPhoneNumber1(bvnData.getPhoneNumber1());
        responseModel.setPhoneNumber2(bvnData.getPhoneNumber2());
        responseModel.setBase64Image(bvnData.getBase64Image());

        responseModel.setLgaOfResidence(bvnData.getLgaOfOrigin());
        responseModel.setStateOfResidence(bvnData.getLgaOfResidence());
        responseModel.setResidentialAddress(bvnData.getResidentialAddress());

        return responseModel;
    }

    public BaseResponse validateBvnForTier2(ValidateBvnForTier2 rq, String channel, String auth) {
        BaseResponse responseModel = new BaseResponse();
        int statusCode = 500;
        String statusMessage = "An error occurred, please try again";
        try {
            statusCode = 400;
            DecodedJWTToken getDecoded = DecodedJWTToken.getDecoded(auth);
            if (!utilMeth.getIfChannelExist(channel)) {
                ProcessorUserFailedTransInfo procFailedTrans = new ProcessorUserFailedTransInfo("validateBvnForTier2",
                        "Upgrade to Tier2, channel type does not exist!",
                        String.valueOf(GlobalMethods.generateTransactionId()), "", channel, "Profiling-Service");

                responseModel.setDescription("Upgrade to Tier2, channel type does not exist!");
                responseModel.setStatusCode(statusCode);

                procFailedRepo.save(procFailedTrans);
                return responseModel;
            }

            OtpValidateRequest request1 = new OtpValidateRequest();
            request1.setOtp(rq.getOtp());
            request1.setRequestId(rq.getRequestId().trim());

            List<BvnIdentityLog> fBvn = bvnIdentityLogRepository.findByRequestId(rq.getRequestId().trim());
            if (fBvn.size() <= 0) {

                ProcessorUserFailedTransInfo procFailedTrans = new ProcessorUserFailedTransInfo("upgrade-user-tier",
                        "RequestId is invalid!",
                        String.valueOf(GlobalMethods.generateTransactionId()), fBvn.get(0).getPhoneNumber1(), "", "Utilities-Service");

                procFailedRepo.save(procFailedTrans);
                responseModel.setDescription("RequestId is invalid!");
                responseModel.setStatusCode(400);
                return responseModel;
            }
            if (!fBvn.get(0).getWalletNo().equals(rq.getPhoneNumber())) {

                ProcessorUserFailedTransInfo procFailedTrans = new ProcessorUserFailedTransInfo("upgrade-user-tier",
                        "RequestId is invalid!",
                        String.valueOf(GlobalMethods.generateTransactionId()), fBvn.get(0).getPhoneNumber1(), "", "Utilities-Service");

                procFailedRepo.save(procFailedTrans);
                responseModel.setDescription("RequestId is invalid!");
                responseModel.setStatusCode(400);
                return responseModel;
            }
            // System.out.println("registerUser :::::::: otp validation a success" + "
            // ::::::::::::::::::::: " + getInitAcPin.get(0).getUserId());

            BaseResponse bRes = otpService.validateOtp(request1);
            boolean resToReturn = false;

            if (bRes.getStatusCode() == HttpServletResponse.SC_OK) {

                WalletTierVerifyBvn logint = walletTierVerifyBvnRepo.findByPhoneNumberProductCodeDe(fBvn.get(0).getPhoneNumberProductCode());
                logint.setBvn(fBvn.get(0).getBvn());
                logint.setBvnPhoneNumber(fBvn.get(0).getPhoneNumber1());
                logint.setCreatedDate(Instant.now());
                logint.setFirstName(fBvn.get(0).getFirstName());
                logint.setLastModifiedDate(Instant.now());
                logint.setLastName(fBvn.get(0).getLastName());
                logint.setRequestId(fBvn.get(0).getRequestId());
                logint.setWalletNo(fBvn.get(0).getWalletNo());
                logint.setBvnVerificationStatus(3);

                responseModel.setDescription(utilMeth.getTier2() + " validate BVN was sucessful.");
                responseModel.setStatusCode(bRes.getStatusCode());
                responseModel.addData("bvnNumber", fBvn.get(0).getBvn());
                responseModel.addData("phoneNumber", fBvn.get(0).getPhoneNumber1());
                return responseModel;

            } else {
                // if (bRes.getStatusCode() != 200) {

                ProcessorUserFailedTransInfo procFailedTrans = new ProcessorUserFailedTransInfo("upgrade-user-tier",
                        bRes.getDescription(),
                        String.valueOf(GlobalMethods.generateTransactionId()), fBvn.get(0).getPhoneNumber1(), "", "Utilities-Service");

                procFailedRepo.save(procFailedTrans);

                responseModel.setDescription(bRes.getDescription());
                responseModel.setStatusCode(bRes.getStatusCode());
                return responseModel;

            }

        } catch (Exception ex) {
            responseModel.setDescription(statusMessage);
            responseModel.setStatusCode(statusCode);
            ex.printStackTrace();
        }

        return responseModel;

    }

    public BaseResponse authenticateUserAdmin(AuthUserRequest rq, String channel) {

        BaseResponse responseModel = new BaseResponse();
        int statusCode = 500;
        String statusMessage = "An error occured,please try again";
        try {
            statusCode = 400;

            if (!utilMeth.isValidEmailAddress(rq.getEmailAddress())) {
                //  if (!utilMeth.isNumeric(rq.getEmailAddress())) {

                ProcessorUserFailedTransInfo procFailedTrans = new ProcessorUserFailedTransInfo(
                        "authenticate-user", "Authenticate User failed, The details is not Email Address!",
                        String.valueOf(GlobalMethods.generateTransactionId()), "", channel, "Utilities-Service"
                );

                responseModel.setDescription("Authenticate User failed, The details is not a valid Email Address!");
                responseModel.setStatusCode(statusCode);

                procFailedRepo.save(procFailedTrans);
                return responseModel;

            }

            Optional<UserDetails> getUserDetailsResult = userDeRepo.findByUserEmailId(rq.getEmailAddress());
            userDetailsResult = getUserDetailsResult.get();

            //logger.info(String.format("userDetailsResult.getPassword() >>>>>> +++++++++++++ =>%s", userDetailsResult.getPassword()));
            //if (!utilMeth.passwordEncoder().matches(rq.getPassword(), userDetailsResult.getPassword())) {
            String encodePwd = utilMeth.encyrpt(rq.getPassword(), encryptionKey);

            if (!encodePwd.equals(userDetailsResult.getPassword())) {

                ProcessorUserFailedTransInfo procFailedTrans = new ProcessorUserFailedTransInfo(
                        "authenticate-user", "Authenticate User failed, Password is invalid!",
                        String.valueOf(GlobalMethods.generateTransactionId()), "", channel, "Utilities-Service"
                );

                responseModel.setDescription("Authenticate User failed, Password is invalid!");
                responseModel.setStatusCode(statusCode);

                procFailedRepo.save(procFailedTrans);
                return responseModel;

            }

            if (userDetailsResult.getEnabled().equals("0")) {

                ProcessorUserFailedTransInfo procFailedTrans = new ProcessorUserFailedTransInfo(
                        "authenticate-user", "Authenticate User failed, User disabled, please contact The Administrator!",
                        String.valueOf(GlobalMethods.generateTransactionId()), "", channel, "Utilities-Service"
                );

                responseModel.setDescription("Authenticate User failed, User disabled, please contact The Administrator!");
                responseModel.setStatusCode(statusCode);

                procFailedRepo.save(procFailedTrans);
                return responseModel;

            }

            responseModel.addData("productName", userDetailsResult.getProductName());
            responseModel.addData("productCode", userDetailsResult.getProdudctCode());
            responseModel.addData("emailAddress", userDetailsResult.getEmailAddress());

            responseModel.setStatusCode(HttpServletResponse.SC_OK);
            responseModel.setDescription(LOGIN_SUCCESSFUL);

        } catch (Exception ex) {
            responseModel.setDescription(statusMessage);
            responseModel.setStatusCode(statusCode);

            ex.printStackTrace();
        }

        return responseModel;

    }

}
