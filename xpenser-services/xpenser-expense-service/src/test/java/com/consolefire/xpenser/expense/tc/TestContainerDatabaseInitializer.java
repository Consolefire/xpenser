package com.consolefire.xpenser.expense.tc;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class TestContainerDatabaseInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public static final String SCHEMA_NAME = "component_test_schema";
    private static final PostgreSQLContainer CONTAINER = new PostgreSQLContainer(
        DockerImageName.parse("postgres:latest"));

    static {
        CONTAINER.withDatabaseName("component_test_db")
            .withInitScript("component_test_db/init-schema.sql")
            .withUrlParam("currentSchema", SCHEMA_NAME)
            .start();
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        TestPropertyValues.of(
            "spring.datasource.url=" + CONTAINER.getJdbcUrl(),
            "spring.datasource.username=" + CONTAINER.getUsername(),
            "spring.datasource.password=" + CONTAINER.getPassword(),
            "common.infra.db.host=" + CONTAINER.getHost(),
            "common.infra.db.port=" + CONTAINER.getExposedPorts().get(0),
            "common.infra.db.username=" + CONTAINER.getUsername(),
            "common.infra.db.password=" + CONTAINER.getPassword(),
            "common.infra.db.database-name=" + CONTAINER.getDatabaseName(),
            "common.infra.db.default-schema=" + SCHEMA_NAME
        ).applyTo(applicationContext);
    }
}