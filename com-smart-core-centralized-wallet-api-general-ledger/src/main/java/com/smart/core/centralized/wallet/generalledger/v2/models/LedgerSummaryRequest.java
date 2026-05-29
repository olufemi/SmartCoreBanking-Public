/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart.core.centralized.wallet.generalledger.v2.models;

/**
 *
 * @author SmartCore Contributors
 */
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class LedgerSummaryRequest implements Serializable {

    private String accountNumber;
    private String productCode;
    private List<LedgerPeriodQuery> periods;

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }

    public List<LedgerPeriodQuery> getPeriods() { return periods; }
    public void setPeriods(List<LedgerPeriodQuery> periods) { this.periods = periods; }

    public static class LedgerPeriodQuery implements Serializable {

        private String code; // DAILY/WEEKLY/MONTHLY/YEARLY
        private LocalDateTime from; // optional
        private LocalDateTime to;   // optional

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        public LocalDateTime getFrom() { return from; }
        public void setFrom(LocalDateTime from) { this.from = from; }

        public LocalDateTime getTo() { return to; }
        public void setTo(LocalDateTime to) { this.to = to; }
    }
}

