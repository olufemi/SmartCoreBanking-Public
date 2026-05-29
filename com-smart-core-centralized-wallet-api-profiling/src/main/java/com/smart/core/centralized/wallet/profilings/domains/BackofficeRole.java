package com.smart.core.centralized.wallet.profilings.domains;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "BACKOFFICE_ROLE")
@Data
public class BackofficeRole implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String SEQ_NAME = "BACKOFFICE_ROLE_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pooled")
    @GenericGenerator(name = "pooled",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                @Parameter(name = "sequence_name", value = SEQ_NAME),
                @Parameter(name = "initial_value", value = "100"),
                @Parameter(name = "increment_size", value = "1"),
                @Parameter(name = "optimizer", value = "pooled")
            }
    )
    @Column(name = "ID")
    private Long id;

    @Column(nullable = false, unique = true)
    private String roleCode;

    @Column(nullable = false)
    private String roleName;

    @Column(nullable = false)
    private String status;

    @Column(length = 2000)
    private String permissions;

    private String createdBy;
    private Instant createdAt;
    private Instant updatedAt;
}
