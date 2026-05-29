package com.cwg.centralized.wallet.utility.identity.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.UpdateTimestamp;

/**

 @author SmartCore Contributors
 */
@Entity
@Data
@NoArgsConstructor
@Table(indexes = {
    @Index(name = "SMl2hKRtG95", columnList = "walletNo")
})
public class FaceLivelinessCheckLog implements Serializable {

    private static final long serialVersionUID = 215613549L;
    private static final String SEQUENCE_NAME = "FLNCLOG_SEQ_GEN";
    private static final String GENERATOR_NAME = "FLNCLOG_GENRT";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GENERATOR_NAME)
    @GenericGenerator(
            name = GENERATOR_NAME,
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                @Parameter(name = "sequence_name", value = SEQUENCE_NAME),
                @Parameter(name = "initial_value", value = "1"),
                @Parameter(name = "increment_size", value = "50"),
                @Parameter(name = "optimizer", value = "pooled")
            }
    )
    private Long logId;
    @Column(unique = true)
    private String walletNo;
    private boolean status;
    private String detail;
    private Float confidence;
    private Float confidence_in_percentage;
    @CreationTimestamp
    // @Column(name = "CREATED_DATE", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Column(name = "CREATED_DATE", nullable = false, updatable = false) @JsonIgnore
    private Instant createdDate;
    @UpdateTimestamp
    @Column(name = "LAST_MODIFIED_DATE")
    @JsonIgnore
    private Instant lastModifiedDate;
    private String verification_status;
    private String verification_reference;
    @Column(name = "REQUEST_ID")
    private String requestId;
}
