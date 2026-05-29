package com.cwg.centralized.wallet.sessionmanager.responses;

import lombok.Data;

@Data
public class AuthApiResponse {

    private String statusCode;

    private String statusMessage;

    private String productName;

    private String productCode;

    private String emailAddress;

}
