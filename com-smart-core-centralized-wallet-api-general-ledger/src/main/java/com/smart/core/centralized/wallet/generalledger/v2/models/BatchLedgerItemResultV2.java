/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart.core.centralized.wallet.generalledger.v2.models;

/**
 *
 * @author SmartCore Contributors
 */
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class BatchLedgerItemResultV2 {
    private String requestRef;      // rq.transactionId
    private String accountNumber;   // rq.phoneNumber
    private String productCode;
    private int statusCode;
    private String description;
    private Map<String, Object> data = new HashMap<>();
}
