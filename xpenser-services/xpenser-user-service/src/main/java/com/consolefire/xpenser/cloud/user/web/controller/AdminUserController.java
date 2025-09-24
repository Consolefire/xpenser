package com.consolefire.xpenser.cloud.user.web.controller;

import com.consolefire.xpenser.cloud.user.data.dto.AdminUserUpdateRequestDto;
import com.consolefire.xpenser.cloud.user.data.dto.UserRegistrationRequestDto;
import com.consolefire.xpenser.cloud.user.data.dto.UserResponseDto;
import com.consolefire.xpenser.cloud.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/admin-center/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public Flux<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/onboard")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserResponseDto> onboardUser(@Valid @RequestBody UserRegistrationRequestDto request) {
        return userService.createUser(request);
    }

    @PutMapping("/{id}/status")
    public Mono<UserResponseDto> updateUserStatus(@PathVariable Long id,
        @Valid @RequestBody AdminUserUpdateRequestDto request) {
        return userService.updateUserStatus(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }
}
