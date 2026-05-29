/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart.core.centralized.wallet.generalledger.v2.models;

import java.util.Map;
import lombok.Data;

/**
 *
 * @author SmartCore Contributors
 */

@Data
public class BatchLedgerFullItemResultV2 {
    private String requestRef;
    private String direction;
    private String accountNumber;
    private String productCode;
    private int statusCode;
    private String description;
    private Map data;
    private String legTag;
}
