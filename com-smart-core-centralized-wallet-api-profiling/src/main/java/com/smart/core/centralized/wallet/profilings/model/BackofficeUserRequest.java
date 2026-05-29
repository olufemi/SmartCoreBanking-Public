package com.smart.core.centralized.wallet.profilings.model;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BackofficeUserRequest {

    @NotBlank(message = "the field \"operatorId\" is required")
    private String operatorId;

    @NotBlank(message = "the field \"emailAddress\" is required")
    private String emailAddress;

    @NotBlank(message = "the field \"fullName\" is required")
    private String fullName;

    @NotBlank(message = "the field \"productCode\" is required")
    private String productCode;

    @NotBlank(message = "the field \"roleCode\" is required")
    private String roleCode;

    private String password;
}
