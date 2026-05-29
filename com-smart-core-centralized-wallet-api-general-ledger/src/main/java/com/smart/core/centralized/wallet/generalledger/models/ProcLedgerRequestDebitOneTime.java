/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smart.core.centralized.wallet.generalledger.models;

import lombok.Data;

/**
 *
 * @author SmartCore Contributors
 */
@Data
public class ProcLedgerRequestDebitOneTime {

    private String transactionId;
    private String phonenumber;
    private String description;
    private String finalCharges;
    private String fees;
    private String narration;
    private String productCode;
    private String productName;
    private String phoneNumberProductCode;
}
