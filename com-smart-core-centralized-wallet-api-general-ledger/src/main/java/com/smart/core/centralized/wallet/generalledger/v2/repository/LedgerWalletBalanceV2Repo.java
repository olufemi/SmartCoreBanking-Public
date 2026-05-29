/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.smart.core.centralized.wallet.generalledger.v2.repository;

/**
 *
 * @author SmartCore Contributors
 */


import com.smart.core.centralized.wallet.generalledger.v2.domains.LedgerWalletBalanceV2;
import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LedgerWalletBalanceV2Repo extends JpaRepository<LedgerWalletBalanceV2, String> {

    Optional<LedgerWalletBalanceV2> findByAccountNumberProductCode(String accountNumberProductCode);

    List<LedgerWalletBalanceV2> findByProductCode(String productCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from LedgerWalletBalanceV2 w where w.accountNumberProductCode = :walletKey")
    Optional<LedgerWalletBalanceV2> lockByWalletKey(@Param("walletKey") String walletKey);
    
   
}
