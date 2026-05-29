/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.smart.core.centralized.wallet.generalledger.v2.enumm;

/**
 *
 * @author SmartCore Contributors
 */
public enum PostingModeV2 {
    ONE_SIDED,   // inflow credit only, withdrawal debit only
    BALANCED    // multi-leg settlement/transfer: net movement must be 0
}
