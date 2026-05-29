/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smart.core.centralized.wallet.profilings.services;

import com.smart.core.centralized.wallet.profilings.domains.ProcessorUserFailedTransInfo;
import com.smart.core.centralized.wallet.profilings.domains.ProcessorUserHistoryInfo;
import com.smart.core.centralized.wallet.profilings.domains.UserDetails;
import com.smart.core.centralized.wallet.profilings.domains.VerifyReqIdDetails;
import com.smart.core.centralized.wallet.profilings.model.BaseResponse;
import com.smart.core.centralized.wallet.profilings.model.InitiateForgetPwdDataUser;
import com.smart.core.centralized.wallet.profilings.model.OtpRequest;
import com.smart.core.centralized.wallet.profilings.model.OtpValidateRequest;
import com.smart.core.centralized.wallet.profilings.model.UserDetailsRequest;
import com.smart.core.centralized.wallet.profilings.model.VerifyOtp;
import com.smart.core.centralized.wallet.profilings.proxy.UtilitiesProxy;
import com.smart.core.centralized.wallet.profilings.repo.ProcessorUserFailedTransInfoRepo;
import com.smart.core.centralized.wallet.profilings.repo.ProcessorUserHistoryInfoRepo;
import com.smart.core.centralized.wallet.profilings.repo.UserDetailsRepo;
import com.smart.core.centralized.wallet.profilings.repo.VerifyReqIdDetailsRepo;
import com.smart.core.centralized.wallet.profilings.utils.GlobalMethods;
import com.smart.core.centralized.wallet.profilings.utils.UttilityMethods;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author SmartCore Contributors
 */
@Service
public class UserServices {

    private final ProcessorUserFailedTransInfoRepo procFailedRepo;

    private final ProcessorUserHistoryInfoRepo procTransRepo;
    private final UttilityMethods utilMeth;
    private final UserDetailsRepo userDeRepo;
    @Value("${gen.otp.encrypt.key}")
    private String encryptionKey;
    UserDetails userDetailsResult = new UserDetails();
    private final VerifyReqIdDetailsRepo verifyReqIdDetailsRepo;
    private static final String OTP_SUCCESSFULLY_SENT = "Otp Sent SuccessFully";
    private final UtilitiesProxy utilitiesProxy;

    public UserServices(ProcessorUserFailedTransInfoRepo procFailedRepo,
            ProcessorUserHistoryInfoRepo procTransRepo,
            UttilityMethods utilMeth,
            UserDetailsRepo userDeRepo,
            VerifyReqIdDetailsRepo verifyReqIdDetailsRepo,
            UtilitiesProxy utilitiesProxy) {

        this.procFailedRepo = procFailedRepo;
        this.procTransRepo = procTransRepo;
        this.utilMeth = utilMeth;
        this.userDeRepo = userDeRepo;
        this.verifyReqIdDetailsRepo = verifyReqIdDetailsRepo;
        this.utilitiesProxy = utilitiesProxy;

    }

