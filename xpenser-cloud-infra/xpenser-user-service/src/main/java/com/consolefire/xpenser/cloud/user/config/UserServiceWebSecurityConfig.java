package com.consolefire.xpenser.cloud.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
public class UserServiceWebSecurityConfig {

    @Configuration
    @Profile("NO_AUTH")
    public class ConfigureNoAuth {

        @Bean
        public SecurityWebFilterChain securityBypassFilterChain(ServerHttpSecurity http) {
            return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorize -> authorize
                    .anyExchange().permitAll()
                )
                .build();
        }
    }

    @Profile("!NO_AUTH")
    @Configuration
    @EnableWebFluxSecurity // Enables Spring Security's reactive web security features
    @EnableReactiveMethodSecurity // Enables @PreAuthorize, @PostAuthorize for reactive methods
    public static class ConfigureWithSecurity {

        @Bean
        public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
            return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Disable CSRF for API-only service
                .authorizeExchange(authorize -> authorize
                    .pathMatchers("/actuator/**").permitAll() // For health checks etc.
                    .pathMatchers("/admin/**").hasRole("ADMIN") // Only ADMIN role can access /admin endpoints
                    .anyExchange().authenticated() // All other requests require authentication
                )
                .httpBasic(Customizer.withDefaults()) // Enable HTTP Basic authentication
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable) // Disable default form login
                // For stateless API, do not save security context between requests
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .build();
        }

        @Bean
        public ReactiveAuthenticationManager authenticationManager(ReactiveUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
            UserDetailsRepositoryReactiveAuthenticationManager authenticationManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
            authenticationManager.setPasswordEncoder(passwordEncoder);
            return authenticationManager;
        }
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
