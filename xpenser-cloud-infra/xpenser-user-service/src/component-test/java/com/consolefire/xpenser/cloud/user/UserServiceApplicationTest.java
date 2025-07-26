package com.consolefire.xpenser.cloud.user;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.consolefire.xpenser.cloud.user.data.repository.UserRepository;
import com.consolefire.xpenser.testutils.testcontainers.WithTestContainers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;


@WithTestContainers
@SpringBootTest(properties = {"spring.cloud.config.enabled=false"})
public class UserServiceApplicationTest {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    UserRepository userRepository;

    @BeforeAll
    void setupBeforeAll() {

    }

    @Test
    @DisplayName("Should start application context")
    void shouldUseContainer() {
        assertNotNull(applicationContext);
        userRepository.findByUsername("test");
    }
}
