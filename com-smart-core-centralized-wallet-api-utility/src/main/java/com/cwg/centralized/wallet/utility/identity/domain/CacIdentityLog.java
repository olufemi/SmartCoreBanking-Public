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
    @Index(name = "JprcO0wf0I2Pq", columnList = "walletNo"),
    @Index(name = "jdHTUpwHKka9T", columnList = "rc_number")
})
public class CacIdentityLog implements Serializable {

    private static final long serialVersionUID = 9878913549L;
    private static final String SEQUENCE_NAME = "CACIDLOG_SEQ_GEN";
    private static final String GENERATOR_NAME = "CACIDLOG_GENRT";

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
    public boolean status;
    @Column(unique = true)
    public String rc_number;
    public String date_of_registration;
    private String email_address;
    private String branchAddress;
    public String company_name;
    public String company_type;
    public String email;
    public String state;
    public String lga;
    public String address;
    @CreationTimestamp
    // @Column(name = "CREATED_DATE", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Column(name = "CREATED_DATE", nullable = false, updatable = false) @JsonIgnore
    private Instant createdDate;
    @UpdateTimestamp
    @Column(name = "LAST_MODIFIED_DATE")
    @JsonIgnore
    private Instant lastModifiedDate;
    @Column(name = "REQUEST_ID")
    private String requestId;
}
