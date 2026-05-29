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
import javax.persistence.PreUpdate;
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
public class WalletFundingInfoCum implements Serializable {

    @NotNull
    @Id
    private String phoneNumber;

    private String walletId;

    private int countSuccessTrans;

    private BigDecimal totalAmountPaidIn;

    private BigDecimal totalSwCharges;
    @Column(name = "TOTAL_KULEAN_CHARGES")
    private BigDecimal totalFMoneyChrge;

    private BigDecimal totalAmtCreToWallet;
    @Column(insertable = true, updatable = false)
    private LocalDateTime Created;
    private LocalDateTime Modified;

    public WalletFundingInfoCum(String phoneNumber, String walletId, int countSuccessTrans,
            BigDecimal totalAmountPaidIn, BigDecimal totalSwCharges, BigDecimal totalFMoneyChrge, BigDecimal totalAmtCreToWallet) {

        this.phoneNumber = phoneNumber;
        this.walletId = walletId;

        this.countSuccessTrans = countSuccessTrans;
        this.totalAmountPaidIn = totalAmountPaidIn;
        this.totalSwCharges = totalSwCharges;
        this.totalFMoneyChrge = totalFMoneyChrge;
        this.totalAmtCreToWallet = totalAmtCreToWallet;

    }

    @PrePersist
    void onCreate() {
        this.setCreated(LocalDateTime.now());
        this.setModified(LocalDateTime.now());
    }

    @PreUpdate
    void onUpdate() {

        this.setModified(LocalDateTime.now());
    }

}
