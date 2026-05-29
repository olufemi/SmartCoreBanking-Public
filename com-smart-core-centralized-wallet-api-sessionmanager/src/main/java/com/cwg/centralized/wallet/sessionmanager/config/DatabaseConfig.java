package com.cwg.centralized.wallet.sessionmanager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories("com.genledger")
@EnableTransactionManagement
public class DatabaseConfig {

}
