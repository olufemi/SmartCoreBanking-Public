/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.smart.core.centralized.wallet.generalledger.v2.repository;

import java.math.BigDecimal;

/**
 *
 * @author SmartCore Contributors
 */
public interface LedgerSummaryProjection {
    BigDecimal getCreditAmount();
    Long getCreditCount();
    BigDecimal getDebitAmount();
    Long getDebitCount();
}
