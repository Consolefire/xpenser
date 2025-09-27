package com.consolefire.xpenser.cloud.auth.api.controller;

import com.consolefire.xpenser.cloud.auth.api.model.ClientRegistrationRequest;
import com.consolefire.xpenser.cloud.auth.data.repository.ClientRepository;
import com.consolefire.xpenser.cloud.auth.tc.TestContainerDatabaseInitializer;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for ClientRegistrationController using WebTestClient for reactive endpoints. Uses @SpringBootTest
 * to load the full application context including the JPA-backed service.
 */
@AutoConfigureWebTestClient
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"ComponentTests", "TestContainerTests"})
@TestInstance(Lifecycle.PER_CLASS)
@ContextConfiguration(initializers = TestContainerDatabaseInitializer.class)
@Transactional
class ClientRegistrationControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ClientRepository clientRepository;

    private static final String CLIENT_REGISTRATION_URI = "/client-registration/clients";

    @BeforeEach
    void setup() {
        // Ensure no leftover data, although @Transactional should handle rollback
        clientRepository.deleteAll();
    }

    @Test
    void testRegisterClient_Success() {
        ClientRegistrationRequest request = ClientRegistrationRequest.builder()
            .clientName("WebFlux Test App")
            .redirectUris(List.of("http://127.0.0.1:8080/callback"))
            .grantTypes(List.of("authorization_code", "refresh_token"))
            .scopes("openid,profile,email")
            .tokenEndpointAuthMethod("client_secret_basic")
            .build();

        webTestClient.post().uri(CLIENT_REGISTRATION_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            // Expect HTTP 201 CREATED
            .expectStatus().isCreated()
            // Verify core OIDC/OAuth fields are present in the response
            .expectBody()
            .jsonPath("$.client_id").isNotEmpty()
            .jsonPath("$.client_secret").isNotEmpty()
            .jsonPath("$.client_name").isEqualTo("WebFlux Test App")
            .jsonPath("$.grant_types").isArray()
            .consumeWith(entityExchangeResult -> {
                System.out.println(entityExchangeResult);
            })
            .jsonPath("$.scope").isEqualTo("openid,profile,email");
    }

    @Test
    void testRegisterClient_MissingClientName_BadRequest() {
        // clientName is asserted non-empty in the service layer (blocking assertion triggers exception)
        ClientRegistrationRequest request = ClientRegistrationRequest.builder()
            .redirectUris(List.of("http://127.0.0.1:8080/callback"))
            .grantTypes(List.of("authorization_code"))
            .build();

        webTestClient.post().uri(CLIENT_REGISTRATION_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            // Expect HTTP 400 Bad Request due to missing required field validation (Assert.hasText failure in service)
            .expectStatus().isBadRequest();
    }

    @Test
    void testGetClientDetails_Success() {
        // 1. Setup: Register a client first using a blocking call for simplicity in setup
        ClientRegistrationRequest registerRequest = ClientRegistrationRequest.builder()
            .clientName("Test Retriever Client")
            .redirectUris(List.of("http://test.com/callback"))
            .grantTypes(List.of("client_credentials"))
            .scopes("api.read")
            .build();

        // Perform the registration and capture the client_id from the response
        String clientId = webTestClient.post().uri(CLIENT_REGISTRATION_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(registerRequest)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.client_id").isNotEmpty()
            .returnResult().getResponseBodyContent().toString();

        // Extract the client_id string (simplified extraction, assumes JSON structure is verified)
        // Since WebTestClient doesn't expose the ObjectMapper readily, we use a simple regex or parsing on the string content if needed,
        // but for integration testing, relying on the JSON path assertion for the GET request is cleaner.
        // We will execute the registration again and retrieve the ID through a simpler path.
        String actualClientId = webTestClient.post().uri(CLIENT_REGISTRATION_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(registerRequest)
            .exchange()
            .expectStatus().isCreated()
            .expectBody().jsonPath("$.client_id").value((id) -> {
                // 2. Test: Retrieve the registered client using the captured ID
                webTestClient.get().uri(CLIENT_REGISTRATION_URI + "/{clientId}", id)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.client_id").isEqualTo(id)
                    .jsonPath("$.client_name").isEqualTo("Test Retriever Client")
                    .jsonPath("$.grant_types[0]").isEqualTo("client_credentials")
                    .jsonPath("$.scope").isEqualTo("api.read");
            }).returnResult().getResponseBodyContent().toString();
    }

    @Test
    void testGetClientDetails_NotFound() {
        String nonExistentClientId = "non-existent-12345";

        webTestClient.get().uri(CLIENT_REGISTRATION_URI + "/{clientId}", nonExistentClientId)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            // Expect HTTP 404 Not Found
            .expectStatus().isNotFound();
    }
}
