package com.consolefire.xpenser.cloud.auth.api.controller;

import com.consolefire.xpenser.cloud.auth.api.model.ClientRegistrationRequest;
import com.consolefire.xpenser.cloud.auth.api.model.ClientRegistrationResponse;
import com.consolefire.xpenser.cloud.auth.service.AuthServerClientRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/client-registration/clients")
public class ClientRegistrationController {

    private final AuthServerClientRegistrationService authServerClientRegistrationService;

    /**
     * Registers a new OAuth 2.0 Client. Maps to: POST /client-registration/clients (OIDC Dynamic Client Registration
     * endpoint)
     *
     * @param request the client registration details
     * @return a Mono containing the registered client details including the generated client_id and client_secret.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ClientRegistrationResponse> registerClient(@Valid @RequestBody ClientRegistrationRequest request) {
        log.info("Received request to register new client: {}", request.getClientName());
        // NOTE: We assume clientRegistrationService now provides a reactive method.
        return authServerClientRegistrationService.registerNewClientReactive(request);
    }

    /**
     * Retrieves the details of a registered OAuth 2.0 Client. Maps to: GET /client-registration/clients/{clientId}
     * (OIDC Client Read endpoint)
     *
     * @param clientId the unique client ID
     * @return a Mono containing the registered client details in a ResponseEntity (200 OK or 404 Not Found).
     */
    @GetMapping("/{clientId}")
    public Mono<ResponseEntity<ClientRegistrationResponse>> getClientDetails(@PathVariable String clientId) {
        log.info("Received request to retrieve client details for client ID: {}", clientId);

        // NOTE: We assume clientRegistrationService now provides a reactive method
        // which returns Mono<ClientRegistrationResponse> (Mono.empty() if not found).
        return authServerClientRegistrationService.findClientByClientIdReactive(clientId)
            .map(ResponseEntity::ok) // Map the result to 200 OK
            .defaultIfEmpty(
                ResponseEntity.notFound().build()); // If Mono is empty (client not found), return 404 Not Found
    }
}
