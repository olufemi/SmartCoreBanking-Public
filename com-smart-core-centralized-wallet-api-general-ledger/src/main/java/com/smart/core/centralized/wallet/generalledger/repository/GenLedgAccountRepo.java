/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smart.core.centralized.wallet.generalledger.repository;

import com.smart.core.centralized.wallet.generalledger.domains.GenLedgAccount;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author SmartCore Contributors
 */
public interface GenLedgAccountRepo extends
        CrudRepository<GenLedgAccount, String> {

    GenLedgAccount findTopByOrderByIdDesc();

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByProductCode(String productCode);

    @Query("select tal1 from GenLedgAccount tal1 where tal1.productCode = :productCode and tal1.created = (select max(tal2.created) from GenLedgAccount tal2 where tal2.productCode = tal1.productCode)")
    Optional<GenLedgAccount> findByProductCode(@Param("productCode") String productCode);

    boolean existsByTransactionId(String transactionId);

    @Query("select tal1 from GenLedgAccount tal1 where tal1.phoneNumber = :phoneNumber and tal1.created = (select max(tal2.created) from GenLedgAccount tal2 where tal2.phoneNumber = tal1.phoneNumber)")
    Optional<GenLedgAccount> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    @Query("SELECT o FROM GenLedgAccount o where o.phoneNumber = :phoneNumber and o.productCode = :productCode")
    List<GenLedgAccount> findByPhoneNumberProdCode(@Param("phoneNumber") String phoneNumber, @Param("productCode") String productCode);

    @Query("select tal1 from GenLedgAccount tal1 where tal1.phoneNumberProductCode = :phoneNumberProductCode and tal1.created = (select max(tal2.created) from GenLedgAccount tal2 where tal2.phoneNumberProductCode = tal1.phoneNumberProductCode)")
    Optional<GenLedgAccount> findByOptPhoneNumberProdCode(@Param("phoneNumberProductCode") String phoneNumberProductCode);

    Page<GenLedgAccount> findAllByOrderByCreatedAscIdAsc(Pageable pageable);

}
