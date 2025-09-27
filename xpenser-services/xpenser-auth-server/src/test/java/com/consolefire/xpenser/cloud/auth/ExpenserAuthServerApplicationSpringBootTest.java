package com.consolefire.xpenser.cloud.auth;

import static org.junit.jupiter.api.Assertions.*;

import com.consolefire.xpenser.cloud.auth.tc.TestContainerDatabaseInitializer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@Slf4j
@SpringBootTest
@ActiveProfiles({"ComponentTests", "TestContainerTests"})
@TestInstance(Lifecycle.PER_CLASS)
@ContextConfiguration(initializers = TestContainerDatabaseInitializer.class)
class ExpenserAuthServerApplicationSpringBootTest {

    @Autowired
    private ApplicationContext applicationContext;


    @Test
    void shouldLoadApplicationContext() {
        assertNotNull(applicationContext);
        log.info("Context loaded with id: {}", applicationContext.getId());
    }
}