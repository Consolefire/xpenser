package com.consolefire.xpenser.cloud.user.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("component-test")
public class FlywayTestConfiguration {

    @Value("${spring.flyway.url}")
    private String flywayUrl;
    @Value("${spring.flyway.user}")
    private String flywayUser;
    @Value("${spring.flyway.password}")
    private String flywayPassword;
    @Value("${spring.flyway.locations:classpath:db/migration}") // Default Flyway location
    private String[] flywayLocations;
    @Value("${spring.flyway.baseline-on-migrate:false}")
    private boolean flywayBaselineOnMigrate;

    @Bean(initMethod = "migrate") // This makes Flyway run migrations on bean initialization
    public Flyway flyway() {
        return Flyway.configure()
            .dataSource(flywayUrl, flywayUser, flywayPassword)
            .locations(flywayLocations)
            .baselineOnMigrate(flywayBaselineOnMigrate)
            .load();
    }

}
