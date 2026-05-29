/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.smart.core.centralized.wallet.generalledger.v2.enumm;

/**
 *
 * @author SmartCore Contributors
 */
public enum LedgerPeriod {
DAILY, WEEKLY, MONTHLY, YEARLY;


public static LedgerPeriod from(String v) {
return LedgerPeriod.valueOf(v.toUpperCase());
}
}
