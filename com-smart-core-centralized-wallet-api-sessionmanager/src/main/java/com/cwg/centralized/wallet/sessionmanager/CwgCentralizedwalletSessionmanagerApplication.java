package com.cwg.centralized.wallet.sessionmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
@EnableFeignClients
@EnableEurekaClient
public class CwgCentralizedwalletSessionmanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CwgCentralizedwalletSessionmanagerApplication.class, args);
	}

}
