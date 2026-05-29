/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwg.centralized.wallet.sessionmanager.repository;

import com.cwg.centralized.wallet.sessionmanager.entities.NotificationConfig;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.cache.annotation.Cacheable;



/**
 *
 * @author SmartCore Contributors
 */
public interface NotificationConfigRepo extends JpaRepository<NotificationConfig, Long> {

    @Cacheable("notificationConfigRepo")
    Optional<NotificationConfig> findByType(String type);

}
