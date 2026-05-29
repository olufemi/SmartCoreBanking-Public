/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwg.centralized.wallet.utility.repo;

import com.cwg.centralized.wallet.utility.domain.UserLimitConfig;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author SmartCore Contributors
 */
public interface UserLimitConfigRepo extends
        CrudRepository<UserLimitConfig, String> {

    @Query("select config from UserLimitConfig config where config.phoneNumberProductCode=:phoneNumberProductCode")
    List<UserLimitConfig> findByPhoneNumberProductCode(String phoneNumberProductCode);

    @Query("select bs from UserLimitConfig bs where bs.phoneNumberProductCode=:phoneNumberProductCode")
    UserLimitConfig findByPhoneNumberProductCodeQuery(String phoneNumberProductCode);

}
