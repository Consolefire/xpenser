package com.consolefire.xpenser.cloud.auth.service;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class RemoteReactiveUserDetailsService implements ReactiveUserDetailsService {

    private final String userServiceBaseUrl;
    private final String userServiceGatewayPathPrefix;
    private final WebClient webClient;

    public RemoteReactiveUserDetailsService(
        @Value("${gateway.services.user-service.base-url}")
        String userServiceBaseUrl,
        @Value("${gateway.services.user-service.path-prefix}")
        String userServiceGatewayPathPrefix) {
        this.userServiceBaseUrl = userServiceBaseUrl;
        this.userServiceGatewayPathPrefix = userServiceGatewayPathPrefix;
        this.webClient = WebClient.builder()
            .baseUrl(userServiceBaseUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }


    @Override
    public Mono<UserDetails> findByUsername(final String username) {
        return webClient.get()
            .uri(userServiceGatewayPathPrefix + "/{username}", username) // Use the path prefix from YAML
            .retrieve()
            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                clientResponse -> Mono.error(new RuntimeException(
                    "Error fetching user details from remote user-service: " + clientResponse.statusCode()
                        + " for user: " + username))
            )
            .bodyToMono(Map.class)
            .map(userMap -> {
                String fetchedUsername = (String) userMap.get("username");
                String fetchedPassword = (String) userMap.get("password");
                String rolesString = (String) userMap.get("roles");

                if (fetchedUsername == null || fetchedPassword == null || rolesString == null) {
                    throw new IllegalArgumentException(
                        "Invalid user details received from user-service via gateway for username: " + username);
                }

                return User.withUsername(fetchedUsername)
                    .password(fetchedPassword)
                    .roles(rolesString.split(","))
                    .build();
            })
            .switchIfEmpty(Mono.error(new RuntimeException("User not found via gateway: " + username)));
    }
}
