package com.smart.core.centralized.wallet.generalledger.v2.models;

import lombok.Data;

@Data
public class LedgerAdminResponse {
    private int statusCode;
    private String description;
    private Object data;
}
