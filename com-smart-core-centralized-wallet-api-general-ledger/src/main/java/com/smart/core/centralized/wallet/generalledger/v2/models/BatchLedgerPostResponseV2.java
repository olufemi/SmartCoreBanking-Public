/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart.core.centralized.wallet.generalledger.v2.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 *
 * @author SmartCore Contributors
 */
@Data
public class BatchLedgerPostResponseV2 {
    private int statusCode;
    private String description;

    private String batchRef;
    private String postingId; // optional
    private Map<String, Object> data;

    private List<LegResultV2> legResults = new ArrayList<>();
}
