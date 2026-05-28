package com.logossystemsit.logiceducore;

import com.logossystemsit.logiceducore.infrastructure.security.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class LogicEduCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogicEduCoreApplication.class, args);
    }

}
