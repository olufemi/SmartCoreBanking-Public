package com.cwg.centralized.wallet.utility;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
@EnableFeignClients
@EnableEurekaClient
public class CwgCentralizedwalletUtilityApplication {

	public static void main(String[] args) {
		SpringApplication.run(CwgCentralizedwalletUtilityApplication.class, args);
	}

}
