package com.consolefire.xpenser.store;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * JUnit 5 extension interface to provide a reusable PostgreSQL Testcontainer.
 * Implementing classes will automatically get a running PostgreSQL container
 * and have their Spring Boot datasource configured via @ServiceConnection.
 */
@Testcontainers // This annotation is needed on the interface to manage the container lifecycle
public interface PostgresTestcontainersTest {

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