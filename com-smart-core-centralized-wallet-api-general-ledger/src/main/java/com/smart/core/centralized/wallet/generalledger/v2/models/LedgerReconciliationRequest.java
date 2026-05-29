package com.smart.core.centralized.wallet.generalledger.v2.models;

import lombok.Data;

@Data
public class LedgerReconciliationRequest {
    private String productCode;
    private String accountNumber;
    private boolean includeMatched;
    private int maxResults = 500;
}
