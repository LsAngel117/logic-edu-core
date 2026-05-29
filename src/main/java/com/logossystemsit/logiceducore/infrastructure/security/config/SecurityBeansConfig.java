package com.logossystemsit.logiceducore.infrastructure.security.config;

import com.logossystemsit.logiceducore.infrastructure.security.service.JwtService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityBeansConfig {

    @Bean
    public JwtService jwtService(JwtProperties properties) {
        return new JwtService(properties);
    }
}
