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
    @Index(name = "SMD1hKRtG95", columnList = "walletNo"),
    @Index(name = "KqfLm3fkIYe", columnList = "phoneNumber")
})
public class FacialCompIdentityLog implements Serializable {

    private static final long serialVersionUID = 1873913549L;
    private static final String SEQUENCE_NAME = "FCIDLOG_SEQ_GEN";
    private static final String GENERATOR_NAME = "FCIDLOG_GENRT";

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
    private String walletNo;
    public String phoneNumber;
    public String image_url_1;
    public String image_url_2;
    public String status;
    public String message;
    public Float confidence;
    @CreationTimestamp
   // @Column(name = "CREATED_DATE", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Column(name = "CREATED_DATE", nullable = false, updatable = false)   @JsonIgnore
    private Instant createdDate;
    @UpdateTimestamp
     @Column(name = "LAST_MODIFIED_DATE")
    @JsonIgnore
    private Instant lastModifiedDate;
}
