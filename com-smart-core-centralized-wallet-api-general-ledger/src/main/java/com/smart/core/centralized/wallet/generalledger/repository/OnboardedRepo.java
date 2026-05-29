/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smart.core.centralized.wallet.generalledger.repository;

import com.smart.core.centralized.wallet.generalledger.domains.Onboarded;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author SmartCore Contributors
 */
public interface OnboardedRepo extends
        CrudRepository<Onboarded, String> {

    boolean existsByWalletNo(String walletNo);

    @Query("SELECT u FROM Onboarded u where u.walletNo = :walletNo ")
    List<Onboarded> findByProductNameDe(@Param("walletNo ") String walletNo);

    @Query("SELECT u FROM Onboarded u where u.walletNo = :walletNo and u.productCode = :productCode")
    List<Onboarded> findByWalletNoProductCode(String walletNo, String productCode);

    @Query("SELECT u FROM Onboarded u where u.walletNo = :walletNo and u.productCode = :productCode")
    Onboarded findByWalletNoProductCodeUpdate(String walletNo, String productCode);

}
