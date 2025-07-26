package com.consolefire.xpenser.testutils.testcontainers;

import com.consolefire.xpenser.cloud.user.config.TestContainersTestContextInitializer;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;


@Testcontainers
@Target(ElementType.TYPE)
@ActiveProfiles("component-test")
@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@Retention(RetentionPolicy.RUNTIME)
@ContextConfiguration(initializers = TestContainersTestContextInitializer.class)
public @interface WithTestContainers {

}
