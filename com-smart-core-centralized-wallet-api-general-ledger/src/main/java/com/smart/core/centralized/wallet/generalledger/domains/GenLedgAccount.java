/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smart.core.centralized.wallet.generalledger.domains;

import com.smart.core.centralized.wallet.generalledger.utils.GlobalMethods;
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
public class GenLedgAccount implements Serializable {

    @NotNull
    @Id
    private String id;

    private String transactionId;

    private String phoneNumber;

    private String transType;

    private BigDecimal accountCredit;

    private BigDecimal accountCreditCum;

    private BigDecimal balance;

    private BigDecimal bookBalance;

    private BigDecimal merchantBookedBalance;

    private BigDecimal pl_cum_AccountCredit;

    private BigDecimal accountDebit;

    private BigDecimal accountDebitCum;

    private BigDecimal pl_cum_AccountDebit;

    private BigDecimal swCharges;

    private BigDecimal swChargesCum;

    private BigDecimal pl_cum_swCharges;
    @Column(name = "PRODUCT_CHARGES")
    private BigDecimal demoPayCharges;
    @Column(name = "PL_CUM_CHARGES")
    private BigDecimal pl_cum_fMoneyCharges;
    @Column(name = "PRODUCT_CUM_CHARGES")
    private BigDecimal demoPayChargesCum;
    private String narration;
    private String productCode;
    private int countProductCodeTrans;
    private String productName;
    private BigDecimal productCodeFeeCum;
    private String phoneNumberProductCode;
    private BigDecimal balancePhnProCode;
    private BigDecimal accountCreditCumPhnProCode;
    private BigDecimal accountDebitCumPhnProCode;
    private BigDecimal swChargesCumGelPhnProCode;
    @Column(name = "PL_CUM_CHARGES_PHON_PRO_CODE")
    private BigDecimal fMoneyChargesCumGelPhnProCode;
    private BigDecimal bookBalancePhnProCode;
    private BigDecimal merchantBookedBalancePhnProCode;

//    @Column(insertable = true, updatable = false)
//    private LocalDateTime Created;
    @Column(name = "Created", updatable = false)
    private LocalDateTime created;   // v1 entity field name can be created

    public GenLedgAccount(String transactionId, String phoneNumber, String transType,
            BigDecimal accountCredit, BigDecimal balance, BigDecimal bookBalance,
            BigDecimal accountCreditCum, BigDecimal pl_cum_AccountCredit,
            BigDecimal accountDebit, BigDecimal accountDebitCum, BigDecimal pl_cum_AccountDebit,
            BigDecimal swCharges, BigDecimal swChargesCum, BigDecimal pl_cum_swCharges,
            BigDecimal demoPayCharges, BigDecimal pl_cum_fMoneyCharges,
            BigDecimal demoPayChargesCum, String narration,
            BigDecimal merchantBookedBalance, String productCode, String productName,
            int countProductCodeTrans, BigDecimal productCodeFeeCum,
            String phoneNumberProductCode, BigDecimal balancePhnProCode, BigDecimal accountCreditCumPhnProCode,
            BigDecimal accountDebitCumPhnProCode, BigDecimal swChargesCumGelPhnProCode,
            BigDecimal fMoneyChargesCumGelPhnProCode, BigDecimal bookBalancePhnProCode,
            BigDecimal merchantBookedBalancePhnProCode) {
        this.id = String.valueOf(GlobalMethods.generateTransactionId());
        this.transactionId = transactionId;
        this.phoneNumber = phoneNumber;
        this.transType = transType;
        this.accountCredit = accountCredit;
        this.accountCreditCum = accountCreditCum;
        this.balance = balance;
        this.bookBalance = bookBalance;
        this.pl_cum_AccountCredit = pl_cum_AccountCredit;
        this.accountDebit = accountDebit;
        this.accountDebitCum = accountDebitCum;
        this.pl_cum_AccountDebit = pl_cum_AccountDebit;
        this.swCharges = swCharges;
        this.swChargesCum = swChargesCum;
        this.pl_cum_swCharges = pl_cum_swCharges;
        this.demoPayCharges = demoPayCharges;
        this.pl_cum_fMoneyCharges = pl_cum_fMoneyCharges;
        this.demoPayChargesCum = demoPayChargesCum;
        this.narration = narration;
        this.merchantBookedBalance = merchantBookedBalance;
        this.productCode = productCode;
        this.productName = productName;
        this.countProductCodeTrans = countProductCodeTrans;
        this.productCodeFeeCum = productCodeFeeCum;
        this.phoneNumberProductCode = phoneNumberProductCode;
        this.accountCreditCumPhnProCode = accountCreditCumPhnProCode;
        this.accountDebitCumPhnProCode = accountDebitCumPhnProCode;
        this.swChargesCumGelPhnProCode = swChargesCumGelPhnProCode;
        this.fMoneyChargesCumGelPhnProCode = fMoneyChargesCumGelPhnProCode;
        this.bookBalancePhnProCode = bookBalancePhnProCode;
        this.merchantBookedBalancePhnProCode = merchantBookedBalancePhnProCode;
        this.balancePhnProCode = balancePhnProCode;

    }

    @PrePersist
    void onCreate() {
        if (this.created == null) {
            this.created = LocalDateTime.now();
        }
    }
}
