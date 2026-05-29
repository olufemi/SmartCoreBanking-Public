/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart.core.centralized.wallet.generalledger.v2.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author SmartCore Contributors
 */
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class LedgerSummaryResponse implements Serializable {

    private int statusCode;
    private String description;
    private List<LedgerPeriodSummary> periods;

    public LedgerSummaryResponse() {
        this.periods = new ArrayList<LedgerPeriodSummary>();
    }

    public int getStatusCode() { return statusCode; }
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<LedgerPeriodSummary> getPeriods() { return periods; }
    public void setPeriods(List<LedgerPeriodSummary> periods) { this.periods = periods; }

    public static class LedgerPeriodSummary implements Serializable {

        private String code; // DAILY/WEEKLY/MONTHLY/YEARLY

        private BigDecimal creditAmount; // sum(credit)
        private Integer creditCount;     // count(credit)

        private BigDecimal debitAmount;  // sum(debit)
        private Integer debitCount;      // count(debit)

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        public BigDecimal getCreditAmount() { return creditAmount; }
        public void setCreditAmount(BigDecimal creditAmount) { this.creditAmount = creditAmount; }

        public Integer getCreditCount() { return creditCount; }
        public void setCreditCount(Integer creditCount) { this.creditCount = creditCount; }

        public BigDecimal getDebitAmount() { return debitAmount; }
        public void setDebitAmount(BigDecimal debitAmount) { this.debitAmount = debitAmount; }

        public Integer getDebitCount() { return debitCount; }
        public void setDebitCount(Integer debitCount) { this.debitCount = debitCount; }
    }
}

