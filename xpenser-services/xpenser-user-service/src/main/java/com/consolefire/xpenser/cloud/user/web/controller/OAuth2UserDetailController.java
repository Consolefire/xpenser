package com.consolefire.xpenser.cloud.user.web.controller;

import com.consolefire.xpenser.cloud.user.data.entity.User;
import com.consolefire.xpenser.cloud.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth2/users/{username}")
public class OAuth2UserDetailController {

    private final UserService userService;

    public Mono<User> fetchUserDetails(
        @PathVariable(name = "username") String username) {
        return userService.getUserByUsername(username);

    }

}
