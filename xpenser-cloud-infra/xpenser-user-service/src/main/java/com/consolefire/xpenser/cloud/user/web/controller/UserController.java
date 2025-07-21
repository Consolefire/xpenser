package com.consolefire.xpenser.cloud.user.web.controller;

import com.consolefire.xpenser.cloud.user.data.dto.UserRegistrationRequestDto;
import com.consolefire.xpenser.cloud.user.data.dto.UserResponseDto;
import com.consolefire.xpenser.cloud.user.data.dto.UserUpdateRequestDto;
import com.consolefire.xpenser.cloud.user.data.entity.User;
import com.consolefire.xpenser.cloud.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserResponseDto> registerUser(@Valid @RequestBody UserRegistrationRequestDto request) {
        return userService.registerUser(request);
    }

    @PutMapping("/me")
    public Mono<UserResponseDto> updateMyData(@AuthenticationPrincipal Mono<User> currentUserMono,
        @Valid @RequestBody UserUpdateRequestDto request) {
        return currentUserMono.flatMap(currentUser -> userService.updateUserData(currentUser.getId(), request));
    }

    @GetMapping("/me")
    public Mono<UserResponseDto> getMyData(@AuthenticationPrincipal Mono<User> currentUserMono) {
        return currentUserMono.flatMap(currentUser -> userService.getUserById(currentUser.getId()));
    }

}
