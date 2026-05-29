package com.smart.core.centralized.wallet.generalledger.v2.domains;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "ledger_idempotency_v2",
        indexes = {
                @Index(name = "idx_li_v2_product", columnList = "productCode"),
                @Index(name = "idx_li_v2_status", columnList = "status"),
                @Index(name = "idx_li_v2_created", columnList = "createdAt")
        })
@Data
@NoArgsConstructor
public class LedgerIdempotencyV2 implements Serializable {

    @Id
    @Column(length = 300, nullable = false)
    private String idempotencyId;

    @Column(nullable = false, length = 50)
    private String productCode;

    @Column(nullable = false, length = 225)
    private String idempotencyKey;

    @Column(nullable = false, length = 225)
    private String batchRef;

    @Column(nullable = false, length = 64)
    private String requestHash;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(length = 500)
    private String errorMessage;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime modifiedAt = LocalDateTime.now();

    public static String id(String productCode, String idempotencyKey) {
        return productCode + ":" + idempotencyKey;
    }

    public static LedgerIdempotencyV2 pending(String productCode, String idempotencyKey, String batchRef, String requestHash) {
        LedgerIdempotencyV2 idem = new LedgerIdempotencyV2();
        idem.idempotencyId = id(productCode, idempotencyKey);
        idem.productCode = productCode;
        idem.idempotencyKey = idempotencyKey;
        idem.batchRef = batchRef;
        idem.requestHash = requestHash;
        idem.status = "PENDING";
        return idem;
    }

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (this.createdAt == null) this.createdAt = now;
        if (this.modifiedAt == null) this.modifiedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        this.modifiedAt = LocalDateTime.now();
    }
}
