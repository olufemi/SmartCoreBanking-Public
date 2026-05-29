package com.smart.core.centralized.wallet.generalledger.v2.domains;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "ledger_outbox_event_v2",
        indexes = {
            @Index(name = "idx_loe_v2_product", columnList = "productCode"),
            @Index(name = "idx_loe_v2_status", columnList = "status"),
            @Index(name = "idx_loe_v2_created", columnList = "createdAt")
        },
        uniqueConstraints = {
            @UniqueConstraint(name = "uq_loe_v2_product_event_ref", columnNames = {"productCode", "eventType", "aggregateRef"})
        })
@Data
@NoArgsConstructor
public class LedgerOutboxEventV2 implements Serializable {

    @Id
    @Column(length = 60, nullable = false)
    private String id;

    @Column(nullable = false, length = 225)
    private String productCode;

    @Column(nullable = false, length = 100)
    private String eventType;

    @Column(nullable = false, length = 225)
    private String aggregateRef;

    @Column(length = 64)
    private String requestHash;

    @Lob
    @Column(nullable = false)
    private String payload;

    @Column(nullable = false, length = 40)
    private String status;

    @Column(nullable = false)
    private Integer retryCount;

    @Column(length = 500)
    private String lastError;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime publishedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.retryCount == null) {
            this.retryCount = 0;
        }
        if (this.status == null) {
            this.status = "PENDING";
        }
    }
}
