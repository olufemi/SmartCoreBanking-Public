/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smart.core.centralized.wallet.profilings.repo;

import com.smart.core.centralized.wallet.profilings.domains.AppConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author SmartCore Contributors
 */
@Repository
public interface AppConfigRepo extends JpaRepository<AppConfig, Long> {
}