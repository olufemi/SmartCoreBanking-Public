/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart.core.centralized.wallet.generalledger.v2.models;

import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author SmartCore Contributors
 */

@Data
public class SingleLedgerPostRequestV2 {
    private String requestRef;      // idempotency
    private String productCode;
    private String productName;

    private String accountNumber;
    private String narration;
    private String transType;       // Deposit / Withdrawal

    private BigDecimal amount;      // transAmount
    private BigDecimal fees;        // charges
    private BigDecimal finalCharges;// required for DEBIT: amount+fees

    private String description;
    private String groupRef;
}
