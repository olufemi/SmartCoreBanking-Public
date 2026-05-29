package com.smart.core.centralized.wallet.generalledger.models;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import lombok.Data;

@MappedSuperclass
@Data
public abstract class BaseTransactionLimitConfig {

    @Column(name = "WITHDRAWAL")
    private String withdrawal;

    @Column(name = "WITHDRAWAL_SINGLE_TRANS")
    private String withdrawalSingleTransaction;

    /*  @Column(name = "ONE_TIME_PAY_SINGLE_TRANS")
    private String oneTimePaymentSingleTransfer;

    @Column(name = "ONE_TIME_PAY_TRANS")
    private String oneTimePaymentTransfer;

    @Column(name = "MILESTONE_SINGLE_TRANS")
    private String milePaymentSingleTransfer;*/

 /* @Column(name = "MILESTONE_TRANS")
    private String milePaymentTransfer;

    @Column(name = "WALLET_TRANS")
    private String walletTransfer;

    @Column(name = "WALLET_SINGLE_TRANS")
    private String walletSingleTransfer;*/
    @Column(name = "WALLET_DEPOSIT")
    private String walletDeposit;

    @Column(name = "WALLET_SINGLE_DEPOSIT")
    private String walletSingleDeposit;

    @Column(name = "MAXIMUM_BALANCE")
    private String maximumBalance;

    @Column(name = "MIN_BALANCE")
    private String minimumBalance;

    /* @Column(name = "DAILY_LIMIT")
    private String dailyLimit;

    @Column(name = "SINGLE_TRANSACTION_LIMIT")
    private String singleTransactionLimit;*/
    @Column(name = "CATGEORY")
    private String category;

    /* @Column(name = "AIR_TIME_TRANSFER")
    private String airTime;

    @Column(name = "AIR_TIME_SINGLE_TRAN")
    private String airtimeSingleTransaction;

    @Column(name = "ENAIRA_USER")
    private String enairaUser;

    @Column(name = "ENAIRA_USER_SINGLE")
    private String enairaUserSingle;

    @Column(name = "BILLS_PAYMENT_TRANSFER")
    private String billsPayment;

    @Column(name = "BILLS_SINGLE_TRANS")
    private String billsPaymentSingleTransaction;*/
}
