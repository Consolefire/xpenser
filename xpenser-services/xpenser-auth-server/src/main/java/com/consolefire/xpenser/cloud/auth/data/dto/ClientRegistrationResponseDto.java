package com.consolefire.xpenser.cloud.auth.data.dto;

import java.time.Instant;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder // Lombok's @Builder helps create instances easily
public class ClientRegistrationResponseDto {

    private String id; // Internal ID
    private String clientId;
    private String clientSecret; // This will ONLY be set for the initial registration response
    private Instant clientIdIssuedAt;
    private Instant clientSecretExpiresAt; // If your secrets have an expiry
    private Set<String> clientAuthenticationMethods;
    private Set<String> authorizationGrantTypes;
    private Set<String> redirectUris;
    private Set<String> scopes;
    private boolean requireProofKeyForCodeExchange;
    // Add any other relevant client settings you want to expose
}