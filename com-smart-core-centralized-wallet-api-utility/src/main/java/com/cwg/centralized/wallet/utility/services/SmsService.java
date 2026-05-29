/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwg.centralized.wallet.utility.services;

import com.cwg.centralized.wallet.utility.models.BaseResponse;
import com.cwg.centralized.wallet.utility.models.SMSRequestModel;
import com.cwg.centralized.wallet.utility.util.UttilityMethods;
import com.google.gson.Gson;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author SmartCore Contributors
 */
@Service
@Transactional
@RequiredArgsConstructor
public class SmsService {

    private static final String INTERNAL_SERVER_ERROR = "Something went wrong. Please try again later";

    @Value("${gen.sms.monty.base.url}")
    private String montyBaseUrl;

    @Autowired
    UttilityMethods uttilityMethods;
    Gson gson = new Gson();

    @Value("${gen.otp.encrypt.key}")
    private String encryptionKey;

    public ResponseEntity<BaseResponse> sendSMS(String destination, String otp, String appDeviceSig) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            RestTemplate withoutEurekarestTemplate = new RestTemplate();
            if (uttilityMethods.getSETTING_KEY_SMS_SERVICE_VENDOR().equals("monty")) {
                SMSRequestModel smsRequest = new SMSRequestModel();
                List<String> list = Stream.of(destination).collect(Collectors.toList());
                System.out.println(":::::::: appDeviceSig" + "::::: " + appDeviceSig);

                String newSig = appDeviceSig;

                String smsMessage = TemplateMessage.otpSMSEmailMessage(otp, newSig);

                System.out.println(":::::::: appDeviceSig smsMessage" + "::::: " + smsMessage);

                smsRequest.setDestination(list);
                smsRequest.setSource(uttilityMethods.getMontySeriveId());
                smsRequest.setText(smsMessage);
                // System.out.println("smsRequest reeq ::::::::::::::::               ::::: %S  " + new Gson().toJson(smsRequest));
                /*SMSResponseMonty response = montyMobileProxy.sendSMSToMonty(smsRequest, uttilityMethods.getMontySMS());
            System.out.println("smsRequest response ::::::::::::::::               ::::: %S  " + new Gson().toJson(response));*/

                //System.out.println("smsRequest reeq ::::::::::::::::               ::::: %S  " + new Gson().toJson(smsRequest));
                String requestJson = "{ \"source\": \"DemoPay\", \"destination\": [\"" + destination + "\"], \"text\": \"" + smsMessage + "\"}";
                System.out.println("MONTY sms requestJson ::::::::::::::::               ::::: %S  " + requestJson);

                //String requestJson = "{ \"source\": \"BillsnPay\", \"destination\": [\"" + destination + "\"], \"text\": \"" + smsMessage + "\"}";
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.add("Authorization", "Basic " + uttilityMethods.decrypt(uttilityMethods.getMontySMS(), encryptionKey));

                String url = montyBaseUrl + "/SendBulkSMS";
                HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
                // SMSResponseSmartCoreModel response = gson.fromJson(withoutEurekarestTemplate.postForObject(url, entity, String.class), SMSResponseSmartCoreModel.class);
                String response = withoutEurekarestTemplate.postForObject(url, entity, String.class);
                //String response = gson.fromJson(withoutEurekarestTemplate.postForObject(url, entity, String.class), String.class);
                System.out.println("MONTY sms response ::::::::::::::::               ::::: %S  " + response);

                // for (GlobalLimitConfig gConfig : globalLimitConfigRepo.findLimits()) {
                /*  for (SMSResponseMonty gConfig : response.getsMSResponseSmartCore()) {

                   SMSResponseMonty getDe = response.getsMSResponseSmartCore().get(0);
                if (gConfig.getErrorCode() == 0) {
                    baseResponse.setStatusCode(HttpServletResponse.SC_OK);
                    baseResponse.setDescription(gConfig.getDescription());
                } else {
                    baseResponse.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
                    baseResponse.setDescription(gConfig.getDescription());
                }

            }*/
            } else {

                String ACCOUNT_SID = uttilityMethods.decrypt(uttilityMethods.getSETTING_KEY_TWILLO_SID(), encryptionKey);
                String AUTH_TOKEN = uttilityMethods.decrypt(uttilityMethods.getSETTING_KEY_TWILLO_TOK(), encryptionKey);
                String TWI_PHN = uttilityMethods.decrypt(uttilityMethods.getSETTING_KEY_TWILLO_PHN_NUMB(), encryptionKey);
                String smsMessage = TemplateMessage.otpSMSEmailMessage(otp, appDeviceSig);

                String text = destination;
                String replacement = "+234";
                String result = replacement.concat(text.substring(1));
                System.out.println("TWILLO TO ::::::::::::::::               ::::: %S  " + result);

                Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
                Message message = Message.creator(
                        new com.twilio.type.PhoneNumber(result),
                        new com.twilio.type.PhoneNumber(TWI_PHN),
                        smsMessage)
                        .create();

                System.out.println("TWILLO sms response ::::::::::::::::               ::::: %S  " + message.getStatus());

                System.out.println(message.getStatus());

            }
            baseResponse.setStatusCode(HttpServletResponse.SC_OK);
            baseResponse.setDescription("SMS sent sucessfully.");

        } catch (Exception exception) {
            exception.printStackTrace();
            baseResponse.setDescription(INTERNAL_SERVER_ERROR);
            baseResponse.setStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

}
