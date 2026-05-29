package com.smart.core.centralized.wallet.profilings.model;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BackofficeApprovalSubmitRequest {

    @NotBlank(message = "the field \"productCode\" is required")
    private String productCode;

    @NotBlank(message = "the field \"operationType\" is required")
    private String operationType;

    @NotBlank(message = "the field \"requestPayload\" is required")
    private String requestPayload;
}
