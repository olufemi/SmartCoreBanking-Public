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
@Table(name = "ledger_entry_v2",
        indexes = {
            @Index(name = "idx_le_v2_batch", columnList = "batchRef"),
            @Index(name = "idx_le_v2_wallet", columnList = "accountNumberProductCode"),
            @Index(name = "idx_le_v2_product", columnList = "productCode"),
            @Index(name = "idx_le_v2_created", columnList = "createdAt")
        },
        uniqueConstraints = {
            @UniqueConstraint(name = "uq_le_v2_product_request_ref", columnNames = {"productCode", "requestRef"}),
            @UniqueConstraint(name = "uq_le_v2_product_batch_leg", columnNames = {"productCode", "batchRef", "legRef"})
        })
@Data
@NoArgsConstructor
public class LedgerEntryV2 implements Serializable {

    @Id
    @Column(length = 60, nullable = false)
    private String id;

    @Column(length = 225)
    private String batchRef;

    @Column(length = 225)
    private String requestRef;

    @Column(length = 225)
    private String legRef;

    @Column(length = 225, nullable = false)
    private String transactionId; // per leg id

    @Column(nullable = false, length = 40)
    private String accountNumber;

    @Column(nullable = false, length = 120)
    private String accountNumberProductCode;

    @Column(nullable = false, length = 225)
    private String productCode;

    @Column(length = 100)
    private String productName;

    @Column(nullable = false, length = 225)
    private String legType; // CREDIT / DEBIT

    @Column(nullable = false, length = 225)
    private String transType; // Deposit / Withdrawal / etc

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal fees;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal finalCharges;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceBefore;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAfter;

    @Column(length = 255)
    private String narration;

    @Column(length = 255)
    private String description;

    @Column(length = 64)
    private String requestHash;

    @Column(length = 64)
    private String previousEntryHash;

    @Column(length = 64)
    private String entryHash;

    @Column(length = 1000)
    private String hashPayload;

    @Column(length = 60)
    private String reversalOfEntryId;

    @Column(length = 255)
    private String reversalReason;

    private Integer statusCode;     // 200/400/etc (store what you return)

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
