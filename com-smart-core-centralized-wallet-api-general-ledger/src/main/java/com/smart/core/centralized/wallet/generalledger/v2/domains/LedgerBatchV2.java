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
import java.time.LocalDateTime;

@Entity
@Table(name = "ledger_batch_v2",
        indexes = {
                @Index(name = "idx_ledger_batch_v2_product", columnList = "productCode"),
                @Index(name = "idx_ledger_batch_v2_status", columnList = "status"),
                @Index(name = "idx_ledger_batch_v2_created", columnList = "createdAt")
        })
@Data
@NoArgsConstructor
public class LedgerBatchV2 implements Serializable {

    @Id
    @Column(length = 225, nullable = false)
    private String batchRef;

    @Column(nullable = false, length = 50)
    private String productCode;

    @Column(nullable = false, length = 20)
    private String status; // PENDING / POSTED / FAILED

    @Column(length = 64)
    private String requestHash;

    @Column(length = 20)
    private String postingMode;

    private Integer totalLegs;

    @Column(length = 500)
    private String errorMessage;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime postedAt;

    public static LedgerBatchV2 pending(String batchRef, String productCode) {
        LedgerBatchV2 b = new LedgerBatchV2();
        b.batchRef = batchRef;
        b.productCode = productCode;
        b.status = "PENDING";
        b.createdAt = LocalDateTime.now();
        return b;
    }
}
