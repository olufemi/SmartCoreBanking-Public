package com.smart.core.centralized.wallet.generalledger.v2.models;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class LedgerReconciliationResponse {
    private int statusCode;
    private String description;
    private int checkedCount;
    private int matchedCount;
    private int mismatchCount;
    private List<WalletReconciliationResult> results = new ArrayList<WalletReconciliationResult>();

    @Data
    public static class WalletReconciliationResult {
        private String accountNumberProductCode;
        private String accountNumber;
        private String productCode;
        private BigDecimal ledgerBalance;
        private BigDecimal walletBalance;
        private BigDecimal difference;
        private String status;
        private String hashStatus;
        private String hashIssue;
    }
}
