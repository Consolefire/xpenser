package com.consolefire.xpenser.cloud.auth.config;

import jakarta.annotation.PostConstruct;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

@Configuration
@RequiredArgsConstructor
public class AuthServerClientRegistrarConfiguration {

    private final RegisteredClientRepository registeredClientRepository;

//    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient registrarClient = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("registrar-client")
            .clientSecret("{noop}secret")
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .scope("client.create")
            .scope("client.read")
            .build();

        return new InMemoryRegisteredClientRepository(registrarClient);
    }



}
