/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart.core.centralized.wallet.generalledger.v2.domains;

/**
 *
 * @author SmartCore Contributors
 */


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ledger_wallet_balance_v2",
        indexes = {
                @Index(name = "idx_lwb_v2_account", columnList = "accountNumber"),
                @Index(name = "idx_lwb_v2_product", columnList = "productCode"),
                @Index(name = "idx_lwb_v2_modified", columnList = "modifiedAt")
        })
@Data
@NoArgsConstructor
public class LedgerWalletBalanceV2 implements Serializable {

    @Id
    @Column(length = 120, nullable = false)
    private String accountNumberProductCode; // "acct:product"

    @Column(nullable = false, length = 40)
    private String accountNumber;

    @Column(nullable = false, length = 50)
    private String productCode;

    @Column(length = 100)
    private String productName;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal bookBalance;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal merchantBookedBalance;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalCredit;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalDebit;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalCharges;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalSwCharges;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime modifiedAt = LocalDateTime.now();

    @Version
    private Long version;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (this.createdAt == null) this.createdAt = now;
        if (this.modifiedAt == null) this.modifiedAt = now;
        if (balance == null) balance = BigDecimal.ZERO;
        if (bookBalance == null) bookBalance = BigDecimal.ZERO;
        if (merchantBookedBalance == null) merchantBookedBalance = BigDecimal.ZERO;
        if (totalCredit == null) totalCredit = BigDecimal.ZERO;
        if (totalDebit == null) totalDebit = BigDecimal.ZERO;
        if (totalCharges == null) totalCharges = BigDecimal.ZERO;
        if (totalSwCharges == null) totalSwCharges = BigDecimal.ZERO;
    }

    @PreUpdate
    public void onUpdate() {
        this.modifiedAt = LocalDateTime.now();
    }

    public static String walletKey(String accountNumber, String productCode) {
        return accountNumber + ":" + productCode; // delimiter is important
    }

    public static LedgerWalletBalanceV2 newWallet(String accountNumber, String productCode, String productName) {
        LedgerWalletBalanceV2 w = new LedgerWalletBalanceV2();
        w.accountNumberProductCode = walletKey(accountNumber, productCode);
        w.accountNumber = accountNumber;
        w.productCode = productCode;
        w.productName = productName;
        w.balance = BigDecimal.ZERO;
        w.bookBalance = BigDecimal.ZERO;
        w.merchantBookedBalance = BigDecimal.ZERO;
        w.totalCredit = BigDecimal.ZERO;
        w.totalDebit = BigDecimal.ZERO;
        w.totalCharges = BigDecimal.ZERO;
        w.totalSwCharges = BigDecimal.ZERO;
        return w;
    }
}

