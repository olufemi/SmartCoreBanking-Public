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
public class NipIdentityLog implements Serializable {

    private static final long serialVersionUID = 7816154651L;
    private static final String SEQUENCE_NAME = "NIPIDLOG_SEQ_GEN";
    private static final String GENERATOR_NAME = "NIPIDLOG_GENRT";

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
    private String first_name;
    private String last_name;
    private String middle_name;
    private String dob;
    @Lob
    @Column(name = "BASE_64_IMAGE", length = 1000000)
    private String photo;
    private String gender;
    private String issued_at;
    private String issued_date;
    private String expiry_date;
    private String reference_id;
    private String number;
    @CreationTimestamp
   // @Column(name = "CREATED_DATE", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Column(name = "CREATED_DATE", nullable = false, updatable = false)  @JsonIgnore
    private Instant createdDate;
    @Column(name = "LAST_MODIFIED_DATE")
    @JsonIgnore
    private Instant lastModifiedDate;
    private String verification_status;
    private String verification_reference;
}
