/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smart.core.centralized.wallet.generalledger.domains;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author SmartCore Contributors
 */
@Entity
@Data
@NoArgsConstructor
public class GenLedgAccountCum implements Serializable {

    @NotNull
    @Id
    private String phoneNumber;

    private String walletId;

    private int countSuccessTrans;

    private BigDecimal totalAmountCredited;

    private BigDecimal totalBalance;

    private BigDecimal totalBookBalance;
    private BigDecimal totalMerchantBookedBalance;

    private BigDecimal totalAmountDebited;

    private BigDecimal totalSwCharges;

    private BigDecimal totalPayCharge;
    private String phnProductCode;
    private int countSuccTransPhnProCode;

    private BigDecimal totalAmtCreditedPhnProCode;

    private BigDecimal totalBalancePhnProCode;

    private BigDecimal totalBookBalancePhnProCode;
    private BigDecimal totalMerBookedBalPhnProCode;

    private BigDecimal totalAmtDebitedPhnProCode;

    private BigDecimal totalSwChargesPhnProCode;

    private BigDecimal totalPayChargePhnProCode;

    @Column(insertable = true, updatable = false)
    private LocalDateTime Created;

    public GenLedgAccountCum(String phoneNumber, String walletId, int countSuccessTrans,
            BigDecimal totalAmountCredited, BigDecimal totalBalance,
            BigDecimal totalBookBalance, BigDecimal totalAmountDebited,
            BigDecimal totalSwCharges, BigDecimal totalPayCharge,
            BigDecimal totalMerchantBookedBalance,
            String phoneNumberProductCode, int countSuccTransPhnProCode, BigDecimal totalAmountCreditedPhnProCode,
            BigDecimal totalBalancePhnProCode, BigDecimal totalBookBalancePhnProCode,
            BigDecimal totalMerchantBookedBalancePhnProCode,
            BigDecimal totalAmountDebitedPhnProCode, BigDecimal totalSwChargesPhnProCode,
            BigDecimal totalPayChargePhnProCode
    ) {

        this.phoneNumber = phoneNumber;
        this.walletId = walletId;
        this.countSuccessTrans = countSuccessTrans;
        this.totalAmountCredited = totalAmountCredited;
        this.totalBalance = totalBalance;
        this.totalBookBalance = totalBookBalance;
        this.totalAmountDebited = totalAmountDebited;
        this.totalSwCharges = totalSwCharges;
        this.totalPayCharge = totalPayCharge;
        this.totalMerchantBookedBalance = totalMerchantBookedBalance;
        this.phnProductCode = phoneNumberProductCode;
        this.countSuccTransPhnProCode = countSuccTransPhnProCode;
        this.totalAmtCreditedPhnProCode = totalAmountCreditedPhnProCode;
        this.totalBalancePhnProCode = totalBalancePhnProCode;
        this.totalBookBalancePhnProCode = totalBookBalancePhnProCode;
        this.totalMerBookedBalPhnProCode = totalMerchantBookedBalancePhnProCode;
        this.totalAmtDebitedPhnProCode = totalAmountDebitedPhnProCode;
        this.totalSwChargesPhnProCode = totalSwChargesPhnProCode;
        this.totalPayChargePhnProCode = totalPayChargePhnProCode;

    }

    @PrePersist
    void onCreate() {
        this.setCreated(LocalDateTime.now());

    }
}
