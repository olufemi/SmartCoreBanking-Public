/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smart.core.centralized.wallet.generalledger.repository;

import com.smart.core.centralized.wallet.generalledger.domains.WalletFundSucInfo;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author SmartCore Contributors
 */
public interface WalletFundingSucessInfoRepo extends
        CrudRepository<WalletFundSucInfo, String> {

    WalletFundSucInfo findTopByOrderByIdDesc();

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByTransactionId(String transactionId);

    @Query("select tal1 from WalletFundSucInfo tal1 where tal1.phoneNumber = :phoneNumber and tal1.Created = (select max(tal2.Created) from WalletFundSucInfo tal2 where tal2.phoneNumber = tal1.phoneNumber)")
    Optional<WalletFundSucInfo> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);
}
