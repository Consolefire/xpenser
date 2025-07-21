package com.consolefire.xpenser.cloud.user.service.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.consolefire.xpenser.cloud.user.data.dto.AdminUserUpdateRequestDto;
import com.consolefire.xpenser.cloud.user.data.dto.UserRegistrationRequestDto;
import com.consolefire.xpenser.cloud.user.data.dto.UserResponseDto;
import com.consolefire.xpenser.cloud.user.data.dto.UserUpdateRequestDto;
import com.consolefire.xpenser.cloud.user.data.entity.Privilege;
import com.consolefire.xpenser.cloud.user.data.entity.Role;
import com.consolefire.xpenser.cloud.user.data.entity.User;
import com.consolefire.xpenser.cloud.user.data.mapper.UserMapper;
import com.consolefire.xpenser.cloud.user.data.repository.RoleRepository;
import com.consolefire.xpenser.cloud.user.data.repository.UserRepository;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        reset(userRepository, roleRepository, userMapper, passwordEncoder);
        // Important: Use Mockito.lenient() if you expect some mocks might not be strictly used in all code paths,
        // though it's better to make tests precise. For now, let's aim for precise tests.
    }

    // Helper method to create common User/Role/Privilege mocks for the
    // fetchUserWithRolesAndPrivileges internal method of UserServiceImpl.
    // This helper is called when the internal service logic *needs* to fetch roles/privileges.
    private void mockUserRolesAndPrivileges(User user, Role role, Set<Privilege> privileges) {
        // Ensure user ID is not null for these mocks if the service relies on it
        if (user.getId() != null) {
            when(roleRepository.findRolesByUserId(user.getId())).thenReturn(Flux.just(role));
            when(roleRepository.findPrivilegesByRoleId(role.getId())).thenReturn(Flux.fromIterable(privileges));
        } else {
            // Handle case where user ID might be null (e.g., before save in registerUser)
            // Or, more accurately, don't call this helper if the service hasn't saved the user yet.
            // The service's fetchUserWithRolesAndPrivileges would likely only be called on an existing user.
        }
    }

    // --- Register User Tests ---
    @Test
    @DisplayName("Should register a new user successfully with default role")
    void registerUser_success() {
        Long newUserId = 3L;
        Privilege readPrivilege = Privilege.builder().id(1L).name("READ_PRIVILEGE").build();
        Role roleUser = Role.builder().id(100L).name("ROLE_USER")
            .privileges(new HashSet<>(Collections.singletonList(readPrivilege))).build();

        UserRegistrationRequestDto requestDto = UserRegistrationRequestDto.builder()
            .username("newuser")
            .password("rawPassword")
            .email("new@example.com")
            .firstName("New")
            .lastName("User")
            .build();

        // The entity before saving (ID is null)
        User newUserEntity = User.builder()
            .username("newuser")
            .password("encodedRawPassword")
            .email("new@example.com")
            .firstName("New")
            .lastName("User")
            .build();

        // The entity returned after saving (ID is set)
        Instant now = Instant.now();
        User savedUserEntity = User.builder()
            .id(newUserId)
            .username("newuser")
            .password("encodedRawPassword")
            .email("new@example.com")
            .firstName("New")
            .lastName("User")
            .roles(new HashSet<>(Collections.singletonList(roleUser))) // Assuming service adds role here
            .enabled(true).accountNonExpired(true).credentialsNonExpired(true).accountNonLocked(true) // Default status
            .createdAt(now)
            .updatedAt(now)
            .build();

        // Expected DTO after mapping
        UserResponseDto expectedDto = UserResponseDto.builder()
            .id(savedUserEntity.getId())
            .username(savedUserEntity.getUsername())
            .email(savedUserEntity.getEmail())
            .firstName(savedUserEntity.getFirstName())
            .lastName(savedUserEntity.getLastName())
            .enabled(savedUserEntity.isEnabled())
            .accountNonExpired(savedUserEntity.isAccountNonExpired())
            .credentialsNonExpired(savedUserEntity.isCredentialsNonExpired())
            .accountNonLocked(savedUserEntity.isAccountNonLocked())
            .createdAt(savedUserEntity.getCreatedAt())
            .updatedAt(savedUserEntity.getUpdatedAt())
            .roles(Set.of("ROLE_USER"))
            .privileges(Set.of("READ_PRIVILEGE"))
            .build();

        // Configure Mocks
        when(userRepository.findByUsername(requestDto.getUsername())).thenReturn(Mono.empty());
        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(
            Mono.empty()); // Fix for NullPointerException
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Mono.just(roleUser));
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encodedRawPassword");
        when(userMapper.toEntity(requestDto)).thenReturn(newUserEntity);

        // Mock the save operation. The savedUserEntity includes the assigned role.
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUserEntity));

        // Mock roles and privileges for the *saved* user, as fetchUserWithRolesAndPrivileges will be called after save
        mockUserRolesAndPrivileges(savedUserEntity, roleUser, new HashSet<>(Collections.singletonList(readPrivilege)));

        // Mock the final mapping to DTO. This is crucial for the ClassCastException fix.
        // Ensure that when userMapper.toDto is called with the *enriched* User entity, it returns the expected DTO.
        when(userMapper.toDto(any(User.class))).thenReturn(expectedDto);

        StepVerifier.create(userService.registerUser(requestDto))
            .expectNextMatches(response -> {
                assertThat(response.getUsername(), is(requestDto.getUsername()));
                assertThat(response.getEmail(), is(requestDto.getEmail()));
                assertThat(response.getRoles(), hasItem("ROLE_USER"));
                assertThat(response.getPrivileges(), hasItem("READ_PRIVILEGE"));
                assertThat(response.getId(), is(notNullValue()));
                return true;
            })
            .verifyComplete();

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode(requestDto.getPassword());
        verify(userMapper).toEntity(requestDto);
        verify(userMapper).toDto(any(User.class)); // Verify this was called
    }

    @Test
    @DisplayName("Should throw conflict exception if username already exists during registration")
    void registerUser_usernameConflict() {
        UserRegistrationRequestDto requestDto = UserRegistrationRequestDto.builder()
            .username("testuser")
            .password("rawPassword")
            .email("unique@example.com")
            .firstName("New")
            .lastName("User")
            .build();
        User existingUser = User.builder().id(1L).username("testuser").email("unique@example.com").build();

        // Configure Mocks
        when(userRepository.findByUsername(requestDto.getUsername())).thenReturn(Mono.just(existingUser));
//        when(userRepository.findByEmail(anyString())).thenReturn(
//            Mono.empty()); // Added this mock to prevent NPE for findByEmail

        StepVerifier.create(userService.registerUser(requestDto))
            .expectErrorMatches(e -> e instanceof ResponseStatusException
                && ((ResponseStatusException) e).getStatusCode().value() == 409)
            .verify();

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw conflict exception if email already exists during registration")
    void registerUser_emailConflict() {
        UserRegistrationRequestDto requestDto = UserRegistrationRequestDto.builder()
            .username("uniqueuser")
            .password("rawPassword")
            .email("test@example.com")
            .firstName("New")
            .lastName("User")
            .build();
        User existingUser = User.builder().id(1L).username("uniqueuser").email("test@example.com").build();

        // Configure Mocks
        when(userRepository.findByUsername(requestDto.getUsername())).thenReturn(Mono.empty());
        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Mono.just(existingUser));

        StepVerifier.create(userService.registerUser(requestDto))
            .expectErrorMatches(e -> e instanceof ResponseStatusException
                && ((ResponseStatusException) e).getStatusCode().value() == 409)
            .verify();

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw illegal state exception if default ROLE_USER not found during registration")
    void registerUser_defaultRoleNotFound() {
        UserRegistrationRequestDto requestDto = UserRegistrationRequestDto.builder()
            .username("newuser")
            .password("rawPassword")
            .email("new@example.com")
            .firstName("New")
            .lastName("User")
            .build();

        // Configure Mocks
        when(userRepository.findByUsername(requestDto.getUsername())).thenReturn(Mono.empty());
        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Mono.empty());
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Mono.empty());

        StepVerifier.create(userService.registerUser(requestDto))
            .expectErrorMatches(e -> e instanceof IllegalStateException && e.getMessage()
                .contains("Default role 'ROLE_USER' not found"))
            .verify();

        verify(userRepository, never()).save(any(User.class));
    }

    // --- Get User By ID Tests ---
    @Test
    @DisplayName("Should retrieve a user by ID successfully")
    void getUserById_success() {
        Long userId = 1L;
        Privilege readPrivilege = Privilege.builder().id(1L).name("READ_PRIVILEGE").build();
        Role roleUser = Role.builder().id(100L).name("ROLE_USER")
            .privileges(new HashSet<>(Collections.singletonList(readPrivilege))).build();
        User testUser = User.builder()
            .id(userId)
            .username("testuser")
            .password("encodedPassword")
            .email("test@example.com")
            .firstName("Test")
            .lastName("User")
            .roles(new HashSet<>(Collections.singletonList(roleUser)))
            .createdAt(Instant.parse("2023-01-01T10:00:00Z"))
            .updatedAt(Instant.parse("2023-01-01T10:00:00Z"))
            .enabled(true).accountNonExpired(true).credentialsNonExpired(true).accountNonLocked(true) // Default status
            .build();

        UserResponseDto testUserResponseDto = UserResponseDto.builder()
            .id(testUser.getId())
            .username(testUser.getUsername())
            .email(testUser.getEmail())
            .firstName(testUser.getFirstName())
            .lastName(testUser.getLastName())
            .enabled(testUser.isEnabled())
            .accountNonExpired(testUser.isAccountNonExpired())
            .credentialsNonExpired(testUser.isCredentialsNonExpired())
            .accountNonLocked(testUser.isAccountNonLocked())
            .createdAt(testUser.getCreatedAt())
            .updatedAt(testUser.getUpdatedAt())
            .roles(Set.of("ROLE_USER"))
            .privileges(Set.of("READ_PRIVILEGE"))
            .build();

        // Configure Mocks
        when(userRepository.findById(userId)).thenReturn(Mono.just(testUser));
        mockUserRolesAndPrivileges(testUser, roleUser, new HashSet<>(Collections.singletonList(readPrivilege)));
        when(userMapper.toDto(any(User.class))).thenReturn(testUserResponseDto);

        StepVerifier.create(userService.getUserById(userId))
            .expectNextMatches(response -> {
                assertThat(response.getUsername(), is(testUser.getUsername()));
                assertThat(response.getEmail(), is(testUser.getEmail()));
                assertThat(response.getId(), is(testUser.getId()));
                assertThat(response.getRoles(), hasItem("ROLE_USER"));
                return true;
            })
            .verifyComplete();
    }

    @Test
    @DisplayName("Should throw not found exception when user by ID does not exist")
    void getUserById_notFound() {
        // Configure Mocks
        when(userRepository.findById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(userService.getUserById(99L))
            .expectErrorMatches(e -> e instanceof ResponseStatusException
                && ((ResponseStatusException) e).getStatusCode().value() == 404)
            .verify();
    }

    // --- Update User Data Tests ---
    @Test
    @DisplayName("Should update user data successfully")
    void updateUserData_success() {
        Long userId = 1L;
        Privilege readPrivilege = Privilege.builder().id(1L).name("READ_PRIVILEGE").build();
        Role roleUser = Role.builder().id(100L).name("ROLE_USER")
            .privileges(new HashSet<>(Collections.singletonList(readPrivilege))).build();
        User testUser = User.builder()
            .id(userId)
            .username("testuser")
            .password("encodedPassword")
            .email("test@example.com")
            .firstName("Test")
            .lastName("User")
            .roles(new HashSet<>(Collections.singletonList(roleUser)))
            .createdAt(Instant.parse("2023-01-01T10:00:00Z"))
            .updatedAt(Instant.parse("2023-01-01T10:00:00Z"))
            .enabled(true).accountNonExpired(true).credentialsNonExpired(true).accountNonLocked(true) // Default status
            .build();

        UserUpdateRequestDto updateDto = UserUpdateRequestDto.builder()
            .firstName("Updated First")
            .lastName("Updated Last")
            .build();

        // Capture Instant.now() once for consistent comparison
        Instant updateTime = Instant.now();

        User updatedUserEntity = User.builder()
            .id(testUser.getId())
            .username(testUser.getUsername())
            .password(testUser.getPassword())
            .email(testUser.getEmail())
            .firstName(updateDto.getFirstName())
            .lastName(updateDto.getLastName())
            .roles(testUser.getRoles())
            .enabled(testUser.isEnabled()).accountNonExpired(testUser.isAccountNonExpired())
            .credentialsNonExpired(testUser.isCredentialsNonExpired())
            .accountNonLocked(testUser.isAccountNonLocked()) // Copy current status
            .createdAt(testUser.getCreatedAt())
            .updatedAt(updateTime) // Use captured time
            .build();

        UserResponseDto expectedDto = UserResponseDto.builder()
            .id(updatedUserEntity.getId())
            .username(updatedUserEntity.getUsername())
            .email(updatedUserEntity.getEmail())
            .firstName(updatedUserEntity.getFirstName())
            .lastName(updatedUserEntity.getLastName())
            .enabled(updatedUserEntity.isEnabled())
            .accountNonExpired(updatedUserEntity.isAccountNonExpired())
            .credentialsNonExpired(updatedUserEntity.isCredentialsNonExpired())
            .accountNonLocked(updatedUserEntity.isAccountNonLocked())
            .createdAt(updatedUserEntity.getCreatedAt())
            .updatedAt(updatedUserEntity.getUpdatedAt())
            .roles(Set.of("ROLE_USER"))
            .privileges(Set.of("READ_PRIVILEGE"))
            .build();

        // Configure Mocks
        when(userRepository.findById(userId)).thenReturn(Mono.just(testUser));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(updatedUserEntity));
        doAnswer(invocation -> { // Mocking void method for MapStruct
            User targetUser = invocation.getArgument(1);
            UserUpdateRequestDto sourceDto = invocation.getArgument(0);
            if (sourceDto.getFirstName() != null) {
                targetUser.setFirstName(sourceDto.getFirstName());
            }
            if (sourceDto.getLastName() != null) {
                targetUser.setLastName(sourceDto.getLastName());
            }
            return null;
        }).when(userMapper).updateFromDto(any(UserUpdateRequestDto.class), any(User.class));
        mockUserRolesAndPrivileges(updatedUserEntity, roleUser,
            new HashSet<>(Collections.singletonList(readPrivilege)));
        when(userMapper.toDto(any(User.class))).thenReturn(expectedDto);

        StepVerifier.create(userService.updateUserData(userId, updateDto))
            .expectNextMatches(response -> {
                assertThat(response.getFirstName(), is(updateDto.getFirstName()));
                assertThat(response.getLastName(), is(updateDto.getLastName()));
                assertThat(response.getUpdatedAt(), is(updateTime)); // Directly compare against captured time
                return true;
            })
            .verifyComplete();

        verify(userRepository).save(argThat(user ->
            user.getFirstName().equals(updateDto.getFirstName()) &&
                user.getLastName().equals(updateDto.getLastName())
        ));
    }

    @Test
    @DisplayName("Should update user data and email successfully when email is unique")
    void updateUserData_emailChange_success() {
        Long userId = 1L;
        Privilege readPrivilege = Privilege.builder().id(1L).name("READ_PRIVILEGE").build();
        Role roleUser = Role.builder().id(100L).name("ROLE_USER")
            .privileges(new HashSet<>(Collections.singletonList(readPrivilege))).build();
        User testUser = User.builder()
            .id(userId)
            .username("testuser")
            .password("encodedPassword")
            .email("test@example.com")
            .firstName("Test")
            .lastName("User")
            .roles(new HashSet<>(Collections.singletonList(roleUser)))
            .createdAt(Instant.parse("2023-01-01T10:00:00Z"))
            .updatedAt(Instant.parse("2023-01-01T10:00:00Z"))
            .enabled(true).accountNonExpired(true).credentialsNonExpired(true).accountNonLocked(true) // Default status
            .build();

        UserUpdateRequestDto updateDto = UserUpdateRequestDto.builder()
            .email("newemail@example.com")
            .build();
        Instant updateTime = Instant.now();
        User updatedUserEntity = User.builder()
            .id(testUser.getId())
            .username(testUser.getUsername())
            .password(testUser.getPassword())
            .email(updateDto.getEmail())
            .firstName(testUser.getFirstName())
            .lastName(testUser.getLastName())
            .roles(testUser.getRoles())
            .enabled(testUser.isEnabled()).accountNonExpired(testUser.isAccountNonExpired())
            .credentialsNonExpired(testUser.isCredentialsNonExpired())
            .accountNonLocked(testUser.isAccountNonLocked()) // Copy current status
            .createdAt(testUser.getCreatedAt())
            .updatedAt(updateTime)
            .build();

        UserResponseDto expectedDto = UserResponseDto.builder()
            .id(updatedUserEntity.getId())
            .username(updatedUserEntity.getUsername())
            .email(updatedUserEntity.getEmail())
            .firstName(updatedUserEntity.getFirstName())
            .lastName(updatedUserEntity.getLastName())
            .enabled(updatedUserEntity.isEnabled())
            .accountNonExpired(updatedUserEntity.isAccountNonExpired())
            .credentialsNonExpired(updatedUserEntity.isCredentialsNonExpired())
            .accountNonLocked(updatedUserEntity.isAccountNonLocked())
            .createdAt(updatedUserEntity.getCreatedAt())
            .updatedAt(updatedUserEntity.getUpdatedAt())
            .roles(Set.of("ROLE_USER"))
            .privileges(Set.of("READ_PRIVILEGE"))
            .build();

        // Configure Mocks
        when(userRepository.findById(userId)).thenReturn(Mono.just(testUser));
        when(userRepository.findByEmail(updateDto.getEmail())).thenReturn(Mono.empty());
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(updatedUserEntity));
        doAnswer(invocation -> {
            User targetUser = invocation.getArgument(1);
            UserUpdateRequestDto sourceDto = invocation.getArgument(0);
            if (sourceDto.getEmail() != null) {
                targetUser.setEmail(sourceDto.getEmail());
            }
            return null;
        }).when(userMapper).updateFromDto(any(UserUpdateRequestDto.class), any(User.class));
        mockUserRolesAndPrivileges(updatedUserEntity, roleUser,
            new HashSet<>(Collections.singletonList(readPrivilege)));
        when(userMapper.toDto(any(User.class))).thenReturn(expectedDto);

        StepVerifier.create(userService.updateUserData(userId, updateDto))
            .expectNextMatches(response -> {
                assertThat(response.getEmail(), is(updateDto.getEmail()));
                return true;
            })
            .verifyComplete();
        verify(userRepository).findByEmail(updateDto.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw conflict exception when updating user email to an already existing email")
    void updateUserData_emailConflict() {
        Long userId = 1L;
        User testUser = User.builder().id(userId).username("testuser").email("test@example.com").build();
        User existingAdminUser = User.builder().id(2L).username("adminuser").email("admin@example.com").build();

        UserUpdateRequestDto updateDto = UserUpdateRequestDto.builder()
            .email(existingAdminUser.getEmail())
            .build();

        // Configure Mocks
        when(userRepository.findById(userId)).thenReturn(Mono.just(testUser));
        when(userRepository.findByEmail(updateDto.getEmail())).thenReturn(Mono.just(existingAdminUser));

        StepVerifier.create(userService.updateUserData(userId, updateDto))
            .expectErrorMatches(e -> e instanceof ResponseStatusException
                && ((ResponseStatusException) e).getStatusCode().value() == 409)
            .verify();

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw not found exception when updating non-existent user data")
    void updateUserData_userNotFound() {
        UserUpdateRequestDto updateDto = UserUpdateRequestDto.builder()
            .firstName("Updated First")
            .lastName("Updated Last")
            .build();

        // Configure Mocks
        when(userRepository.findById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(userService.updateUserData(99L, updateDto))
            .expectErrorMatches(e -> e instanceof ResponseStatusException
                && ((ResponseStatusException) e).getStatusCode().value() == 404)
            .verify();

        verify(userRepository, never()).save(any(User.class));
    }

    // --- ReactiveUserDetailsService (findByUsername) Tests ---
    @Test
    @DisplayName("Should load user details by username successfully")
    void findByUsername_success() {
        Long userId = 1L;
        Privilege readPrivilege = Privilege.builder().id(1L).name("READ_PRIVILEGE").build();
        Role roleUser = Role.builder().id(100L).name("ROLE_USER")
            .privileges(new HashSet<>(Collections.singletonList(readPrivilege))).build();
        User testUser = User.builder()
            .id(userId)
            .username("testuser")
            .password("encodedPassword")
            .email("test@example.com")
            .firstName("Test")
            .lastName("User")
            .roles(new HashSet<>(Collections.singletonList(roleUser)))
            .enabled(true).accountNonExpired(true).credentialsNonExpired(true).accountNonLocked(true) // Default status
            .build();

        // Configure Mocks
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Mono.just(testUser));
        mockUserRolesAndPrivileges(testUser, roleUser, new HashSet<>(Collections.singletonList(readPrivilege)));

        StepVerifier.create(userService.findByUsername(testUser.getUsername()))
            .expectNextMatches(userDetails -> {
                assertThat(userDetails.getUsername(), is(testUser.getUsername()));
                assertThat(userDetails.getAuthorities(), hasSize(2)); // ROLE_USER, READ_PRIVILEGE
                assertThat(userDetails.getAuthorities(), hasItem(Matchers.hasProperty("authority", is("ROLE_USER"))));
                assertThat(userDetails.getAuthorities(),
                    hasItem(Matchers.hasProperty("authority", is("READ_PRIVILEGE"))));
                return true;
            })
            .verifyComplete();
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException if user not found by username")
    void findByUsername_notFound() {
        // Configure Mocks
        when(userRepository.findByUsername(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(userService.findByUsername("nonexistent"))
            .expectError(UsernameNotFoundException.class)
            .verify();
    }

    // --- Admin Service Methods Tests ---

    @Test
    @DisplayName("Should delete a user successfully by admin")
    void deleteUser_success() {
        Long userId = 1L;
        Privilege readPrivilege = Privilege.builder().id(1L).name("READ_PRIVILEGE").build();
        Role roleUser = Role.builder().id(100L).name("ROLE_USER")
            .privileges(new HashSet<>(Collections.singletonList(readPrivilege))).build();
        User testUser = User.builder()
            .id(userId)
            .username("testuser")
            .password("encodedPassword")
            .email("test@example.com")
            .firstName("Test")
            .lastName("User")
            .roles(new HashSet<>(Collections.singletonList(roleUser)))
            .enabled(true).accountNonExpired(true).credentialsNonExpired(true).accountNonLocked(true) // Default status
            .build();

        // Configure Mocks
        when(userRepository.findById(testUser.getId())).thenReturn(Mono.just(testUser));
//        when(roleRepository.findRolesByUserId(testUser.getId())).thenReturn(
//            Flux.just(roleUser)); // Added for isAdmin check
        when(userRepository.delete(testUser)).thenReturn(Mono.empty());

        StepVerifier.create(userService.deleteUser(testUser.getId()))
            .verifyComplete();

        verify(userRepository).delete(testUser);
    }

    @Test
    @DisplayName("Should throw not found exception when deleting non-existent user")
    void deleteUser_notFound() {
        // Configure Mocks
        when(userRepository.findById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(userService.deleteUser(99L))
            .expectErrorMatches(e -> e instanceof ResponseStatusException
                && ((ResponseStatusException) e).getStatusCode().value() == 404)
            .verify();

        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    @DisplayName("Should throw forbidden exception when trying to delete an ADMIN user")
    void deleteUser_adminForbidden() {
        Long adminUserId = 2L;
        Privilege readPrivilege = Privilege.builder().id(1L).name("READ_PRIVILEGE").build();
        Privilege writePrivilege = Privilege.builder().id(2L).name("WRITE_PRIVILEGE").build();
        Set<Privilege> adminPrivileges = new HashSet<>(Set.of(readPrivilege, writePrivilege));
        Role roleAdmin = Role.builder().id(101L).name("ROLE_ADMIN").privileges(adminPrivileges).build();
        User testAdminUser = User.builder()
            .id(adminUserId)
            .username("adminuser")
            .password("encodedAdminPassword")
            .email("admin@example.com")
            .firstName("Admin")
            .lastName("User")
            .roles(new HashSet<>(Collections.singletonList(roleAdmin)))
            .enabled(true).accountNonExpired(true).credentialsNonExpired(true).accountNonLocked(true) // Default status
            .build();

        // Configure Mocks
        when(userRepository.findById(testAdminUser.getId())).thenReturn(Mono.just(testAdminUser));
        // This is crucial: mock roles for the internal isAdmin check.
//        mockUserRolesAndPrivileges(testAdminUser, roleAdmin, adminPrivileges);

        StepVerifier.create(userService.deleteUser(testAdminUser.getId()))
            .expectErrorMatches(e -> e instanceof ResponseStatusException
                && ((ResponseStatusException) e).getStatusCode().value() == 403)
            .verify();

        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    @DisplayName("Should update user status successfully by admin")
    void updateUserStatus_success() {
        Long userId = 1L;
        Privilege readPrivilege = Privilege.builder().id(1L).name("READ_PRIVILEGE").build();
        Role roleUser = Role.builder().id(100L).name("ROLE_USER")
            .privileges(new HashSet<>(Collections.singletonList(readPrivilege))).build();
        User testUser = User.builder()
            .id(userId)
            .username("testuser")
            .password("encodedPassword")
            .email("test@example.com")
            .firstName("Test")
            .lastName("User")
            .roles(new HashSet<>(Collections.singletonList(roleUser)))
            .createdAt(Instant.parse("2023-01-01T10:00:00Z"))
            .updatedAt(Instant.parse("2023-01-01T10:00:00Z"))
            .enabled(true).accountNonExpired(true).credentialsNonExpired(true)
            .accountNonLocked(true) // Start with enabled
            .build();

        AdminUserUpdateRequestDto updateDto = AdminUserUpdateRequestDto.builder()
            .enabled(false)
            .accountNonExpired(false)
            .credentialsNonExpired(false)
            .accountNonLocked(false)
            .build();

        Instant updateTime = Instant.now();
        User updatedUserEntity = User.builder()
            .id(testUser.getId())
            .username(testUser.getUsername())
            .password(testUser.getPassword())
            .email(testUser.getEmail())
            .firstName(testUser.getFirstName())
            .lastName(testUser.getLastName())
            .roles(testUser.getRoles())
            .enabled(false) // Updated status
            .accountNonExpired(false) // Updated status
            .credentialsNonExpired(false) // Updated status
            .accountNonLocked(false) // Updated status
            .createdAt(testUser.getCreatedAt())
            .updatedAt(updateTime)
            .build();

        UserResponseDto testUserResponseDto = UserResponseDto.builder()
            .id(testUser.getId())
            .username(testUser.getUsername())
            .email(testUser.getEmail())
            .firstName(testUser.getFirstName())
            .lastName(testUser.getLastName())
            .enabled(false) // Updated status
            .accountNonExpired(false) // Updated status
            .credentialsNonExpired(false) // Updated status
            .accountNonLocked(false) // Updated status
            .createdAt(testUser.getCreatedAt())
            .updatedAt(updatedUserEntity.getUpdatedAt())
            .roles(Set.of("ROLE_USER"))
            .privileges(Set.of("READ_PRIVILEGE"))
            .build();

        // Configure Mocks
        when(userRepository.findById(testUser.getId())).thenReturn(Mono.just(testUser));
        when(roleRepository.findRolesByUserId(testUser.getId())).thenReturn(
            Flux.just(roleUser)); // Added for isAdmin check in service
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(updatedUserEntity));
        mockUserRolesAndPrivileges(updatedUserEntity, roleUser,
            new HashSet<>(Collections.singletonList(readPrivilege))); // This mock is for the final DTO conversion path
        when(userMapper.toDto(any(User.class))).thenReturn(testUserResponseDto);

        StepVerifier.create(userService.updateUserStatus(testUser.getId(), updateDto))
            .expectNextMatches(response -> {
                assertThat(response.isEnabled(), is(false));
                assertThat(response.isAccountNonExpired(), is(false));
                assertThat(response.isCredentialsNonExpired(), is(false));
                assertThat(response.isAccountNonLocked(), is(false));
                return true;
            })
            .verifyComplete();

        verify(userRepository).save(argThat(user ->
            !user.isEnabled() && !user.isAccountNonExpired() && !user.isCredentialsNonExpired()
                && !user.isAccountNonLocked()
        ));
    }

    @Test
    @DisplayName("Should throw not found exception when updating status of non-existent user")
    void updateUserStatus_notFound() {
        AdminUserUpdateRequestDto updateDto = AdminUserUpdateRequestDto.builder()
            .enabled(true)
            .accountNonExpired(true)
            .credentialsNonExpired(true)
            .accountNonLocked(true)
            .build();

        // Configure Mocks
        when(userRepository.findById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(userService.updateUserStatus(99L, updateDto))
            .expectErrorMatches(e -> e instanceof ResponseStatusException
                && ((ResponseStatusException) e).getStatusCode().value() == 404)
            .verify();

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw forbidden exception when trying to deactivate an ADMIN user")
    void updateUserStatus_adminDeactivationForbidden() {
        Long adminUserId = 2L;
        Privilege readPrivilege = Privilege.builder().id(1L).name("READ_PRIVILEGE").build();
        Privilege writePrivilege = Privilege.builder().id(2L).name("WRITE_PRIVILEGE").build();
        Set<Privilege> adminPrivileges = new HashSet<>(Set.of(readPrivilege, writePrivilege));
        Role roleAdmin = Role.builder().id(101L).name("ROLE_ADMIN").privileges(adminPrivileges).build();
        User testAdminUser = User.builder()
            .id(adminUserId)
            .username("adminuser")
            .password("encodedAdminPassword")
            .email("admin@example.com")
            .firstName("Admin")
            .lastName("User")
            .roles(new HashSet<>(Collections.singletonList(roleAdmin)))
            .enabled(true).accountNonExpired(true).credentialsNonExpired(true)
            .accountNonLocked(true) // Start with enabled
            .build();

        AdminUserUpdateRequestDto updateDto = AdminUserUpdateRequestDto.builder()
            .enabled(false) // Try to disable admin
            .accountNonExpired(true)
            .credentialsNonExpired(true)
            .accountNonLocked(true)
            .build();

        // Configure Mocks
        when(userRepository.findById(testAdminUser.getId())).thenReturn(Mono.just(testAdminUser));
//        mockUserRolesAndPrivileges(testAdminUser, roleAdmin, adminPrivileges); // Crucial for isAdmin check

        StepVerifier.create(userService.updateUserStatus(testAdminUser.getId(), updateDto))
            .expectErrorMatches(e -> e instanceof ResponseStatusException
                && ((ResponseStatusException) e).getStatusCode().value() == 403)
            .verify();

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw forbidden exception when trying to lock an ADMIN user")
    void updateUserStatus_adminLockForbidden() {
        Long adminUserId = 2L;
        Privilege readPrivilege = Privilege.builder().id(1L).name("READ_PRIVILEGE").build();
        Privilege writePrivilege = Privilege.builder().id(2L).name("WRITE_PRIVILEGE").build();
        Set<Privilege> adminPrivileges = new HashSet<>(Set.of(readPrivilege, writePrivilege));
        Role roleAdmin = Role.builder().id(101L).name("ROLE_ADMIN").privileges(adminPrivileges).build();
        User testAdminUser = User.builder()
            .id(adminUserId)
            .username("adminuser")
            .password("encodedAdminPassword")
            .email("admin@example.com")
            .firstName("Admin")
            .lastName("User")
            .roles(new HashSet<>(Collections.singletonList(roleAdmin)))
            .enabled(true).accountNonExpired(true).credentialsNonExpired(true)
            .accountNonLocked(true) // Start with unlocked
            .build();

        AdminUserUpdateRequestDto updateDto = AdminUserUpdateRequestDto.builder()
            .enabled(true)
            .accountNonExpired(true)
            .credentialsNonExpired(true)
            .accountNonLocked(false) // Try to lock admin
            .build();

        // Configure Mocks
        when(userRepository.findById(testAdminUser.getId())).thenReturn(Mono.just(testAdminUser));
//        mockUserRolesAndPrivileges(testAdminUser, roleAdmin, adminPrivileges); // Crucial for isAdmin check

        StepVerifier.create(userService.updateUserStatus(testAdminUser.getId(), updateDto))
            .expectErrorMatches(e -> e instanceof ResponseStatusException
                && ((ResponseStatusException) e).getStatusCode().value() == 403)
            .verify();

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should retrieve all users successfully by admin")
    void getAllUsers_success() {
        Long userId1 = 1L;
        Long userId2 = 2L;
        Long userId3 = 3L;

        Privilege readPrivilege = Privilege.builder().id(1L).name("READ_PRIVILEGE").build();
        Privilege writePrivilege = Privilege.builder().id(2L).name("WRITE_PRIVILEGE").build();
        Set<Privilege> userPrivileges = new HashSet<>(Collections.singletonList(readPrivilege));
        Set<Privilege> adminPrivileges = new HashSet<>(Set.of(readPrivilege, writePrivilege));

        Role roleUser = Role.builder().id(100L).name("ROLE_USER").privileges(userPrivileges).build();
        Role roleAdmin = Role.builder().id(101L).name("ROLE_ADMIN").privileges(adminPrivileges).build();

        User testUser = User.builder()
            .id(userId1)
            .username("testuser")
            .password("encodedPassword")
            .email("test@example.com")
            .firstName("Test")
            .lastName("User")
            .roles(new HashSet<>(Collections.singletonList(roleUser)))
            .enabled(true).accountNonExpired(true).credentialsNonExpired(true).accountNonLocked(true)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

        User testAdminUser = User.builder()
            .id(userId2)
            .username("adminuser")
            .password("encodedAdminPassword")
            .email("admin@example.com")
            .firstName("Admin")
            .lastName("User")
            .roles(new HashSet<>(Collections.singletonList(roleAdmin)))
            .enabled(true).accountNonExpired(true).credentialsNonExpired(true).accountNonLocked(true)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

        User anotherUser = User.builder()
            .id(userId3)
            .username("anotheruser")
            .password("pwd")
            .email("another@example.com")
            .firstName("Another")
            .lastName("User")
            .roles(Collections.singleton(roleUser))
            .enabled(true).accountNonExpired(true).credentialsNonExpired(true).accountNonLocked(true)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

        // Configure Mocks for findAll
        when(userRepository.findAll()).thenReturn(Flux.just(testUser, testAdminUser, anotherUser));

        // Setup mocks for fetchUserWithRolesAndPrivileges for each user
        // These should be called inside the flatMap of getAllUsers
        mockUserRolesAndPrivileges(testUser, roleUser, userPrivileges);
        mockUserRolesAndPrivileges(testAdminUser, roleAdmin, adminPrivileges);
        mockUserRolesAndPrivileges(anotherUser, roleUser, userPrivileges);

        // Mocks for mapper for each user entity to DTO conversion.
        // Use thenAnswer to dynamically return DTOs based on the input User.
        // This handles the case where the User object passed to toDto might be a modified instance.
        reset(userMapper); // Reset previous toDto stubs, as you correctly did
        when(userMapper.toDto(any(User.class))).thenAnswer(invocation -> {
            User userArg = invocation.getArgument(0);
            // Debugging: Print out the ID of the user being mapped
            System.out.println("Mapping user with ID: " + userArg.getId() + ", Username: " + userArg.getUsername());

            // Return the appropriate DTO based on the user's ID or username
            if (userArg.getId().equals(userId1)) {
                return UserResponseDto.builder()
                    .id(testUser.getId()).username(testUser.getUsername()).email(testUser.getEmail())
                    .firstName(testUser.getFirstName()).lastName(testUser.getLastName())
                    .enabled(testUser.isEnabled()).accountNonExpired(testUser.isAccountNonExpired())
                    .credentialsNonExpired(testUser.isCredentialsNonExpired())
                    .accountNonLocked(testUser.isAccountNonLocked())
                    .createdAt(testUser.getCreatedAt()).updatedAt(testUser.getUpdatedAt())
                    .roles(Set.of("ROLE_USER")).privileges(Set.of("READ_PRIVILEGE"))
                    .build();
            } else if (userArg.getId().equals(userId2)) {
                return UserResponseDto.builder()
                    .id(testAdminUser.getId()).username(testAdminUser.getUsername()).email(testAdminUser.getEmail())
                    .firstName(testAdminUser.getFirstName()).lastName(testAdminUser.getLastName())
                    .enabled(testAdminUser.isEnabled()).accountNonExpired(testAdminUser.isAccountNonExpired())
                    .credentialsNonExpired(testAdminUser.isCredentialsNonExpired())
                    .accountNonLocked(testAdminUser.isAccountNonLocked())
                    .createdAt(testAdminUser.getCreatedAt()).updatedAt(testAdminUser.getUpdatedAt())
                    .roles(Set.of("ROLE_ADMIN")).privileges(Set.of("READ_PRIVILEGE", "WRITE_PRIVILEGE"))
                    .build();
            } else if (userArg.getId().equals(userId3)) {
                return UserResponseDto.builder()
                    .id(anotherUser.getId()).username(anotherUser.getUsername()).email(anotherUser.getEmail())
                    .firstName(anotherUser.getFirstName()).lastName(anotherUser.getLastName())
                    .enabled(anotherUser.isEnabled()).accountNonExpired(anotherUser.isAccountNonExpired())
                    .credentialsNonExpired(anotherUser.isCredentialsNonExpired())
                    .accountNonLocked(anotherUser.isAccountNonLocked())
                    .createdAt(anotherUser.getCreatedAt()).updatedAt(anotherUser.getUpdatedAt())
                    .roles(Set.of("ROLE_USER")).privileges(Set.of("READ_PRIVILEGE"))
                    .build();
            }
            // Fallback or throw an error if an unexpected user is passed
            throw new IllegalArgumentException("Unexpected user passed to toDto: " + userArg.getId());
        });

        Flux<UserResponseDto> allUsers = userService.getAllUsers();
        StepVerifier.create(allUsers)
            .expectNextCount(3)
            .expectComplete();

        // Verify that toDto was called 3 times, once for each user
        verify(userMapper, times(3)).toDto(any(User.class));

        // Explicitly verify that roleRepository methods were called for each user's ID
        verify(roleRepository, times(1)).findRolesByUserId(userId1);
        verify(roleRepository, times(1)).findPrivilegesByRoleId(roleUser.getId());

        verify(roleRepository, times(1)).findRolesByUserId(userId2);
        verify(roleRepository, times(1)).findPrivilegesByRoleId(roleAdmin.getId());

        verify(roleRepository, times(1)).findRolesByUserId(userId3);
        verify(roleRepository, times(1)).findPrivilegesByRoleId(roleUser.getId());
    }

    @Test
    @DisplayName("Should return empty flux if no users exist for getAllUsers")
    void getAllUsers_empty() {
        // Configure Mocks
        when(userRepository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(userService.getAllUsers())
            .expectNextCount(0)
            .verifyComplete();
    }
}