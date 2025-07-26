package com.consolefire.xpenser.cloud.user.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class UserServiceDataAccessConfiguration {

    /*@Configuration
    @RequiredArgsConstructor
    @EnableConfigurationProperties({FlywayProperties.class})
    public static class FlywayConfiguration {

        private final CommonApplicationProperties commonProperties;

        @Bean(initMethod = "migrate")
//        @ConditionalOnProperty(name = "spring.flyway.enabled", havingValue = "true")
        public Flyway flyway(FlywayProperties flywayProperties) {
            log.info("Configure flyway: {}", flywayProperties);
            return Flyway.configure()
                .dataSource(
                    flywayProperties.getUrl(),
                    commonProperties.getInfra().postgresql().getUsername(),
                    commonProperties.getInfra().postgresql().getPassword()
                )
                .locations(flywayProperties.getLocations().stream().toArray(String[]::new))
                .baselineOnMigrate(flywayProperties.isBaselineOnMigrate())
                .load();
        }

    }

    @Configuration
    @EnableR2dbcRepositories("com.consolefire.xpenser.cloud.user.data.repository")
    public static class ConfigureR2dbc extends AbstractR2dbcConfiguration {

        private final CommonApplicationProperties commonProperties;
        private final FlywayProperties flywayProperties;

        public ConfigureR2dbc(CommonApplicationProperties commonApplicationProperties,
            FlywayProperties flywayProperties) {
            this.commonProperties = commonApplicationProperties;
            this.flywayProperties = flywayProperties;
        }


        @Bean
        public ReactiveTransactionManager transactionManager() {
            return new R2dbcTransactionManager(connectionFactory());
        }

        @Bean
        public DatabaseClient databaseClient() {
            return DatabaseClient.create(connectionFactory());
        }

        @Bean
        @Override
        public ConnectionFactory connectionFactory() {
            PostgresqlConnectionConfiguration configuration = PostgresqlConnectionConfiguration.builder()
                .host(commonProperties.getInfra().postgresql().getHost())
                .port(commonProperties.getInfra().postgresql().getPort())
                .database(commonProperties.getInfra().postgresql().getDatabaseName())
                .username(commonProperties.getInfra().postgresql().getUsername())
                .password(commonProperties.getInfra().postgresql().getPassword())
                .schema(commonProperties.getInfra().postgresql().getCurrentSchema())
                .build();
            return new PostgresqlConnectionFactory(configuration);
        }


    }*/


}
