package com.smart.core.centralized.wallet.generalledger.v2.models;

import lombok.Data;

import java.util.Map;

@Data
public class LedgerReversalResponse {
    private int statusCode;
    private String description;
    private String originalEntryId;
    private String reversalRequestRef;
    private Map<String, Object> data;
}
