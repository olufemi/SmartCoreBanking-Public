package com.smart.core.centralized.wallet.generalledger.v2.models;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LedgerReversalRequest {

    @NotNull
    private String productCode;

    private String originalRequestRef;

    private String originalTransactionId;

    @NotNull
    private String reversalRequestRef;

    private String reversalType;

    private String approvalRef;

    private String fulfilmentStatus;

    private String fulfilmentReference;

    private String fulfilmentResponseCode;

    private String fulfilmentEvidence;

    private String narration;

    private String reason;
}
