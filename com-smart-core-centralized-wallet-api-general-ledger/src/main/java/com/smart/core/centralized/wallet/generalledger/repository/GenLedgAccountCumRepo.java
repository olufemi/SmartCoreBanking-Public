/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smart.core.centralized.wallet.generalledger.repository;

import com.smart.core.centralized.wallet.generalledger.domains.GenLedgAccountCum;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author SmartCore Contributors
 */
public interface GenLedgAccountCumRepo extends
        CrudRepository<GenLedgAccountCum, String> {

    boolean existsByPhoneNumber(String phoneNumber);

    @Query("select tal1 from GenLedgAccountCum tal1 where tal1.phoneNumber = :phoneNumber and tal1.Created = (select max(tal2.Created) from GenLedgAccountCum tal2 where tal2.phoneNumber = tal1.phoneNumber)")
    Optional<GenLedgAccountCum> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    @Query("select tal1 from GenLedgAccountCum tal1 where tal1.phoneNumber = :phoneNumber and tal1.Created = (select max(tal2.Created) from GenLedgAccountCum tal2 where tal2.phoneNumber = tal1.phoneNumber)")
    List<GenLedgAccountCum> findByPhoneNumberList(@Param("phoneNumber") String phoneNumber);

    boolean existsByPhnProductCode(String phnProductCode);

    @Query("select tal1 from GenLedgAccountCum tal1 where tal1.phnProductCode = :phnProductCode and tal1.Created = (select max(tal2.Created) from GenLedgAccountCum tal2 where tal2.phnProductCode = tal1.phnProductCode)")
    Optional<GenLedgAccountCum> findByPhnProductCode(@Param("phnProductCode") String phnProductCode);
    
    @Query("select tal1 from GenLedgAccountCum tal1 where tal1.phnProductCode = :phnProductCode and tal1.Created = (select max(tal2.Created) from GenLedgAccountCum tal2 where tal2.phnProductCode = tal1.phnProductCode)")
    List<GenLedgAccountCum> findByPhnProductCodeDe(@Param("phnProductCode") String phnProductCode);
    
    List<GenLedgAccountCum> findAll();

}
