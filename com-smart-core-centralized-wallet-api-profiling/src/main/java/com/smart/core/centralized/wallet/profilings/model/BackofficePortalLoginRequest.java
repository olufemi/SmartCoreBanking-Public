package com.smart.core.centralized.wallet.profilings.model;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BackofficePortalLoginRequest {

    @NotBlank(message = "the field \"emailAddress\" is required")
    private String emailAddress;

    @NotBlank(message = "the field \"password\" is required")
    private String password;
}
