package com.consolefire.xpenser.cloud.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@EnableR2dbcRepositories("com.consolefire.xpenser.cloud.user.data.repository")
@SpringBootApplication(scanBasePackages = {"com.consolefire.xpenser.cloud.user"})
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
