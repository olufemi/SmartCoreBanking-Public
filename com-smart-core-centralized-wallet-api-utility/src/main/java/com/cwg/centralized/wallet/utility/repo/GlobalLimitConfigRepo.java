/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwg.centralized.wallet.utility.repo;

import com.cwg.centralized.wallet.utility.domain.GlobalLimitConfig;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author SmartCore Contributors
 */
public interface GlobalLimitConfigRepo extends
        CrudRepository<GlobalLimitConfig, String> {

    @Query("SELECT config from GlobalLimitConfig config where config.category=:category")
    List<GlobalLimitConfig> findByLimitCategoryProductCode(String category);

    public static final String FIND_LIMITS = "SELECT dailyLimit, singleTransactionLimit, maximumBalance, category FROM GlobalLimitConfig";

    // @Query("SELECT config.dailyLimit, config.singleTransactionLimit, config.maximumBalance, config.category FROM GlobalLimitConfig config")
    @Query("SELECT config FROM GlobalLimitConfig config")
    public List<GlobalLimitConfig> findLimits();
}
