package com.consolefire.xpenser.cloud.gateway;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@EnableDiscoveryClient
@SpringBootApplication
public class ExpenserCloudGatewayApplication {

    @Value("${test.name:unknown}")
    private String testName;

    public static void main(String[] args) {
        SpringApplication.run(ExpenserCloudGatewayApplication.class, args);
    }

    @PostConstruct
    void print(){
        System.out.println("**** test: " + testName);
    }
}
