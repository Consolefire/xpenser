package com.consolefire.xpenser.cloud.user.config;

import static java.lang.String.format;

import com.consolefire.xpenser.cloud.user.utils.MapBuilder;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;

@Slf4j
public class TestContainersTestContextInitializer implements
    ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Logger TC_LOGGER = LoggerFactory.getLogger("TestContainerLogs");

    @Container
    public static PostgreSQLContainer<?> TC_POSTGRES_DB = new PostgreSQLContainer<>("postgres:16-alpine")
        .withLogConsumer(new Slf4jLogConsumer(TC_LOGGER))
        .waitingFor(Wait.forListeningPort())
        .withStartupTimeout(Duration.ofSeconds(10));

    static {
        if (!TC_POSTGRES_DB.isRunning()) {
            TC_POSTGRES_DB.start();
        }
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Map<String, Object> proprtiesMap = MapBuilder.<String, Object>builder()
            .forKey("spring.cloud.config.enabled").withValue(false)
            .forKey("eureka.client.enabled").withValue(false)
            .forKey("eureka.client.fetch-registry").withValue(false)
            .forKey("spring.datasource.url").withValue(TC_POSTGRES_DB.getJdbcUrl())
            .forKey("spring.datasource.username").withValue(TC_POSTGRES_DB.getUsername())
            .forKey("spring.datasource.password").withValue(TC_POSTGRES_DB.getPassword())
            .forKey("spring.datasource.driver-class-name").withValue(TC_POSTGRES_DB.getDriverClassName())
            .forKey("spring.flyway.enabled").withValue(true)
            .forKey("spring.flyway.url").withValue(TC_POSTGRES_DB.getJdbcUrl())
            .forKey("spring.flyway.user").withValue(TC_POSTGRES_DB.getUsername())
            .forKey("spring.flyway.password").withValue(TC_POSTGRES_DB.getPassword())
            .forKey("spring.flyway.clean-disabled").withValue(false)
            .build();

        System.out.println(proprtiesMap);

        String[] propertiesArray = proprtiesMap.entrySet().stream()
            .map(e -> {
                String s = format("%s=%s", e.getKey(), Optional.ofNullable(e.getValue()).map(String::valueOf).orElse(""));
                return s;
            })
            .collect(Collectors.toSet())
            .toArray(new String[]{});

        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(applicationContext, propertiesArray);
    }
}
