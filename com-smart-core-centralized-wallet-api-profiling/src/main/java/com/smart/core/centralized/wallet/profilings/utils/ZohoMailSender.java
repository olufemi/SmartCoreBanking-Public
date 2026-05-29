/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smart.core.centralized.wallet.profilings.utils;

import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

/**
 *
 * @author SmartCore Contributors
 */
@Service
public class ZohoMailSender {

    private static final Logger logger = Logger.getLogger(ZohoMailSender.class.getName());

    public void sendEmail(String to, String subject, String body) {

        final String ZOHO_HOST = "smtp.zoho.com";
        final String TLS_PORT = "587";

        final String SENDER_EMAIL = "admin@fintells.com";

        final String SENDER_USERNAME = "admin@fintells.com";
        final String SENDER_PASSWORD = "Adefolaoo123";

        // protocol properties
        Properties props = System.getProperties();
        props.setProperty("mail.smtps.host", ZOHO_HOST); // change to GMAIL_HOST for gmail // for gmail
        props.setProperty("mail.smtp.port", TLS_PORT);
        props.setProperty("mail.smtp.starttls.enable", "true");
        props.setProperty("mail.smtps.auth", "true");
        // close connection upon quit being sent
        props.put("mail.smtps.quitwait", "false");

        Session session = Session.getInstance(props, null);

        try {
            // create the message
            final MimeMessage msg = new MimeMessage(session);

            // set recipients and content
            msg.setFrom(new InternetAddress(SENDER_EMAIL));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            msg.setSubject(subject);
            msg.setText(body, "utf-8", "html");
            msg.setSentDate(new Date());
            Transport transport = session.getTransport("smtps");
            transport.connect(ZOHO_HOST, SENDER_USERNAME, SENDER_PASSWORD);
            transport.sendMessage(msg, msg.getAllRecipients());

            // send the mail
            /*try ( // this means you do not need socketFactory properties
                    Transport transport = session.getTransport("smtps")) {
                // send the mail
                transport.connect(ZOHO_HOST, SENDER_USERNAME, SENDER_PASSWORD);
                transport.sendMessage(msg, msg.getAllRecipients());
            }*/
        } catch (MessagingException e) {
            logger.log(Level.SEVERE, "Failed to send message", e);

        }
    }
}
