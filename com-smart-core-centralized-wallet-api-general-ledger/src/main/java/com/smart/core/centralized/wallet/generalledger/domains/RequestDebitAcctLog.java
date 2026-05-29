/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smart.core.centralized.wallet.generalledger.domains;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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

@Data
public class RequestDebitAcctLog implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String SEQ_NAME = "REQ_DEBIT_LOG_LOG_SEQ";
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
    private String transactionId;
    private String phonenumber;
    private String description;
    private String finalCharges;
    private String fees;
    private String narration;
    private String productCode;
    private String productName;
    private String phnNumbProductCode;
    private String transRequestId;
    private int genLedResCode;
    private String genLedResDesc;
    private String transStatus;
    private int transStatusCode;

    @CreatedDate
    // @Column(name = "CREATED_DATE", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Column(name = "CREATED_DATE", nullable = false, updatable = false)
    @JsonIgnore
    private Instant createdDate;
    @LastModifiedDate
    @Column(name = "LAST_MODIFIED_DATE")
    @JsonIgnore
    private Instant lastModifiedDate;

}
