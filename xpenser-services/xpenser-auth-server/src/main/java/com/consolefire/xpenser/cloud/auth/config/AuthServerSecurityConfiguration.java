package com.consolefire.xpenser.cloud.auth.config;

import com.consolefire.xpenser.cloud.auth.config.props.AuthServerProperties;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableConfigurationProperties(AuthServerProperties.class)
public class AuthServerSecurityConfiguration {

    private final AuthServerProperties authServerProperties;

    public AuthServerSecurityConfiguration(AuthServerProperties authServerProperties) {
        this.authServerProperties = authServerProperties;
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        AuthServerProperties.Endpoints endpoints = authServerProperties.getEndpoints();
        return AuthorizationServerSettings.builder()
                .issuer(authServerProperties.getIssuerUri())
                .authorizationEndpoint(endpoints.getAuthorization())
                .pushedAuthorizationRequestEndpoint(endpoints.getPushedAuthorizationRequest())
                .deviceAuthorizationEndpoint(endpoints.getDeviceAuthorization())
                .deviceVerificationEndpoint(endpoints.getDeviceVerification())
                .tokenEndpoint(endpoints.getToken())
                .tokenIntrospectionEndpoint(endpoints.getTokenIntrospection())
                .tokenRevocationEndpoint(endpoints.getTokenRevocation())
                .jwkSetEndpoint(endpoints.getJwkSet())
                .oidcLogoutEndpoint(endpoints.getOidcLogout())
                .oidcUserInfoEndpoint(endpoints.getOidcUserInfo())
                .oidcClientRegistrationEndpoint(endpoints.getOidcClientRegistration())
                .build();
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(
            HttpSecurity http, RegisteredClientRepository registeredClientRepository) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = OAuth2AuthorizationServerConfigurer
                .authorizationServer()
                .registeredClientRepository(registeredClientRepository);

        RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();
        http.securityMatcher(endpointsMatcher)
                .csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
                .with(authorizationServerConfigurer,
                        (authorizationServer) -> authorizationServer.oidc(Customizer.withDefaults()))
                .authorizeHttpRequests((authorize) -> authorize.anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                // Redirect to the login page when not authenticated from the
                .formLogin(Customizer.withDefaults())
                // authorization endpoint
                .exceptionHandling((exceptions) -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                );

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
            throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated()
                )
                // Form login handles the redirect to the login page from the
                // authorization server filter chain
                .formLogin(Customizer.withDefaults());

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authenticationProvider);
    }


}

