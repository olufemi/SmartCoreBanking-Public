/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart.core.centralized.wallet.generalledger.v2.models;

import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author SmartCore Contributors
 */
@Data
public class BatchLedgerItemV2 {

    @NotNull
    private String requestRef;        // idempotency per line (client-generated)

    @NotNull
    private String direction;         // "DEBIT" or "CREDIT"

    @NotNull
    private String productCode;       // must match caller token productCode

    @NotNull
    private String transType;         // "Withdrawal" or "Deposit" (as your v1 expects)

    @NotNull
    private String accountNumber;     // renamed from phoneNumber (internally)

    @NotNull
    private String narration;

    @NotNull
    private String amount;            // keep as String to match your v1 style

    @NotNull
    private String fees;

    @NotNull
    private String finalCharges;

    // Optional: a free field for tagging legs like "BUYER", "SELLER", "NGN_GL"
    private String legTag;
}
