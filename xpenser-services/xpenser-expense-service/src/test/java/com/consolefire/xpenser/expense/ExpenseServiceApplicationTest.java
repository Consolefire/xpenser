package com.consolefire.xpenser.expense;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.consolefire.xpenser.expense.tc.TestContainerDatabaseInitializer;
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
@ActiveProfiles({"ComponentTest", "TestContainerTests"})
@TestInstance(Lifecycle.PER_CLASS)
@ContextConfiguration(initializers = TestContainerDatabaseInitializer.class)
class ExpenseServiceApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;


    @Test
    void shouldLoadApplicationContext() {
        assertNotNull(applicationContext);
        log.info("Context loaded with id: {}", applicationContext.getId());
    }

}