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
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "ledger_security_event_v2",
        indexes = {
            @Index(name = "idx_lse_v2_product", columnList = "productCode"),
            @Index(name = "idx_lse_v2_type", columnList = "eventType"),
            @Index(name = "idx_lse_v2_created", columnList = "createdAt")
        })
@Data
@NoArgsConstructor
public class LedgerSecurityEventV2 implements Serializable {

    @Id
    @Column(length = 60, nullable = false)
    private String id;

    @Column(length = 225)
    private String productCode;

    @Column(nullable = false, length = 100)
    private String eventType;

    @Column(nullable = false, length = 40)
    private String severity;

    @Column(length = 225)
    private String aggregateRef;

    @Column(length = 64)
    private String requestHash;

    @Column(nullable = false, length = 500)
    private String reason;

    @Lob
    private String payload;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
