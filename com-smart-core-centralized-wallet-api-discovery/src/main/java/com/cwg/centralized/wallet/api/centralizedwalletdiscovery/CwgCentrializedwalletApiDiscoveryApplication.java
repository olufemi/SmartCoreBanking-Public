package com.cwg.centralized.wallet.api.centralizedwalletdiscovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class CwgCentrializedwalletApiDiscoveryApplication {

	public static void main(String[] args) {
		SpringApplication.run(CwgCentrializedwalletApiDiscoveryApplication.class, args);
	}

}
