package com.smart.core.centralized.wallet.generalledger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories
@EnableFeignClients
@EnableEurekaClient
@EnableScheduling
public class CwgCentralizedwalletGeneralLedgerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CwgCentralizedwalletGeneralLedgerApplication.class, args);
    }

}
