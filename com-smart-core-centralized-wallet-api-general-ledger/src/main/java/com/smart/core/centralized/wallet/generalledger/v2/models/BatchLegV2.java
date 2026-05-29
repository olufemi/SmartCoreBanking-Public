/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart.core.centralized.wallet.generalledger.v2.models;

import com.smart.core.centralized.wallet.generalledger.v2.enumm.LegTypeV2;
import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author SmartCore Contributors
 */
@Data
public class BatchLegV2 {
    private String legRef;          // optional, unique per batch
    private String accountNumber;
    private String productCode;     // must match batch productCode
    private String productName;
    private String requestRef;

    private LegTypeV2 legType;      // CREDIT / DEBIT
    private String transType;       // Deposit / Withdrawal (your existing semantics)

    private BigDecimal amount;      // transAmount
    private BigDecimal fees;        // demoPay fees
    private BigDecimal finalCharges;// for DEBIT: amount+fees (required); for CREDIT can be amount (optional)

    private String description;
    private String narration;       // optional override per leg

    private String reversalOfEntryId;
    private String reversalReason;
}
