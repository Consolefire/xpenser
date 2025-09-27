package com.consolefire.xpenser.cloud.auth.data.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.consolefire.xpenser.cloud.auth.data.entity.Client;
import com.consolefire.xpenser.cloud.auth.tc.TestContainerDatabaseInitializer;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@ActiveProfiles({"ComponentTests", "TestContainerTests"})
@TestInstance(Lifecycle.PER_CLASS)
@ContextConfiguration(initializers = TestContainerDatabaseInitializer.class)
@Transactional
public class JpaRegisteredClientRepositoryTest {

    @Autowired
    private RegisteredClientRepository registeredClientRepository;

    @Autowired
    private ClientRepository clientRepository;

    private RegisteredClient initialClient;

    @BeforeEach
    void setUp() {
        initialClient = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId(UUID.randomUUID().toString())
            .clientIdIssuedAt(Instant.now().minusSeconds(3600))
            .clientSecret("{noop}test-secret")
            .clientName("Test Client")
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .redirectUri("https://test.com/callback")
            .postLogoutRedirectUri("https://test.com/logout")
            .scope("openid")
            .scope("api.read")
            .clientSettings(ClientSettings.builder()
                .requireProofKey(true)
                .requireAuthorizationConsent(true)
                .build())
            .tokenSettings(TokenSettings.builder()
                .accessTokenTimeToLive(Duration.ofHours(2))
                .reuseRefreshTokens(false)
                .build())
            .build();

        // Ensure the database is clean before each test (handled by @Transactional, but good practice)
        clientRepository.deleteAll();
    }

    @Test
    void testSaveNewClient() {
        registeredClientRepository.save(initialClient);

        Client entity = clientRepository.findById(initialClient.getId()).orElse(null);

        assertThat(entity).isNotNull();
        assertThat(entity.getClientId()).isEqualTo(initialClient.getClientId());
        assertThat(entity.getClientSecret()).isEqualTo(initialClient.getClientSecret());
        assertThat(entity.getScopes()).contains("openid", "api.read");
        assertThat(entity.getAuthorizationGrantTypes()).contains(
            AuthorizationGrantType.AUTHORIZATION_CODE.getValue(),
            AuthorizationGrantType.REFRESH_TOKEN.getValue()
        );
    }

    @Test
    void testFindById() {
        // 1. Save the client using the underlying JPA repository to ensure serialization happens via the main repository
        registeredClientRepository.save(initialClient);

        // 2. Retrieve using the public method
        RegisteredClient foundClient = registeredClientRepository.findById(initialClient.getId());

        assertThat(foundClient).isNotNull();
        assertThat(foundClient.getId()).isEqualTo(initialClient.getId());
        assertThat(foundClient.getClientId()).isEqualTo(initialClient.getClientId());
        assertThat(foundClient.getScopes()).containsExactlyInAnyOrderElementsOf(initialClient.getScopes());

        // Check deserialization of settings
        assertThat(foundClient.getClientSettings().isRequireProofKey()).isTrue();
        assertThat(foundClient.getTokenSettings().isReuseRefreshTokens()).isFalse();
        assertThat(foundClient.getTokenSettings().getAccessTokenTimeToLive()).isEqualTo(Duration.ofHours(2));
    }

    @Test
    void testFindByClientId() {
        // 1. Save the client
        registeredClientRepository.save(initialClient);

        // 2. Retrieve using the public method
        RegisteredClient foundClient = registeredClientRepository.findByClientId(initialClient.getClientId());

        assertThat(foundClient).isNotNull();
        assertThat(foundClient.getId()).isEqualTo(initialClient.getId());
        assertThat(foundClient.getClientName()).isEqualTo("Test Client");
        assertThat(foundClient.getAuthorizationGrantTypes()).contains(
            AuthorizationGrantType.AUTHORIZATION_CODE,
            AuthorizationGrantType.REFRESH_TOKEN
        );
    }

    @Test
    void testUpdateClient() {
        // 1. Save initial client
        registeredClientRepository.save(initialClient);

        // 2. Modify the client
        RegisteredClient updatedClient = RegisteredClient.from(initialClient)
            .clientSecret("{noop}new-secret")
            .clientName("Updated Test Client")
            .scope("profile") // Add a new scope
            .build();

        // 3. Save the updated client (which should overwrite the existing record)
        registeredClientRepository.save(updatedClient);

        // 4. Verify the update by retrieving it
        RegisteredClient foundClient = registeredClientRepository.findById(initialClient.getId());

        assertThat(foundClient).isNotNull();
        assertThat(foundClient.getClientSecret()).isEqualTo("{noop}new-secret");
        assertThat(foundClient.getClientName()).isEqualTo("Updated Test Client");
        assertThat(foundClient.getScopes()).contains("openid", "api.read", "profile");

        // 5. Verify only one entry exists in the database
        assertThat(clientRepository.count()).isEqualTo(1);
    }
}
