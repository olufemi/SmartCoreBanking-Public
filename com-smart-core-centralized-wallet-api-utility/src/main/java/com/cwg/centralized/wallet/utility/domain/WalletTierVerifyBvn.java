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
@Table(name = "WALLET_TIER_BVN_LOG")
@Data
public class WalletTierVerifyBvn implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String SEQ_NAME = "WALLET_TIER_BVN_LOG_SEQ";
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

    Long id;
    @Column(name = "BVN", unique = true)
    private String bvn;

    private String walletNo;

    @Column(name = "PHONE_NUMB_PRO_CODE", unique = true)
    private String phoneNumberProductCode;

    private String firstName;
    private String lastName;
    @Column(name = "BVN_VERIFY_STATUS")
    private Integer bvnVerificationStatus = 0;

    private String bvnPhoneNumber;
    @Column(name = "REQUEST_ID")
    private String requestId;
    @CreatedDate
    // @Column(name = "CREATED_DATE", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Column(name = "CREATED_DATE", nullable = false, updatable = false) @JsonIgnore
    private Instant createdDate;
    @LastModifiedDate
    @Column(name = "LAST_MODIFIED_DATE")
    @JsonIgnore
    private Instant lastModifiedDate;

}
