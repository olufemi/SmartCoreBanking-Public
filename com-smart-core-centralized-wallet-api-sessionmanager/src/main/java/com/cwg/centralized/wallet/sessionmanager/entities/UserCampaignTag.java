package com.cwg.centralized.wallet.sessionmanager.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "USER_CAMPAIGN_TAGS")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserCampaignTag {

    private static final String SEQ_NAME = "KULEAN_LT_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pooled")
    @GenericGenerator(name = "pooled", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = SEQ_NAME),
        @Parameter(name = "initial_value", value = "300"),
        @Parameter(name = "increment_size", value = "1"),
        @Parameter(name = "optimizer", value = "pooled")})
    @Column(name = "ID")
    @JsonIgnore
    Long id;

    @Column(name = "CUSTOMER_ID")
    @JsonIgnore
    private String customerId;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "DIGITAL_LOANS")
    private String digitalLoans;

    @Column(name = "ACCESS_REWARDS")
    private String accessRewards;

    @Column(name = "FCY_TRANSFERS")
    private String fcyTransfers;

    @Column(name = "INSURANCE")
    private String insurance;

    @Column(name = "LIFE_STYLE")
    private String lifestyle;
}
