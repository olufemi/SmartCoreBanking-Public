/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smart.core.centralized.wallet.generalledger.models;

import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author SmartCore Contributors
 */
@Data
public class ProcLedgerRequestApi {

    private BigDecimal kulFees;
    //private BigDecimal swFees;
    private BigDecimal transAmount;
    private String phoneNumber;
    // private String swRefrenceNumber;
    private String transactionId;
    private String narration;
    private String transactionType;
    private String productCode;
    private String productName;
}
