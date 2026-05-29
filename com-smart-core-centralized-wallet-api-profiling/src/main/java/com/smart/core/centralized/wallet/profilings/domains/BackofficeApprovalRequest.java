package com.smart.core.centralized.wallet.profilings.domains;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "BACKOFFICE_APPROVAL_REQUEST")
@Data
public class BackofficeApprovalRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String SEQ_NAME = "BACKOFFICE_APPROVAL_REQUEST_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pooled")
    @GenericGenerator(name = "pooled",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                @Parameter(name = "sequence_name", value = SEQ_NAME),
                @Parameter(name = "initial_value", value = "10000"),
                @Parameter(name = "increment_size", value = "1"),
                @Parameter(name = "optimizer", value = "pooled")
            }
    )
    @Column(name = "ID")
    private Long id;

    @Column(nullable = false, unique = true)
    private String approvalRef;

    @Column(nullable = false)
    private String productCode;

    @Column(nullable = false)
    private String operationType;

    @Column(nullable = false)
    private String status;

    @Lob
    @Column(nullable = false)
    private String requestPayload;

    private String requestedBy;
    private String decidedBy;
    private String decisionComment;
    private Instant requestedAt;
    private Instant decidedAt;
    private Instant consumedAt;
    private Instant expiresAt;
}
