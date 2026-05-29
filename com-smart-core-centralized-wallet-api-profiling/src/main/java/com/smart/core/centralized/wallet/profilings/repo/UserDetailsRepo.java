/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smart.core.centralized.wallet.profilings.repo;

import com.smart.core.centralized.wallet.profilings.domains.UserDetails;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author SmartCore Contributors
 */
public interface UserDetailsRepo extends JpaRepository<UserDetails, Long> {

    boolean existsByEmailAddress(String emailAddress);

    boolean existsByProductName(String userName);

    boolean existsByOneTimePwd(String oneTimePwd);

    boolean existsByClearanceId(String clearanceId);

    @Query("SELECT u FROM UserDetails u where u.productName = :productName")
    Optional<UserDetails> findByProductName(@Param("productName") String productName);

    @Query("SELECT u FROM UserDetails u where u.productName = :productName")
    List<UserDetails> findByProductNameDe(@Param("productName") String productName);

    @Query("SELECT u FROM UserDetails u where u.clearanceId = :clearanceId")
    List<UserDetails> findByClearanceIdDe(@Param("clearanceId") String clearanceId);

    @Query("SELECT u FROM UserDetails u where u.produdctCode = :produdctCode")
    Optional<UserDetails> findByProdudctCode(@Param("produdctCode") String produdctCode);

    @Query("SELECT u FROM UserDetails u where u.produdctCode = :produdctCode")
    List<UserDetails> findByProdudctCodeDe(@Param("produdctCode") String produdctCode);

    @Query("SELECT u FROM UserDetails u where u.emailAddress = :emailAddress")
    Optional<UserDetails> findByUserEmailId(@Param("emailAddress") String emailAddress);

}
