package com.cwg.centralized.wallet.api;

import com.cwg.centralized.wallet.api.config.RouteMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.discovery.ServiceRouteMapper;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableZuulProxy
@EnableEurekaClient
@EnableFeignClients
public class CwgCentralizedwalletApiGatewayApplication {

	public static void main(String... args) {
		SpringApplication.run(CwgCentralizedwalletApiGatewayApplication.class, args);
	}

	@Bean
	public ServiceRouteMapper serviceRouteMapper() {
		return new RouteMapper();
	}

}
