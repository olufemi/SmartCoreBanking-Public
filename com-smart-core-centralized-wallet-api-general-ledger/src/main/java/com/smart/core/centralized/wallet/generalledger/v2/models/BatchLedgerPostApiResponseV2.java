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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class BatchLedgerPostApiResponseV2 {
    private String groupRef;
    private int statusCode;
    private String description;

    private int total;
    private int successCount;
    private int failedCount;

    private List<BatchLedgerItemResultV2> results = new ArrayList<>();
    private Map<String, Object> data = new HashMap<>();
}