    public BaseResponse createNewUser(UserDetailsRequest rq) {

        BaseResponse responseModel = new BaseResponse();
        int statusCode = 500;
        String statusMessage = "An error occured,please try again";
        try {
            statusCode = 400;

            String userId = null;

            System.out.println(">>>>>>=>%>>>>>>=>%>>>>>>=>% rq.getClearanceId()    >>>>=>%>>>>>>=>%    " + rq.getClearanceId());

            if (!utilMeth.getIfClearanceExist(rq.getClearanceId())) {

                ProcessorUserFailedTransInfo procFailedTrans = new ProcessorUserFailedTransInfo("create-user",
                        "The ClearanceId is invalid!", String.valueOf(GlobalMethods.generateTransactionId()), userId,
                        "", "Profiling-Service");
                procFailedRepo.save(procFailedTrans);
                responseModel.setDescription("The ClearanceId is invalid!");
                responseModel.setStatusCode(statusCode);
                return responseModel;

            }
            
             if (!utilMeth.lookupProductName(rq.getProductName())) {

                ProcessorUserFailedTransInfo procFailedTrans = new ProcessorUserFailedTransInfo("create-user",
                        "The ProductName is invalid!", String.valueOf(GlobalMethods.generateTransactionId()), userId,
                        "", "Profiling-Service");
                procFailedRepo.save(procFailedTrans);
                responseModel.setDescription("The ProductName is invalid!");
                responseModel.setStatusCode(statusCode);
                return responseModel;

            }

            if (userDeRepo.existsByClearanceId(rq.getClearanceId()) == true) {
                ProcessorUserFailedTransInfo procFailedTrans = new ProcessorUserFailedTransInfo("create-user",
                        "ClearanceId mismatch!", String.valueOf(GlobalMethods.generateTransactionId()), userId,
                        "", "Profiling-Service");
                procFailedRepo.save(procFailedTrans);
                responseModel.setDescription("ClearanceId mismatch!");
                responseModel.setStatusCode(statusCode);
                return responseModel;
            }

            if (!utilMeth.isPasswordValid(rq.getPassword())) {

                ProcessorUserFailedTransInfo procFailedTrans = new ProcessorUserFailedTransInfo("create-user",
                        "The Password is not valid!", String.valueOf(GlobalMethods.generateTransactionId()), userId,
                        "", "Profiling-Service");
                procFailedRepo.save(procFailedTrans);
                responseModel.setDescription("The Password is not valid!");
                responseModel.setStatusCode(statusCode);
                return responseModel;

            }

            if (!rq.getConfPassword().equals(rq.getPassword())) {

                ProcessorUserFailedTransInfo procFailedTrans = new ProcessorUserFailedTransInfo("create-user",
                        "The Password is not valid!", String.valueOf(GlobalMethods.generateTransactionId()), userId,
                        "", "Profiling-Service");
                procFailedRepo.save(procFailedTrans);
                responseModel.setDescription("The Password is not valid!");
                responseModel.setStatusCode(statusCode);
                return responseModel;
            }

            if (!utilMeth.isValidEmailAddress(rq.getEmailAddress().trim())) {
                ProcessorUserFailedTransInfo procFailedTrans = new ProcessorUserFailedTransInfo("create-user",
                        "Creating User failed, The User's EmailAddress is invlaid!",
                        String.valueOf(GlobalMethods.generateTransactionId()), userId, "", "Profiling-Service");

                procFailedRepo.save(procFailedTrans);
                responseModel.setDescription("Creating User failed, The User's EmailAddress is invlaid!");
                responseModel.setStatusCode(statusCode);

                return responseModel;
            }

            if (userDeRepo.existsByEmailAddress(rq.getEmailAddress().trim())) {
                ProcessorUserFailedTransInfo procFailedTrans = new ProcessorUserFailedTransInfo("create-user",
                        "Creating User failed, The User's EmailAddress already exist!",
                        String.valueOf(GlobalMethods.generateTransactionId()), userId, "", "Profiling-Service");

                procFailedRepo.save(procFailedTrans);
                responseModel.setDescription("Creating User failed, The User's EmailAddress already exist!");
                responseModel.setStatusCode(statusCode);

                return responseModel;
            }

            if (userDeRepo.existsByProductName(rq.getProductName())) {
                ProcessorUserFailedTransInfo procFailedTrans = new ProcessorUserFailedTransInfo("create-user",
                        "Creating User failed, The User's Username already exist!",
                        String.valueOf(GlobalMethods.generateTransactionId()), userId, "", "Profiling-Service");

                procFailedRepo.save(procFailedTrans);
                responseModel.setDescription("Creating User failed, The User's Username already exist!");
                responseModel.setStatusCode(statusCode);

                return responseModel;
            }

            List<UserDetails> usDe = userDeRepo.findByProductNameDe(rq.getProductName());
            if (usDe.size() > 0) {

                ProcessorUserFailedTransInfo procFailedTrans = new ProcessorUserFailedTransInfo("create-user",
                        "Creating User Details, Product Name already exists!",
                        String.valueOf(GlobalMethods.generateTransactionId()), userId, "", "Profiling-Service");

                procFailedRepo.save(procFailedTrans);
                responseModel.setDescription("Creating User Details, Product Name already exists!");
                responseModel.setStatusCode(statusCode);

                return responseModel;

            }

            if (!rq.getPassword().equals(rq.getConfPassword())) {

                ProcessorUserFailedTransInfo procFailedTrans = new ProcessorUserFailedTransInfo("create-user",
                        "Creating User Details, please confirm password!",
                        String.valueOf(GlobalMethods.generateTransactionId()), userId, "", "Profiling-Service");

                procFailedRepo.save(procFailedTrans);
                responseModel.setDescription("Creating User Details, please confirm password!");
                responseModel.setStatusCode(statusCode);

                return responseModel;
            }

            // send mail
            // Save UserDetails
            String productCode = utilMeth.generateReferralCode("Client-Onboarding");
            String encodedPwd = utilMeth.encyrpt(rq.getPassword(), encryptionKey);
            UserDetails rqq = new UserDetails();
            rqq.setCreatedDate(Instant.now());
            rqq.setEmailAddress(rq.getEmailAddress());
            rqq.setOneTimePwd("");
            rqq.setPassword(encodedPwd);
            rqq.setProductName(rq.getProductName());
            rqq.setProdudctCode(productCode);
            rqq.setClearanceId(rq.getClearanceId());
            rqq.setEnabled("1");
            userDeRepo.save(rqq);

            // Send API response
            ProcessorUserHistoryInfo procSucessTrans = new ProcessorUserHistoryInfo("create-user",
                    "User Details created successfully", String.valueOf(GlobalMethods.generateTransactionId()), userId,
                    "", "Processor-Channel");
            procTransRepo.save(procSucessTrans);

            responseModel.setDescription("User Details created successfully");
            responseModel.setStatusCode(200);

        } catch (Exception ex) {
            responseModel.setDescription(statusMessage);
            responseModel.setStatusCode(statusCode);

            ex.printStackTrace();
        }

        return responseModel;
    }

