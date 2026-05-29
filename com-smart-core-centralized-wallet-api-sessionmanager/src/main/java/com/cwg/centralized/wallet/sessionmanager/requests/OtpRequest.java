package com.cwg.centralized.wallet.sessionmanager.requests;

import lombok.Data;

@Data
public class OtpRequest {

    private String userId;

    private String serviceName;

    private String phoneNumber;

    private String otp;

    private String newUserId;

}
