package com.smart.core.centralized.wallet.profilings.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PortalCorsConfig implements WebMvcConfigurer {

    @Value("${smartcore.portal.allowed-origins:http://localhost:4173,http://localhost:3000,http://127.0.0.1:4173,http://127.0.0.1:3000}")
    private String allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(splitOrigins())
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization")
                .allowCredentials(false)
                .maxAge(3600);
    }

    private String[] splitOrigins() {
        return allowedOrigins.split("\\s*,\\s*");
    }
}
