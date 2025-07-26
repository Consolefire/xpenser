package com.consolefire.xpenser.cloud.user.data.repository;


import com.consolefire.xpenser.testutils.testcontainers.WithTestContainers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

@WithTestContainers
@DataJpaTest(properties = {"spring.cloud.config.enabled=false"})
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_CLASS,
    scripts = {"classpath:data/UserRepositoryComponentTest/script-insert-01.sql"})
public class UserRepositoryComponentTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void loads(){}

}
