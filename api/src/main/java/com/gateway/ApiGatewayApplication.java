package com.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(scanBasePackages = { "com.gateway", "com.auth" })
@EntityScan(basePackages = { "com.auth.infrastructure" })
@EnableJpaRepositories(basePackages = { "com.auth.infrastructure" })
@EnableMongoRepositories(basePackages = { "com.auth.infrastructure" })
@ConfigurationPropertiesScan(basePackages = { "com.auth" })
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}