package com.consolefire.xpenser.cloud.auth.service;

import com.consolefire.xpenser.cloud.auth.api.model.ClientRegistrationRequest;
import com.consolefire.xpenser.cloud.auth.api.model.ClientRegistrationResponse;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class AuthServerClientRegistrationService {

    private final RegisteredClientRepository registeredClientRepository;

    /**
     * Reactive method to register a new client. Executes blocking repository calls on a dedicated thread pool using
     * Schedulers.boundedElastic().
     */
    public Mono<ClientRegistrationResponse> registerNewClientReactive(ClientRegistrationRequest request) {
        return Mono.fromCallable(() -> this.registerNewClient(request))
            .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Reactive method to find a client by its ID. Executes blocking repository calls on a dedicated thread pool using
     * Schedulers.boundedElastic(). Returns Mono.empty() if the client is not found.
     */
    public Mono<ClientRegistrationResponse> findClientByClientIdReactive(String clientId) {
        return Mono.fromCallable(() -> this.findClientByClientId(clientId))
            .subscribeOn(Schedulers.boundedElastic())
            .filter(java.util.Objects::nonNull);
    }

    // --- Blocking / Synchronous Logic (used by reactive wrappers) ---

    private ClientRegistrationResponse registerNewClient(ClientRegistrationRequest request) {
        // Necessary checks for critical non-nullable fields
        Assert.notNull(request, "ClientRegistrationRequest must not be null");
        Assert.hasText(request.getClientName(), "Client Name must not be empty");

        String generatedId = UUID.randomUUID().toString();
        String generatedClientId = UUID.randomUUID().toString();
        // Generates a random secret for the client. Using {noop} for simplicity.
        String generatedClientSecret = UUID.randomUUID().toString();

        RegisteredClient registeredClient = RegisteredClient.withId(generatedId)
            .clientId(generatedClientId)
            .clientSecret("{noop}" + generatedClientSecret)
            .clientName(request.getClientName())
            .clientIdIssuedAt(Instant.now())
            // Authentication Methods
            .clientAuthenticationMethods(methods -> {
                String authMethod = Optional.ofNullable(request.getTokenEndpointAuthMethod())
                    .orElse(ClientAuthenticationMethod.CLIENT_SECRET_BASIC.getValue());
                methods.add(new ClientAuthenticationMethod(authMethod));
            })
            // Grant Types
            .authorizationGrantTypes(grantTypes -> {
                Optional.ofNullable(request.getGrantTypes()).orElse(Collections.emptyList())
                    .stream()
                    .map(AuthorizationGrantType::new)
                    .forEach(grantTypes::add);
            })
            // URIs
            .redirectUris(
                uris -> uris.addAll(Optional.ofNullable(request.getRedirectUris()).orElse(Collections.emptyList())))
            .postLogoutRedirectUris(uris -> uris.addAll(
                Optional.ofNullable(request.getPostLogoutRedirectUris()).orElse(Collections.emptyList())))
            // Scopes
            .scopes(scopes -> {
                if (StringUtils.hasText(request.getScopes())) {
                    scopes.addAll(StringUtils.commaDelimitedListToSet(request.getScopes()));
                }
            })
            .build();

        registeredClientRepository.save(registeredClient);

        return mapToResponse(registeredClient, generatedClientSecret);
    }

    private ClientRegistrationResponse findClientByClientId(String clientId) {
        // Assert check for input parameter
        Assert.hasText(clientId, "Client ID must not be empty");

        RegisteredClient registeredClient = registeredClientRepository.findByClientId(clientId);

        if (registeredClient == null) {
            return null;
        }

        return mapToResponse(registeredClient, null);
    }

    private ClientRegistrationResponse mapToResponse(RegisteredClient registeredClient, String generatedSecret) {
        // Use the generated secret only if provided (for registration response), otherwise use null (for retrieval response)
        String clientSecret =
            registeredClient.getClientSecret() != null && registeredClient.getClientSecret().startsWith("{noop}")
                && generatedSecret != null
                ? generatedSecret
                : null;

        return ClientRegistrationResponse.builder()
            .clientId(registeredClient.getClientId())
            .clientSecret(clientSecret)
            .clientName(registeredClient.getClientName())
            .redirectUris(registeredClient.getRedirectUris().stream().toList())
            .grantTypes(
                registeredClient.getAuthorizationGrantTypes().stream().map(AuthorizationGrantType::getValue).toList())
            .scopes(StringUtils.collectionToCommaDelimitedString(registeredClient.getScopes()))
            .tokenEndpointAuthMethod(
                registeredClient.getClientAuthenticationMethods().stream().findFirst()
                    .map(ClientAuthenticationMethod::getValue).orElse(null))
            .clientIdIssuedAt(registeredClient.getClientIdIssuedAt().atZone(ZoneOffset.UTC).toEpochSecond())
            .clientSecretExpiresAt(
                registeredClient.getClientSecretExpiresAt() != null ?
                    registeredClient.getClientSecretExpiresAt().atZone(ZoneOffset.UTC).toEpochSecond() :
                    0L)
            .build();
    }
}
