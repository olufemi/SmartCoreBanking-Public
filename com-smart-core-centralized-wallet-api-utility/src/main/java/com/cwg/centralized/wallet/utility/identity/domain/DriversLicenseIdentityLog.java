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
    @Index(name = "wAI5wZVkDgl", columnList = "walletNo"),
    @Index(name = "Azmo8cEOWGi", columnList = "mobileNumber")
})
public class DriversLicenseIdentityLog implements Serializable {

    private static final long serialVersionUID = 1872663549L;
    private static final String SEQUENCE_NAME = "DLIDLOG_SEQ_GEN";
    private static final String GENERATOR_NAME = "DLIDLOG_GENRT";

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
    public String licenceNumber;
    public String previousLicenceNumber;
    public String licenceClass;
    public String licenceDescription;
    public String dateOfFirstIssuance;
    public String stateOfFirstIssuance;
    public String stateOfIssuance;
    public String dateOfIssuance;
    public String dateOfExpiration;
    public String verificationStatus;
    public String serviceType;
    public String firstName;
    public String middleName;
    public String lastName;
    public String fullName;
    public String gender;
    public String height;
    public String lgaOfOrigin;
    public String stateOfBirth;
    public String countryOfBirth;
    public String facialMark;
    public String disability;
    public String glasses;
    public String dateOfBirth;
    public String formattedDateOfBirth;
    public String mobileNumber;
    public String imageUrl;
    public String homeAddress;
    public String residenceLga;
    public String residenceState;
    public String nextOfKinPhoneNumber;
    public String motherMaidenName;
    @CreationTimestamp
    // @Column(name = "CREATED_DATE", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Column(name = "CREATED_DATE", nullable = false, updatable = false)  @JsonIgnore
    private Instant createdDate;
    @UpdateTimestamp
     @Column(name = "LAST_MODIFIED_DATE")
    @JsonIgnore
    private Instant lastModifiedDate;
}
