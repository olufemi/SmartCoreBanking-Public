/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart.core.centralized.wallet.generalledger.v2.models;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author SmartCore Contributors
 */


@Data
public class BatchLedgerCreditPostResponseV2 {
    private int statusCode;                 // 200 if ok / partial ok, 400 if allOrNothing failed
    private String description;
    private int total;
    private int successCount;
    private int failCount;
    private List<BatchLedgerPostLineResultV2> results = new ArrayList<>();
}
