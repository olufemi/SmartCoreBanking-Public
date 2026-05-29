/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwg.centralized.wallet.utility.services;

import com.cwg.centralized.wallet.utility.models.BaseResponse;
import com.cwg.centralized.wallet.utility.models.EmailRequest;
import com.cwg.centralized.wallet.utility.util.UttilityMethods;
import com.google.gson.Gson;

import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;


import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.web.client.RestClientException;

/**
 *
 * @author SmartCore Contributors
 */
@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class SendGridMailSender {

    @Autowired
    private JavaMailSender emailSender;

    private static final String ERROR_OCCURED = "An error occured. Pls try agian later";
    private static final String EMAIL_SENT = "Email sent successfully.";
    @Autowired
    UttilityMethods utilMeth;
    @Value("${gen.otp.encrypt.key}")
    private String encryptionKey;

    public BaseResponse sendEmail(EmailRequest rq) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        BaseResponse baseResponse = new BaseResponse();

        Email from = new Email("demo@example.com");
        String subject = rq.getSubject();
        Email to = new Email(rq.getTo());
        Content content = new Content("text/html", rq.getBody());
        Mail mail = new Mail(from, subject, to, content);
        System.out.println("send grid email otp req :::::::: " + "::::: " + new Gson().toJson(mail));
        String setAuth = utilMeth.decrypt(utilMeth.SETTING_KEY_WEB_SEND_GRID(), encryptionKey);

        SendGrid sg = new SendGrid(setAuth);
        //SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));
        System.out.println("System.getenv(SENDGRID_API_KEY):::::::: req" + "   >>>>>>>>>>>>>>>>>> ::::::::::::::::::::: " + System.getenv("SENDGRID_API_KEY"));
      
        Request request = new Request();
        try {
           
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (RestClientException exception) {
            baseResponse.setDescription(ERROR_OCCURED);
            baseResponse.setStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return baseResponse;
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            baseResponse.setDescription(exception.getMessage());
            baseResponse.setStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return baseResponse;
        }

        return baseResponse;

    }

}
