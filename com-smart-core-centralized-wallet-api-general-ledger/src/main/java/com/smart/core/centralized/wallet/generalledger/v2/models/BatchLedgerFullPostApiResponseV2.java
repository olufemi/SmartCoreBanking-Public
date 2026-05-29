/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart.core.centralized.wallet.generalledger.v2.models;

/**
 *
 * @author SmartCore Contributors
 */



import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class BatchLedgerFullPostApiResponseV2 {
    private String groupRef;
    private int total;
    private int successCount;
    private int failedCount;
    private int statusCode;
    private String description;
    private List<BatchLedgerFullItemResultV2> results;
}
