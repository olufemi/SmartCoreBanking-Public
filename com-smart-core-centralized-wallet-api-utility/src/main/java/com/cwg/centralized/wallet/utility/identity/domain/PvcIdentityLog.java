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
    @Index(name = "jdPjmez4k1", columnList = "walletNo"),
    @Index(name = "jlr7PTozzgP", columnList = "phoneNumber")
})
public class PvcIdentityLog implements Serializable {

    private static final long serialVersionUID = 7191154657L;
    private static final String SEQUENCE_NAME = "PVCIDLOG_SEQ_GEN";
    private static final String GENERATOR_NAME = "PVCIDLOG_GENRT";

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
    public String requestReference;
    @Column(unique = true)
    public String pvcNumber;
    public String verificationStatus;
    public String serviceType;
    public String firstName;
    public String middleName;
    public String lastName;
    public String fullName;
    public String email;
    public String phoneNumber;
    public String gender;
    public String dateOfBirth;
    public String formattedDateOfBirth;
    public String occupation;
    public String pollingUnitId;
    public String pollingUnit;
    public String pollingWard;
    public String pollingLga;
    public String pollingDelimitation;
    @CreationTimestamp
    // @Column(name = "CREATED_DATE", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Column(name = "CREATED_DATE", nullable = false, updatable = false)  @JsonIgnore
    private Instant createdDate;
    @Column(name = "LAST_MODIFIED_DATE")
    @JsonIgnore
    private Instant lastModifiedDate;
}
