package com.consolefire.xpenser.store.data.config;

import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class EntityAuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            // In a real application, you'd get the current user from Spring Security context
            // For example:
            // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            // if (authentication == null || !authentication.isAuthenticated()) {
            //     return Optional.of("anonymous"); // Or throw exception
            // }
            // return Optional.of(authentication.getName());

            // For now, return a fixed value or anonymous if no security
            return Optional.of("default_system_user"); // Replace with actual user logic
        };
    }
}