    public BaseResponse initiateForgetPwdDataUser(InitiateForgetPwdDataUser req) {

        BaseResponse responseModel = new BaseResponse();
        int statusCode = 500;
        String statusMessage = "An error occured,please try again";
        try {
            statusCode = 400;

            String processId = String.valueOf(GlobalMethods.generateTransactionId());

            Optional<UserDetails> getUserDetailsResult = userDeRepo.findByUserEmailId(req.getEmailAddress());
            userDetailsResult = getUserDetailsResult.get();

            System.out.println("Otp Sent To User ----- " + userDetailsResult.getProductName());
            // }
            OtpRequest otp = new OtpRequest();
            otp.setEmailAddress(userDetailsResult.getEmailAddress());
            otp.setUserId("");
            otp.setPhoneNumber("");
            otp.setServiceName("Create-Wallet-Profiling-Service-Send-Otp_By-Email");

            BaseResponse bRes = utilitiesProxy.sendOtpEmail(otp);
            if (bRes.getStatusCode() != 200) {

                responseModel.setDescription(bRes.getDescription());
                responseModel.setStatusCode(bRes.getStatusCode());
                return responseModel;
            }
            String otpReqId = (String) bRes.getData().get("requestId");
            String pNumb = (String) bRes.getData().get("phoneNumber");
            responseModel.addData("requestId", otpReqId);
            responseModel.addData("phoneNumber", pNumb);
            responseModel.setDescription(OTP_SUCCESSFULLY_SENT);
            responseModel.setStatusCode(200);
            VerifyReqIdDetails vDe = new VerifyReqIdDetails();
            vDe.setCreatedDate(Instant.now());
            vDe.setRequestId(otpReqId);
            vDe.setServiceName("Initiate-Forget-Password-Profiling-Service");
            vDe.setUserId(req.getEmailAddress());
            vDe.setUserIdType("emialAddress");
            vDe.setProcessId(processId);
            verifyReqIdDetailsRepo.save(vDe);

            ProcessorUserHistoryInfo procSucessTrans = new ProcessorUserHistoryInfo("user-initiate-forget-password",
                    "User initiate change password was successful",
                    String.valueOf(GlobalMethods.generateTransactionId()), req.getEmailAddress(), "", "Processor-Channel");
            procTransRepo.save(procSucessTrans);

            responseModel.setDescription("User initiate change password was successful");
            responseModel.setStatusCode(200);
        } catch (Exception ex) {
            responseModel.setDescription(statusMessage);
            responseModel.setStatusCode(statusCode);

            ex.printStackTrace();
        }

        return responseModel;
    }

