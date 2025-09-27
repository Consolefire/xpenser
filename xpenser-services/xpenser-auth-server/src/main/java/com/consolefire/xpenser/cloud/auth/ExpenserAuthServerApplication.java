package com.consolefire.xpenser.cloud.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.consolefire.xpenser.cloud.auth"})
public class ExpenserAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpenserAuthServerApplication.class, args);
    }
}
