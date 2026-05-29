package com.smart.core.centralized.wallet.profilings;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
@EnableFeignClients
@EnableEurekaClient
public class CwgCentralizedwalletProfilingsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CwgCentralizedwalletProfilingsApplication.class, args);
	}

}
