package com.consolefire.xpenser.cloud.auth.service;

import com.consolefire.xpenser.cloud.auth.config.AuthServerConfiguration;
import com.consolefire.xpenser.cloud.auth.tc.TestContainerDatabaseInitializer;
import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ActiveProfiles({"ComponentTests", "TestContainerTests"})
@TestInstance(Lifecycle.PER_CLASS)
@ContextConfiguration(initializers = TestContainerDatabaseInitializer.class)
@SpringBootTest(classes = {RemoteReactiveUserDetailsService.class})
@TestPropertySource(properties = {
    "gateway.services.user-service.path-prefix=/api/v1/users"
})
class RemoteReactiveUserDetailsServiceTest {

    private static MockWebServer staticMockWebServer;


    // Static block guarantees the MockWebServer starts before the Spring Context
    // executes @DynamicPropertySource, solving the NullPointerException.
    static {
        try {
            staticMockWebServer = new MockWebServer();
            staticMockWebServer.start();
        } catch (IOException e) {
            throw new RuntimeException("Failed to start MockWebServer", e);
        }
    }

    @AfterAll
    static void tearDown() throws Exception {
        // Shut down the mock server after all tests are run
        if (staticMockWebServer != null) {
            staticMockWebServer.shutdown();
        }
    }

    /**
     * Overrides the 'gateway.services.user-service.base-url' property with the dynamically assigned URL of the running
     * MockWebServer. This resolves the UnknownHostException issue.
     */
    @DynamicPropertySource
    static void registerMockUserServer(DynamicPropertyRegistry registry) {
        // Set the property in the Spring context to the MockWebServer's dynamic URL
        registry.add("gateway.services.user-service.base-url",
            () -> staticMockWebServer.url("/").toString());
    }

    @Autowired
    private ReactiveUserDetailsService userDetailsService;

    // --- Tests ---

    @Test
    void testFindUserByUsername_Success() {
        String username = "testuser";
        String encodedPassword = "{bcrypt}someEncodedHash";
        String roles = "ADMIN,USER";

        // 1. Enqueue a successful response from the mocked user-service
        String mockResponseBody = String.format("{\"username\":\"%s\", \"password\":\"%s\", \"roles\":\"%s\"}",
            username, encodedPassword, roles);

        staticMockWebServer.enqueue(new MockResponse()
            .setResponseCode(HttpStatus.OK.value())
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(mockResponseBody));

        Mono<UserDetails> userDetailsMono = userDetailsService.findByUsername(username);

        // 2. Verify the emitted UserDetails object
        StepVerifier.create(userDetailsMono)
            .expectNextMatches(userDetails ->
                userDetails.getUsername().equals(username) &&
                    userDetails.getPassword().equals(encodedPassword) &&
                    userDetails.getAuthorities().size() == 2
            )
            .verifyComplete();
    }

    @Test
    void testFindUserByUsername_UserNotFound_ThrowsRuntimeException() {
        String username = "missinguser";

        // 1. Enqueue a 404 NOT FOUND response
        staticMockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.NOT_FOUND.value()));

        Mono<UserDetails> userDetailsMono = userDetailsService.findByUsername(username);

        // 2. Verify that the Mono results in a RuntimeException (as defined by onStatus mapping in the configuration)
        StepVerifier.create(userDetailsMono)
            .expectErrorSatisfies(throwable -> {
                assert throwable instanceof RuntimeException;
                // Check the error message contains the status code
                assert throwable.getMessage()
                    .contains("Error fetching user details from remote user-service: 404 NOT_FOUND");
            })
            .verify();
    }

    @Test
    void testFindUserByUsername_InvalidPayload_ThrowsIllegalArgumentException() {
        String username = "invalidpayload";

        // 1. Enqueue a successful 200 response with missing mandatory fields (e.g., missing "roles")
        String mockResponseBody = String.format("{\"username\":\"%s\", \"password\":\"%s\"}",
            username, "password");

        staticMockWebServer.enqueue(new MockResponse()
            .setResponseCode(HttpStatus.OK.value())
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(mockResponseBody));

        Mono<UserDetails> userDetailsMono = userDetailsService.findByUsername(username);

        // 2. Verify that the Mono results in the custom IllegalArgumentException due to missing data map check
        StepVerifier.create(userDetailsMono)
            .expectErrorSatisfies(throwable -> {
                assert throwable instanceof IllegalArgumentException;
                assert throwable.getMessage()
                    .contains("Invalid user details received from user-service via gateway for username: " + username);
            })
            .verify();
    }
}