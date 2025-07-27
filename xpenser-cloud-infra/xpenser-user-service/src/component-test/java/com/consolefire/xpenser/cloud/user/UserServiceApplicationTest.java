package com.consolefire.xpenser.cloud.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.consolefire.xpenser.cloud.user.data.repository.UserRepository;
import com.consolefire.xpenser.testutils.testcontainers.WithTestContainers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;


@WithTestContainers
@SpringBootTest(properties = {"spring.cloud.config.enabled=false"})
public class UserServiceApplicationTest {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeAll
    void setupBeforeAll() {

    }

    @Test
    @DisplayName("Should start application context")
    void shouldUseContainer() {
        assertNotNull(applicationContext);
        String encode = passwordEncoder.encode("Admin@L0calH0$t");
        System.out.println(encode);
        assertTrue(passwordEncoder.matches("Admin@L0calH0$t", "$2a$10$6x9E3Kcnkuf4ek3W4aOKQuhq8FPxtKXtKy0DpD2jSGZR7amOWzTyO"));

    }
}
