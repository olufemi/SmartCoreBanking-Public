package com.cwg.centralized.wallet.utility.services;

public class TemplateMessage {

    public static String otpSMSEmailMessage(String otp, String appDeviceSig) {
        if (appDeviceSig.equals("")) {

            String emailSMSMessage = "Dear " + "Customer" + ","
                    + " Here is your OTP from SmartCore Pay: " + otp + ". Please do not share with Anyone.";
            return emailSMSMessage;

        }
        String emailSMSMessage = "Dear " + "Customer" + ","
                + " Here is your OTP from SmartCore Pay: " + otp + ". Please do not share with Anyone " + appDeviceSig;
        return emailSMSMessage;
    }

    public static String oneTimeJoinTrans(String sender, String transactionId) {
        String sMSMessage = "Dear " + "Customer" + ","
                + " Here is a Transaction-Id: " + transactionId
                + " from " + sender + " to Join a transaction on SmartCore Pay, Thank you.";
        return sMSMessage;
    }

    public static String inventoryPwdEmailMessage(String customerName, String password) {
        String emailMessage = "<p>Dear " + customerName + ",</p>"
                + "<p>Here is your Inventory login password: <b>" + password + "</b>.</p>";
        return emailMessage;
    }

}
