package com.smart.core.centralized.wallet.profilings.model;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class OtpRequest {

    private String userId;

    private String serviceName;

    private String phoneNumber;

    private String emailAddress;

    private String otp;

    private String resend;

    private String requestId;

    private String newUserId;

    public String getResend() {
        return resend;
    }

    public void setResend(String resend) {
        this.resend = resend;
    }

}