    public BaseResponse verifForgetPwd(VerifyOtp rq) {

        BaseResponse responseModel = new BaseResponse();
        int statusCode = 500;
        String statusMessage = "An error occured,please try again";
        try {
            statusCode = 400;

            OtpValidateRequest request1 = new OtpValidateRequest();
            request1.setOtp(rq.getOtp());
            request1.setRequestId(rq.getRequestId());

            List<VerifyReqIdDetails> getInitAcPin = verifyReqIdDetailsRepo.findByRequestId(rq.getRequestId());

            if (getInitAcPin.size() < 0) {

                ProcessorUserFailedTransInfo procFailedTrans = new ProcessorUserFailedTransInfo(
                        "user-initiate-forget-password", "Request-id is not reconized!",
                        String.valueOf(GlobalMethods.generateTransactionId()), "", "", "Profiling-Service");

                responseModel.setDescription("Request-id is not reconized!");
                responseModel.setStatusCode(statusCode);

                procFailedRepo.save(procFailedTrans);
                return responseModel;

            }

            if (getInitAcPin.get(0).getUserIdType().equals("phoneNumber")) {

                Optional<UserDetails> getUserDetailsResult = userDeRepo.findByUserEmailId(getInitAcPin.get(0).getUserId());
                userDetailsResult = getUserDetailsResult.get();
                BaseResponse bRes = utilitiesProxy.validateOtp(request1);

                if (bRes.getStatusCode() == HttpServletResponse.SC_OK) {

                    ProcessorUserHistoryInfo procSucessTrans = new ProcessorUserHistoryInfo(
                            "user-initiate-forget-password", "User verification was successful",
                            String.valueOf(GlobalMethods.generateTransactionId()),
                            getUserDetailsResult.get().getEmailAddress(), "", "Processor-Channel");
                    procTransRepo.save(procSucessTrans);
                    responseModel.addData("processId", getInitAcPin.get(0).getProcessId());
                    responseModel.setDescription("User verification was successful");
                    responseModel.setStatusCode(200);
                }

            } else {
                Optional<UserDetails> getUserDetailsResult = userDeRepo
                        .findByUserEmailId(getInitAcPin.get(0).getUserId());
                userDetailsResult = getUserDetailsResult.get();
                BaseResponse bRes = utilitiesProxy.validateOtp(request1);
                if (bRes.getStatusCode() == HttpServletResponse.SC_OK) {

                    ProcessorUserHistoryInfo procSucessTrans = new ProcessorUserHistoryInfo(
                            "user-initiate-forget-password", "User verification was successful",
                            String.valueOf(GlobalMethods.generateTransactionId()),
                            getUserDetailsResult.get().getEmailAddress(), "", "Processor-Channel");
                    procTransRepo.save(procSucessTrans);
                    responseModel.addData("processId", getInitAcPin.get(0).getProcessId());
                    responseModel.setDescription("User verification was successful");
                    responseModel.setStatusCode(200);
                }
            }

        } catch (Exception ex) {
            responseModel.setDescription(statusMessage);
            responseModel.setStatusCode(statusCode);

            ex.printStackTrace();

        }

        return responseModel;
    }

}
