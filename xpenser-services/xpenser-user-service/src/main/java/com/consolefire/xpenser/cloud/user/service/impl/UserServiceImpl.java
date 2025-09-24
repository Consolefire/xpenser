package com.consolefire.xpenser.cloud.user.service.impl;

import com.consolefire.xpenser.cloud.user.data.dto.AdminUserUpdateRequestDto;
import com.consolefire.xpenser.cloud.user.data.dto.UserRegistrationRequestDto;
import com.consolefire.xpenser.cloud.user.data.dto.UserResponseDto;
import com.consolefire.xpenser.cloud.user.data.dto.UserUpdateRequestDto;
import com.consolefire.xpenser.cloud.user.data.entity.User;
import com.consolefire.xpenser.cloud.user.data.mapper.UserMapper;
import com.consolefire.xpenser.cloud.user.data.repository.RoleRepository;
import com.consolefire.xpenser.cloud.user.data.repository.UserRepository;
import com.consolefire.xpenser.cloud.user.service.UserService;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class UserServiceImpl implements UserService, UserDetailsService, ReactiveUserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
        RoleRepository roleRepository,
        UserMapper userMapper,
        PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    // --- UserDetailsService Implementation (Blocking) ---
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        List<com.consolefire.xpenser.cloud.user.data.entity.Role> roles = roleRepository.findRolesByUserId(
            user.getId());
        user.setRoles(new HashSet<>(roles));

        Set<GrantedAuthority> authorities = new HashSet<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
            List<com.consolefire.xpenser.cloud.user.data.entity.Privilege> privileges = roleRepository.findPrivilegesByRoleId(
                role.getId());
            role.setPrivileges(new HashSet<>(privileges));
            privileges.forEach(privilege -> authorities.add(new SimpleGrantedAuthority(privilege.getName())));
        });

        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            user.isEnabled(),
            user.isAccountNonExpired(),
            user.isCredentialsNonExpired(),
            user.isAccountNonLocked(),
            authorities
        );
    }

    // --- New Reactive UserDetails Loading Method ---
    // This method is now part of your service for reactive consumers
    @Transactional(readOnly = true) // Apply transactional as it's a read operation
    public Mono<UserDetails> findByUsername(String username) {
        // Wrap the blocking loadUserByUsername call in a Mono and offload to boundedElastic scheduler.
        // This makes the blocking UserDetailsService.loadUserByUsername available reactively.
        return Mono.defer(() -> Mono.fromCallable(() -> loadUserByUsername(username)))
            .subscribeOn(Schedulers.boundedElastic())
            .onErrorMap(UsernameNotFoundException.class,
                ex -> new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage()));
        // Convert Spring Security's specific exception to a WebFlux friendly one
    }

    // --- Existing UserService Interface Implementations (from previous responses) ---

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Mono<UserResponseDto> createUser(UserRegistrationRequestDto request) {
        return Mono.defer(() -> Mono.fromCallable(() -> {
                if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
                }
                if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
                }
                return roleRepository.findByName("ROLE_USER")
                    .orElseThrow(
                        () -> new IllegalStateException("Default role 'ROLE_USER' not found. Please ensure it's seeded."));
            }))
            .flatMap(defaultRole -> Mono.fromCallable(() -> {
                User newUser = userMapper.toEntity(request);
                newUser.setPassword(passwordEncoder.encode(request.getPassword()));
                newUser.setRoles(Collections.singleton(defaultRole));
                newUser.setCreatedAt(Instant.now());
                newUser.setUpdatedAt(Instant.now());
                return userRepository.save(newUser);
            }))
            .flatMap(this::fetchUserWithRolesAndPrivileges)
            .map(userMapper::toDto)
            .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional
    public Mono<UserResponseDto> updateUserData(Long userId, UserUpdateRequestDto request) {
        return Mono.defer(() -> Mono.fromCallable(() -> {
                User existingUser = userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

                existingUser.setFirstName(request.getFirstName());
                existingUser.setLastName(request.getLastName());
                existingUser.setUpdatedAt(Instant.now());

                if (request.getEmail() != null && !request.getEmail().equals(existingUser.getEmail())) {
                    userRepository.findByEmail(request.getEmail()).ifPresent(u -> {
                        if (!u.getId().equals(userId)) {
                            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
                        }
                    });
                    existingUser.setEmail(request.getEmail());
                }

                return userRepository.save(existingUser);
            }))
            .flatMap(this::fetchUserWithRolesAndPrivileges)
            .map(userMapper::toDto)
            .subscribeOn(Schedulers.boundedElastic());
    }


    @Override
    @Transactional(readOnly = true)
    public Mono<UserResponseDto> getUserById(Long userId) {
        return Mono.defer(() -> Mono.fromCallable(() ->
                userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"))
            ))
            .flatMap(this::fetchUserWithRolesAndPrivileges)
            .map(userMapper::toDto)
            .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional
    public Mono<Void> deleteUser(Long userId) {
        return Mono.defer(() -> Mono.fromRunnable(() -> {
                if (!userRepository.existsById(userId)) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
                }
                userRepository.deleteById(userId);
            }))
            .then()
            .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional
    public Mono<UserResponseDto> updateUserStatus(Long userId, AdminUserUpdateRequestDto request) {
        return Mono.defer(() -> Mono.fromCallable(() -> {
                User existingUser = userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

                if (request.getEnabled() != null) {
                    existingUser.setEnabled(request.getEnabled());
                }
                if (request.getAccountNonExpired() != null) {
                    existingUser.setAccountNonExpired(request.getAccountNonExpired());
                }
                if (request.getCredentialsNonExpired() != null) {
                    existingUser.setCredentialsNonExpired(request.getCredentialsNonExpired());
                }
                if (request.getAccountNonLocked() != null) {
                    existingUser.setAccountNonLocked(request.getAccountNonLocked());
                }

                if (request.getRoles() != null && !request.getRoles().isEmpty()) {
                    Set<com.consolefire.xpenser.cloud.user.data.entity.Role> newRoles = new HashSet<>();
                    for (String roleName : request.getRoles()) {
                        newRoles.add(roleRepository.findByName(roleName)
                            .orElseThrow(
                                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found: " + roleName)));
                    }
                    existingUser.setRoles(newRoles);
                }

                existingUser.setUpdatedAt(Instant.now());
                return userRepository.save(existingUser);
            }))
            .flatMap(this::fetchUserWithRolesAndPrivileges)
            .map(userMapper::toDto)
            .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<UserResponseDto> getAllUsers() {
        return Flux.defer(() -> Flux.fromIterable(userRepository.findAll()))
            .flatMap(this::fetchUserWithRolesAndPrivileges)
            .map(userMapper::toDto)
            .subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<User> fetchUserWithRolesAndPrivileges(User user) {
        return Mono.defer(() -> Mono.just(user))
            .flatMap(u -> Mono.fromCallable(() -> {
                Set<com.consolefire.xpenser.cloud.user.data.entity.Role> roles = new HashSet<>(
                    roleRepository.findRolesByUserId(u.getId()));
                u.setRoles(roles);

                roles.forEach(role -> {
                    List<com.consolefire.xpenser.cloud.user.data.entity.Privilege> privileges = roleRepository.findPrivilegesByRoleId(
                        role.getId());
                    role.setPrivileges(new HashSet<>(privileges));
                });
                return u;
            }))
            .subscribeOn(Schedulers.boundedElastic());
    }
}