/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smart.core.centralized.wallet.generalledger.domains;

import com.smart.core.centralized.wallet.generalledger.utils.GlobalMethods;
import java.io.Serializable;
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
public class WalletFundTransFailed implements Serializable {

    @NotNull
    @Id
    private String id;

    private String phoneNumber;

    private Double amountToFund;

    private String walletId;

    private String transactionId;

    private String swTransId;

    private String swRefInitatePayId;
    private String swRefValidatePayId;

    private String status;
    private String message;
    private String dataStatus;

    private String reason;

    private String reasonDemoPay;
    @Column(insertable = true, updatable = false)
    private LocalDateTime Created;
    private String savedResponse;

    public WalletFundTransFailed(String phoneNumber, Double amountToFund, String walletId, String transactionId, String swTransId, String swRefInitatePayId, String swRefValidatePayId,
            String status, String reason, String reasonDemoPay, String dataStatus, String message, String savedResponse) {
        this.id = String.valueOf(GlobalMethods.generateTransactionId());
        this.phoneNumber = phoneNumber;
        this.amountToFund = amountToFund;
        this.walletId = walletId;
        this.transactionId = transactionId;
        this.swTransId = swTransId;
        this.swRefInitatePayId = swRefInitatePayId;
        this.swRefValidatePayId = swRefValidatePayId;
        this.status = status;
        this.reason = reason;
        this.reasonDemoPay = reasonDemoPay;
        this.dataStatus = dataStatus;
        this.message = message;
        this.savedResponse = savedResponse;

    }

    @PrePersist
    void onCreate() {

        this.setCreated(LocalDateTime.now());

    }

}
