package com.consolefire.xpenser.cloud.user.service.impl;

import com.consolefire.xpenser.cloud.user.data.dto.AdminUserUpdateRequestDto;
import com.consolefire.xpenser.cloud.user.data.dto.UserRegistrationRequestDto;
import com.consolefire.xpenser.cloud.user.data.dto.UserResponseDto;
import com.consolefire.xpenser.cloud.user.data.dto.UserUpdateRequestDto;
import com.consolefire.xpenser.cloud.user.data.entity.Role;
import com.consolefire.xpenser.cloud.user.data.entity.User;
import com.consolefire.xpenser.cloud.user.data.mapper.UserMapper;
import com.consolefire.xpenser.cloud.user.data.repository.RoleRepository;
import com.consolefire.xpenser.cloud.user.data.repository.UserRepository;
import com.consolefire.xpenser.cloud.user.service.UserService;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, ReactiveUserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<UserResponseDto> registerUser(UserRegistrationRequestDto request) {
        return userRepository.findByUsername(request.getUsername())
            .flatMap(u -> Mono.<UserResponseDto>error(new ResponseStatusException(HttpStatus.CONFLICT,
                "Username already exists"))) // Explicitly cast the error Mono's type
            .switchIfEmpty(Mono.defer(() -> userRepository.findByEmail(request.getEmail())
                    .flatMap(u -> Mono.<UserResponseDto>error(
                        new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists"))))
                // Explicitly cast the error Mono's type
            )
            .switchIfEmpty(Mono.defer(() ->
                roleRepository.findByName("ROLE_USER")
                    .switchIfEmpty(Mono.<Role>error( // Cast this error Mono as well
                        new IllegalStateException("Default role 'ROLE_USER' not found. Please ensure it's seeded.")))
                    .flatMap(defaultRole -> {
                        User newUser = userMapper.toEntity(request);
                        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
                        newUser.setRoles(Collections.singleton(defaultRole));
                        newUser.setCreatedAt(Instant.now());
                        newUser.setUpdatedAt(Instant.now());
                        return userRepository.save(newUser)
                            .flatMap(this::fetchUserWithRolesAndPrivileges) // Returns Mono<User>
                            .map(userMapper::toDto); // Returns Mono<UserResponseDto>
                    })
            ));
    }


    @Override
    @PreAuthorize("#userId == authentication.principal.id")
    public Mono<UserResponseDto> updateUserData(Long userId, UserUpdateRequestDto request) {
        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")))
            .flatMap(user -> {
                // This variable will hold the Mono<User> result of the email uniqueness check and update/save flow
                Mono<User> updateAndSaveMono;

                if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
                    // If email is changing, check for uniqueness in a reactive chain
                    updateAndSaveMono = userRepository.findByEmail(request.getEmail())
                        // If an existing user is found with the new email, throw a conflict error
                        .flatMap(existingUser -> Mono.<User>error( // <-- FIX: Explicitly cast Mono.error to Mono<User>
                            new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use")))
                        // If findByEmail returns empty (email is unique), proceed with the update and save
                        .switchIfEmpty(Mono.defer(() -> {
                            userMapper.updateFromDto(request, user); // Apply updates from DTO to entity
                            user.setUpdatedAt(Instant.now()); // Update timestamp
                            return userRepository.save(user); // Save the updated user
                        }));
                } else {
                    // If email is not changing or is null, just proceed with updating other fields and saving
                    userMapper.updateFromDto(request, user); // Apply updates from DTO to entity
                    user.setUpdatedAt(Instant.now()); // Update timestamp
                    updateAndSaveMono = userRepository.save(user); // Save the user
                }
                return updateAndSaveMono; // Return the result of the chosen update/save path
            })
            .flatMap(
                this::fetchUserWithRolesAndPrivileges) // After saving, fetch full user data including roles/privileges
            .map(userMapper::toDto); // Map the fully loaded User entity to a UserResponseDto
    }

    @Override
    public Mono<UserResponseDto> getUserById(Long userId) {
        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")))
            .flatMap(this::fetchUserWithRolesAndPrivileges)
            .map(userMapper::toDto);
    }

    // --- ReactiveUserDetailsService Implementation ---
    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username)
            .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found: " + username)))
            .flatMap(this::fetchUserWithRolesAndPrivileges) // Fetch roles and privileges here
            .cast(UserDetails.class); // Cast to UserDetails
    }

    // --- Admin Service Functions ---

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<Void> deleteUser(Long userId) {
        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")))
            .flatMap(user -> {
                if (user.getRoles().stream().anyMatch(role -> "ROLE_ADMIN".equals(role.getName()))) {
                    return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Cannot delete an ADMIN user via this endpoint."));
                }
                return userRepository.delete(user);
            });
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<UserResponseDto> updateUserStatus(Long userId, AdminUserUpdateRequestDto request) {
        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")))
            .flatMap(user -> {
                if (user.getRoles().stream().anyMatch(role -> "ROLE_ADMIN".equals(role.getName())) &&
                    (Boolean.FALSE.equals(request.getEnabled()) || Boolean.FALSE.equals(
                        request.getAccountNonLocked()))) {
                    return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Cannot deactivate or lock an ADMIN user via this endpoint."));
                }

                user.setEnabled(request.getEnabled());
                user.setAccountNonExpired(request.getAccountNonExpired());
                user.setCredentialsNonExpired(request.getCredentialsNonExpired());
                user.setAccountNonLocked(request.getAccountNonLocked());
                user.setUpdatedAt(Instant.now());
                return userRepository.save(user);
            })
            .flatMap(this::fetchUserWithRolesAndPrivileges)
            .map(userMapper::toDto);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Flux<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
            .flatMap(this::fetchUserWithRolesAndPrivileges) // Fetch roles and privileges for each user
            .map(o -> {
                System.out.println("%%%%% --- " + o);
                return o;
            })
            .map(userMapper::toDto);
    }

    // --- Helper method to fetch roles and privileges for a User entity ---
    private Mono<User> fetchUserWithRolesAndPrivileges(User user) {
        return Mono.just(user)
            .flatMap(u -> {
                // Fetch roles and privileges
                return roleRepository.findRolesByUserId(u.getId())
                    .collectList()
                    .flatMap(roles -> {
                        u.setRoles(new HashSet<>(roles));
                        return Flux.fromIterable(roles)
                            .flatMap(r -> roleRepository.findPrivilegesByRoleId(r.getId()))
                            .collectList();
                    })
                    .map(privileges -> {
                        // Assume User has a setPrivileges method, or add to a dummy roleset
                        u.getRoles().forEach(role -> role.setPrivileges(
                            new HashSet<>(privileges))); // Simplified, real logic might be more nuanced
                        return u;
                    });
            });
    }
}
