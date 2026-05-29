package com.smart.core.centralized.wallet.profilings.model;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BackofficePasswordResetRequest {

    @NotBlank(message = "the field \"operatorId\" is required")
    private String operatorId;

    @NotBlank(message = "the field \"password\" is required")
    private String password;
}
