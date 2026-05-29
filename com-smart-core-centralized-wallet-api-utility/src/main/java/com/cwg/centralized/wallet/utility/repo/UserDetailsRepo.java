/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwg.centralized.wallet.utility.repo;

import com.cwg.centralized.wallet.utility.domain.UserDetails;
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

  
    @Query("SELECT u FROM UserDetails u where u.productName = :productName")
    Optional<UserDetails> findByProductName(@Param("productName") String productName);

    @Query("SELECT u FROM UserDetails u where u.productName = :productName")
    List<UserDetails> findByProductNameDe(@Param("productName") String productName);

    @Query("SELECT u FROM UserDetails u where u.produdctCode = :produdctCode")
    Optional<UserDetails> findByProdudctCode(@Param("produdctCode") String produdctCode);

    @Query("SELECT u FROM UserDetails u where u.produdctCode = :produdctCode")
    List<UserDetails> findByProdudctCodeDe(@Param("produdctCode") String produdctCode);

    @Query("SELECT u FROM UserDetails u where u.emailAddress = :emailAddress")
    Optional<UserDetails> findByUserEmailId(@Param("emailAddress") String emailAddress);

    @Query("SELECT u FROM UserDetails u where u.emailAddress = :emailAddress")
    List<UserDetails> findByUserEmailIdDe(@Param("emailAddress") String emailAddress);

}
