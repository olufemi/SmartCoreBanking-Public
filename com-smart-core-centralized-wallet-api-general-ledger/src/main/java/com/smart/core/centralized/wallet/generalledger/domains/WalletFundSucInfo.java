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
public class WalletFundSucInfo implements Serializable {

    @NotNull
    @Id
    private String id;

    private BigDecimal amountPaidIn;

    private BigDecimal amountPaidInCum;
    @Column(name = "PL_CUM_AMOUNT_PAIDIN")
    private BigDecimal p_cum_amountPaidIn;

    private BigDecimal swCharges;

    private BigDecimal swChargesCum;

    private BigDecimal pl_cum_swCharges;

    private BigDecimal demoPayCharges;
    @Column(name = "PL_CUM_KULEAN_CHARGES")
    private BigDecimal pl_cum_fMoneyCharges;

    private BigDecimal demoPayChargesCum;

    private BigDecimal amtCreToWallet;

    private BigDecimal amtCreToWalletCum;

    private BigDecimal pl_cum_amtCreToWallet;

    private String phoneNumber;

    private String transactionId;

    private String swRefInitatePayId;
    private String swRefValidatePayId;
    private String swVerifyId;

    private String swRefVerifyPayId;
    @Column(insertable = true, updatable = false)
    private LocalDateTime Created;

    public WalletFundSucInfo(BigDecimal amountPaidIn, BigDecimal amountPaidInCum, BigDecimal p_cum_amountPaidIn,
            BigDecimal swCharges, BigDecimal swChargesCum,
            BigDecimal pl_cum_swCharges, BigDecimal demoPayCharges, BigDecimal demoPayChargesCum, BigDecimal pl_cum_fMoneyCharges,
            BigDecimal amtCreditedToWallet, BigDecimal amtCreToWalletCum, BigDecimal pl_cum_amtCreToWallet, String phoneNumber, String transactionId,
            String swRefInitatePayId, String swRefValidatePayId, String swRefVerifyPayId, String swVerifyId
    ) {
        this.id = String.valueOf(GlobalMethods.generateTransactionId());
        this.amtCreToWallet = amtCreditedToWallet;
        this.amountPaidIn = amountPaidIn;
        this.swCharges = swCharges;
        this.phoneNumber = phoneNumber;
        this.transactionId = transactionId;
        this.swRefInitatePayId = swRefInitatePayId;
        this.swRefValidatePayId = swRefValidatePayId;
        this.swRefVerifyPayId = swRefVerifyPayId;
        this.amountPaidInCum = amountPaidInCum;
        this.p_cum_amountPaidIn = p_cum_amountPaidIn;
        this.swChargesCum = swChargesCum;
        this.demoPayChargesCum = demoPayChargesCum;
        this.amtCreToWalletCum = amtCreToWalletCum;
        this.demoPayCharges = demoPayCharges;
        this.pl_cum_swCharges = pl_cum_swCharges;
        this.pl_cum_fMoneyCharges = pl_cum_fMoneyCharges;
        this.pl_cum_amtCreToWallet = pl_cum_amtCreToWallet;
        this.swVerifyId = swVerifyId;

    }

    @PrePersist
    void onCreate() {

        this.setCreated(LocalDateTime.now());

    }

}
