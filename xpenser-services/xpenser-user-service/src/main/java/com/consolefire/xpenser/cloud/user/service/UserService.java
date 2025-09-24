package com.consolefire.xpenser.cloud.user.service;

import com.consolefire.xpenser.cloud.user.data.dto.AdminUserUpdateRequestDto;
import com.consolefire.xpenser.cloud.user.data.dto.UserRegistrationRequestDto;
import com.consolefire.xpenser.cloud.user.data.dto.UserResponseDto;
import com.consolefire.xpenser.cloud.user.data.dto.UserUpdateRequestDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<UserResponseDto> createUser(UserRegistrationRequestDto request);

    Mono<UserResponseDto> updateUserData(Long userId, UserUpdateRequestDto request);

    Mono<UserResponseDto> getUserById(Long userId);

    Mono<Void> deleteUser(Long userId);

    Mono<UserResponseDto> updateUserStatus(Long userId, AdminUserUpdateRequestDto request);

    Flux<UserResponseDto> getAllUsers();

}
