package com.cwg.centralized.wallet.utility.identity.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.data.annotation.LastModifiedDate;

/**

 @author SmartCore Contributors
 */
@Entity
@Data
@NoArgsConstructor
public class BvnIdentityLog implements Serializable {

    private static final long serialVersionUID = 9833913549L;
    private static final String SEQUENCE_NAME = "BVNIDLOG_SEQ_GEN";
    private static final String GENERATOR_NAME = "BVNIDLOG_GENRT";

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
    @Column(unique = true)
    private String bvn;
    private String nameOnCard;
    private String enrollmentBank;
    private String enrollmentBranch;
    private String registrationDate;
    private String levelOfAccount;
    private String nin;
    private String watchlisted;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String gender;
    private String phoneNumber1;
    private String phoneNumber2;
    private String dateOfBirth;
    private String lgaOfOrigin;
    private String stateOfOrigin;
    private String nationality;
    private String  phoneNumberProductCode;
    private String maritalStatus;
    @Lob
    @Column(name = "BASE_64_IMAGE", length = 1000000)
    private String base64Image;
    private String stateOfResidence;
    private String lgaOfResidence;
    private String residentialAddress;
    private String verification_status;
    private String verification_reference;
    // @Column(name = "CREATED_DATE", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Column(name = "CREATED_DATE", nullable = false, updatable = false)  @JsonIgnore
    private Instant createdDate;
    @LastModifiedDate
    @Column(name = "LAST_MODIFIED_DATE")
    @JsonIgnore
    private Instant lastModifiedDate;
    @Column(name = "REQUEST_ID")
    private String requestId;
}
