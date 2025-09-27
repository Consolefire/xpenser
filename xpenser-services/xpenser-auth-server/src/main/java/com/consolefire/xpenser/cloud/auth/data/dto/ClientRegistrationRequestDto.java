package com.consolefire.xpenser.cloud.auth.data.dto; // Updated package

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientRegistrationRequestDto {

    @NotBlank(message = "Client ID is mandatory")
    private String clientId;

    @NotEmpty(message = "Client authentication methods cannot be empty")
    private Set<String> clientAuthenticationMethods;

    @NotEmpty(message = "Authorization grant types cannot be empty")
    private Set<String> authorizationGrantTypes;

    @NotEmpty(message = "Redirect URIs cannot be empty")
    private Set<String> redirectUris;

    @NotEmpty(message = "Scopes cannot be empty")
    private Set<String> scopes;

    private boolean requireProofKeyForCodeExchange = true;

}