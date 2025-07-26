package com.consolefire.xpenser.cloud.user.web.controller;

import com.consolefire.xpenser.cloud.user.data.dto.UserResponseDto;
import com.consolefire.xpenser.cloud.user.data.dto.UserUpdateRequestDto;
import com.consolefire.xpenser.cloud.user.data.entity.User;
import com.consolefire.xpenser.cloud.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping
    public Mono<UserResponseDto> updateMyData(@AuthenticationPrincipal Mono<User> currentUserMono,
        @Valid @RequestBody UserUpdateRequestDto request) {
        return currentUserMono.flatMap(currentUser -> userService.updateUserData(currentUser.getId(), request));
    }

    @GetMapping
    public Mono<UserResponseDto> getMyData(@AuthenticationPrincipal Mono<User> currentUserMono) {
        return currentUserMono.flatMap(currentUser -> userService.getUserById(currentUser.getId()));
    }

}
