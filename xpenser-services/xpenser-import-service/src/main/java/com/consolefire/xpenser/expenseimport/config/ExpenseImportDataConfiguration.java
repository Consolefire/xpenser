package com.consolefire.xpenser.expenseimport.config;

import com.consolefire.xpenser.expenseimport.data.entity.BaseEntity;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@EnableJpaAuditing
@EntityScan(basePackageClasses = {BaseEntity.class})
@EnableJpaRepositories(basePackages = {"com.consolefire.xpenser.expenseimport.data.repository"})
public class ExpenseImportDataConfiguration {

    @Bean
    AuditorAware<String> auditorAware() {
        return new AuditorAware<String>() {
            @Override
            public Optional<String> getCurrentAuditor() {
                return Optional.of("SYSTEM_USER");
            }
        };
    }

    @Bean
    JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
