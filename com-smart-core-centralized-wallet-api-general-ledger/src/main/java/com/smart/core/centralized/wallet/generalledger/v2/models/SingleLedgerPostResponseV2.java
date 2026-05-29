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
public class SingleLedgerPostResponseV2 {
    private int statusCode;
    private String description;
    private Map<String, Object> data;
}
