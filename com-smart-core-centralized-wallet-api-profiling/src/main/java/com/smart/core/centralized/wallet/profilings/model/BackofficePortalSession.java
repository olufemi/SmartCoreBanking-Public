package com.smart.core.centralized.wallet.profilings.model;

import lombok.Data;

@Data
public class BackofficePortalSession {
    private String operatorId;
    private String emailAddress;
    private String fullName;
    private String productCode;
    private String roleCode;
    private String permissions;
    private String scope;
}
