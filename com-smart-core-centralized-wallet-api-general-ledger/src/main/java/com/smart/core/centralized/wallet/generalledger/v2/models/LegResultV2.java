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

import java.util.Map;

@Data
public class LegResultV2 {
    private String legRef;
    private int statusCode;
    private String description;
    private Map<String, Object> data;
}
