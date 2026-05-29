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
public class NinIdentityLog implements Serializable {

    private static final long serialVersionUID = 9878913549L;
    private static final String SEQUENCE_NAME = "NINIDLOG_SEQ_GEN";
    private static final String GENERATOR_NAME = "NINIDLOG_GENRT";

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
    private String nin;
    private String birthcountry;
    private String birthlga;
    private String birthstate;
    private String centralID;
    private String educationallevel;
    private String email;
    private String emplymentstatus;
    private String firstname;
    private String gender;
    private String heigth;
    private String maritalstatus;
    private String nok_address1;
    private String nok_address2;
    private String nok_firstname;
    private String nok_lga;
    private String nok_middlename;
    private String nok_postalcode;
    private String nok_state;
    private String nok_surname;
    private String nok_town;
    private String nspokenlang;
    private String ospokenlang;
    private String pfirstname;
    @Lob
    @Column(name = "BASE_64_IMAGE", length = 1000000)
    private String photo;
    private String pmiddlename;
    private String profession;
    private String psurname;
    private String religion;
    private String residence_address;
    private String residence_Town;
    private String residence_lga;
    private String residence_state;
    private String residencestatus;
    private String self_origin_lga;
    private String self_origin_place;
    private String self_origin_state;
    private String verification_status;
    private String verification_reference;
    @CreationTimestamp
    // @Column(name = "CREATED_DATE", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Column(name = "CREATED_DATE", nullable = false, updatable = false)  @JsonIgnore
    private Instant createdDate;
    @Column(name = "LAST_MODIFIED_DATE")
    @JsonIgnore
    private Instant lastModifiedDate;
}
