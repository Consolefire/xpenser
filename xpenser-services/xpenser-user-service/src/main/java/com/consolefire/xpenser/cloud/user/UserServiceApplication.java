package com.consolefire.xpenser.cloud.user;

import com.consolefire.xpenser.cloud.user.config.props.CommonApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({CommonApplicationProperties.class})
@SpringBootApplication(scanBasePackages = {"com.consolefire.xpenser.cloud.user"})
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
