/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwg.centralized.wallet.utility.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

/**
 *
 * @author SmartCore Contributors
 */
@Entity
@Table(name = "REG_BVN_NUMBER_LOG")
@Data
public class BvnNumberLog implements Serializable {

     private static final long serialVersionUID = 1L;

    private static final String SEQ_NAME = "REG_BVN_NUMBER_LOG_SEQ";
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pooled")
    @GenericGenerator(name = "pooled",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                @Parameter(name = "sequence_name", value = SEQ_NAME),
                @Parameter(name = "initial_value", value = "300"),
                @Parameter(name = "increment_size", value = "1"),
                @Parameter(name = "optimizer", value = "pooled")
            }
    )
    @Column(name = "ID")
    Long id;
    @Column(name = "BVN", unique = true)
    private String bvn;

    @Column(name = "WALLET_NO", unique = true)
    private String walletNo;
    @Lob
    @Column(name = "BASE_64_IMAGE", length = 100000)
    private String base64Image;

    private String firstName;
    private String middleName;
    private String lastName;
    private String bvnPhoneNumber;
    private String bvnPhoneNumber2;
    private String dateOfBirth;
    private String registrationDate;
    private String bvnEmailAddress;
    private String gender;
    private String lgaOfOrigin;
    private String lgaOfResidence;
    private String maritalStatus;
    private String nationality;
    private String residentialAddress;
    private String stateOfOrigin;
    private String stateOfResidence;
    @Column(name = "REQUEST_ID")
    private String requestId;
    @CreatedDate
   // @Column(name = "CREATED_DATE", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Column(name = "CREATED_DATE", nullable = false, updatable = false)
    @JsonIgnore
    private Instant createdDate;
    @LastModifiedDate
    //@Column(name = "LAST_MODIFIED_DATE", insertable = false, columnDefinition = "TIMESTAMP")
    @Column(name = "LAST_MODIFIED_DATE")
    @JsonIgnore
    private Instant lastModifiedDate;

}
