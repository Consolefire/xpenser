package com.consolefire.xpenser.store;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.TestcontainersExtension;
import org.testcontainers.utility.DockerImageName;

public class PostgresTestContainerTestExtension extends TestcontainersExtension {

    // Define the PostgreSQL container
    // @Container ensures JUnit manages its lifecycle
    // @ServiceConnection automatically configures Spring's datasource properties
    @Container
    @ServiceConnection
    PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
        DockerImageName.parse("postgres:16-alpine")
    )
        .withDatabaseName("testdb")
        .withUsername("testuser")
        .withPassword("testpass");

    // No @DynamicPropertySource needed here or in implementing classes
    // because @ServiceConnection handles the property injection.

}
