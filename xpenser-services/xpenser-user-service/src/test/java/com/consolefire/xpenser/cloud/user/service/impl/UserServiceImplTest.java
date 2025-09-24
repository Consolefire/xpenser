package com.consolefire.xpenser.cloud.user.service.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
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

    // No more shared instance variables for test data!

    class NotEqualTo<T> implements ArgumentMatcher<T> {

        private final T expectedValue;

        public NotEqualTo(T expectedValue) {
            this.expectedValue = expectedValue;
        }

        @Override
        public boolean matches(T actual) {
            // Handle nulls explicitly if needed, otherwise Objects.equals handles it
            return !java.util.Objects.equals(actual, expectedValue);
        }

        @Override
        public String toString() {
            return "notEqualTo(" + expectedValue + ")";
        }
    }

    @BeforeEach
    void setUp() {
        // Common mocking setup for `fetchUserWithRolesAndPrivileges`
        // This is still useful as this internal method is called by many public methods.
        // Data for these mocks are created on-the-fly when needed.
        Role commonRoleUser = Role.builder().id(10L).name("ROLE_USER").build();
        Privilege commonPrivRead = Privilege.builder().id(1L).name("PRIV_READ").build();
        Privilege commonPrivWrite = Privilege.builder().id(2L).name("PRIV_WRITE").build();

        commonRoleUser.setPrivileges(new HashSet<>(Arrays.asList(commonPrivRead, commonPrivWrite)));


    }

    // --- Helper Methods to Create Test Data using Builders ---

    private User buildTestUser(Long id, String username, String email, String password, Set<Role> roles) {
        return User.builder()
            .id(id)
            .username(username)
            .password(password)
            .email(email)
            .firstName("Test")
            .lastName("User")
            .enabled(true)
            .accountNonExpired(true)
            .credentialsNonExpired(true)
            .accountNonLocked(true)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .roles(roles != null ? roles : new HashSet<>())
            .build();
    }

    private Role buildRole(Long id, String name, Set<Privilege> privileges) {
        return Role.builder()
            .id(id)
            .name(name)
            .privileges(privileges != null ? privileges : new HashSet<>())
            .build();
    }

    private Privilege buildPrivilege(Long id, String name) {
        return Privilege.builder().id(id).name(name).build();
    }

    private UserRegistrationRequestDto buildRegistrationRequest(String username, String email, String password) {
        return UserRegistrationRequestDto.builder()
            .username(username)
            .email(email)
            .password(password)
            .firstName("Test")
            .lastName("User")
            .build();
    }

    private UserUpdateRequestDto buildUserUpdateRequest(String firstName, String lastName, String email) {
        return UserUpdateRequestDto.builder()
            .firstName(firstName)
            .lastName(lastName)
            .email(email)
            .build();
    }

    private AdminUserUpdateRequestDto buildAdminUserUpdateRequest(Boolean enabled, Boolean locked, Set<String> roles) {
        return AdminUserUpdateRequestDto.builder()
            .enabled(enabled)
            .accountNonLocked(locked)
            .roles(roles)
            .build();
    }

    private UserResponseDto buildUserResponseDto(User user, Set<String> roleNames, Set<String> privilegeNames) {
        return UserResponseDto.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .enabled(user.isEnabled())
            .accountNonExpired(user.isAccountNonExpired())
            .credentialsNonExpired(user.isCredentialsNonExpired())
            .accountNonLocked(user.isAccountNonLocked())
            .roles(roleNames)
            .privileges(privilegeNames)
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }

    // --- Tests for loadUserByUsername (Blocking UserDetailsService) ---

    @Test
    @DisplayName("loadUserByUsername should return UserDetails for valid username")
    void loadUserByUsername_success() {
        // Test-specific data
        Role userRole = buildRole(10L, "ROLE_USER",
            new HashSet<>(Arrays.asList(buildPrivilege(1L, "PRIV_READ"), buildPrivilege(2L, "PRIV_WRITE"))));
        User user = buildTestUser(1L, "testuser", "test@example.com", "encodedPassword",
            new HashSet<>(Collections.singletonList(userRole)));

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        // Overwrite general roleRepository mocks for this specific test case, if needed, or ensure they align
        when(roleRepository.findRolesByUserId(user.getId())).thenReturn(Collections.singletonList(userRole));
        when(roleRepository.findPrivilegesByRoleId(userRole.getId())).thenReturn(
            Arrays.asList(buildPrivilege(1L, "PRIV_READ"), buildPrivilege(2L, "PRIV_WRITE")));

        UserDetails userDetails = userService.loadUserByUsername("testuser");

        assertThat(userDetails, is(notNullValue()));
        assertThat(userDetails.getUsername(), is(equalTo("testuser")));
        assertThat(userDetails.getPassword(), is(equalTo("encodedPassword")));
        assertThat(userDetails.getAuthorities(), hasItem(
            Matchers.<org.springframework.security.core.GrantedAuthority>hasProperty("authority", is("ROLE_USER"))));
        assertThat(userDetails.getAuthorities(), hasItem(
            Matchers.<org.springframework.security.core.GrantedAuthority>hasProperty("authority", is("PRIV_READ"))));
        assertThat(userDetails.isEnabled(), is(true));

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(roleRepository, times(1)).findRolesByUserId(user.getId());
        verify(roleRepository, times(1)).findPrivilegesByRoleId(userRole.getId());
    }

    @Test
    @DisplayName("loadUserByUsername should throw UsernameNotFoundException for non-existent username")
    void loadUserByUsername_notFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        UsernameNotFoundException thrown = Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("nonexistentuser");
        });

        assertThat(thrown.getMessage(), is(equalTo("User not found with username: nonexistentuser")));

        verify(userRepository, times(1)).findByUsername("nonexistentuser");
        verifyNoInteractions(roleRepository); // No need to interact with roles if user not found
    }

    // --- Tests for findByUsername (Reactive ReactiveUserDetailsService) ---

    @Test
    @DisplayName("findByUsername (reactive) should return UserDetails for valid username")
    void findByUsernameReactive_success() {
        // Test-specific data
        Role userRole = buildRole(10L, "ROLE_USER",
            new HashSet<>(Arrays.asList(buildPrivilege(1L, "PRIV_READ"), buildPrivilege(2L, "PRIV_WRITE"))));
        User user = buildTestUser(1L, "testuser", "test@example.com", "encodedPassword",
            new HashSet<>(Collections.singletonList(userRole)));

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        // Overwrite general roleRepository mocks for this specific test case, if needed, or ensure they align
        when(roleRepository.findRolesByUserId(user.getId())).thenReturn(Collections.singletonList(userRole));
        when(roleRepository.findPrivilegesByRoleId(userRole.getId())).thenReturn(
            Arrays.asList(buildPrivilege(1L, "PRIV_READ"), buildPrivilege(2L, "PRIV_WRITE")));

        Mono<UserDetails> userDetailsMono = userService.findByUsername("testuser");

        StepVerifier.create(userDetailsMono)
            .assertNext(userDetails -> {
                assertThat(userDetails, is(notNullValue()));
                assertThat(userDetails.getUsername(), is(equalTo("testuser")));
                assertThat(userDetails.getPassword(), is(equalTo("encodedPassword")));
                assertThat(userDetails.getAuthorities(), hasItem(
                    Matchers.<org.springframework.security.core.GrantedAuthority>hasProperty("authority",
                        is("ROLE_USER"))));
                assertThat(userDetails.getAuthorities(), hasItem(
                    Matchers.<org.springframework.security.core.GrantedAuthority>hasProperty("authority",
                        is("PRIV_READ"))));
                assertThat(userDetails.isEnabled(), is(true));
            })
            .verifyComplete();

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(roleRepository, times(1)).findRolesByUserId(user.getId());
        verify(roleRepository, times(1)).findPrivilegesByRoleId(userRole.getId());
    }

    @Test
    @DisplayName("findByUsername (reactive) should return ResponseStatusException for non-existent username")
    void findByUsernameReactive_notFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        Mono<UserDetails> userDetailsMono = userService.findByUsername("nonexistentuser");

        StepVerifier.create(userDetailsMono)
            .expectErrorMatches(e -> e instanceof ResponseStatusException &&
                ((ResponseStatusException) e).getStatusCode().equals(HttpStatus.NOT_FOUND) &&
                e.getMessage().contains("User not found"))
            .verify();

        verify(userRepository, times(1)).findByUsername("nonexistentuser");
        verifyNoInteractions(roleRepository);
    }

    // --- Tests for createUser ---

    @Test
    @DisplayName("createUser should successfully register a new user")
    void createUser_success() {
        Role commonRoleUser = Role.builder().id(10L).name("ROLE_USER").build();
        Privilege commonPrivRead = Privilege.builder().id(1L).name("PRIV_READ").build();
        Privilege commonPrivWrite = Privilege.builder().id(2L).name("PRIV_WRITE").build();

        commonRoleUser.setPrivileges(new HashSet<>(Arrays.asList(commonPrivRead, commonPrivWrite)));

        when(roleRepository.findRolesByUserId(anyLong()))
            .thenReturn(Collections.singletonList(commonRoleUser));
        when(roleRepository.findPrivilegesByRoleId(commonRoleUser.getId()))
            .thenReturn(Arrays.asList(commonPrivRead, commonPrivWrite));

        // Test-specific data
        UserRegistrationRequestDto requestDto = buildRegistrationRequest(
            "newuser", "newuser@example.com", "password123");
        Role roleUser = buildRole(10L, "ROLE_USER", null); // Privileges will be fetched via findPrivilegesByRoleId mock
        User newUser = buildTestUser(2L, requestDto.getUsername(), requestDto.getEmail(),
            "encodedPasswordFromMock", new HashSet<>(Collections.singletonList(roleUser)));
        UserResponseDto newUserResponseDto = buildUserResponseDto(newUser,
            new HashSet<>(Collections.singletonList("ROLE_USER")),
            new HashSet<>(Arrays.asList("PRIV_READ", "PRIV_WRITE"))); // Match setup in beforeEach

        when(userRepository.findByUsername(requestDto.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(roleUser));
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encodedPasswordFromMock");
        when(userMapper.toEntity(any(UserRegistrationRequestDto.class))).thenReturn(newUser);
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        when(userMapper.toDto(any(User.class))).thenReturn(newUserResponseDto);

        Mono<UserResponseDto> resultMono = userService.createUser(requestDto);

        StepVerifier.create(resultMono)
            .assertNext(dto -> {
                assertThat(dto, is(notNullValue()));
                assertThat(dto.getUsername(), is(equalTo(newUser.getUsername())));
                assertThat(dto.getRoles(), hasItem("ROLE_USER"));
                assertThat(dto.getPrivileges(), hasItem("PRIV_READ"));
            })
            .verifyComplete();

        verify(userRepository, times(1)).findByUsername(requestDto.getUsername());
        verify(userRepository, times(1)).findByEmail(requestDto.getEmail());
        verify(roleRepository, times(1)).findByName("ROLE_USER");
        verify(passwordEncoder, times(1)).encode(requestDto.getPassword());
        verify(userMapper, times(1)).toEntity(requestDto);
        verify(userRepository, times(1)).save(argThat(u ->
            u.getPassword().equals("encodedPasswordFromMock") && u.getRoles().contains(roleUser)));
        verify(userMapper, times(1)).toDto(any(User.class));
    }

    @Test
    @DisplayName("createUser should throw CONFLICT if username exists")
    void createUser_usernameConflict() {
        UserRegistrationRequestDto requestDto = buildRegistrationRequest(
            "existinguser", "email@example.com", "password123");
        User existingUser = User.builder().username(requestDto.getUsername()).build();

        when(userRepository.findByUsername(requestDto.getUsername())).thenReturn(Optional.of(existingUser));

        Mono<UserResponseDto> resultMono = userService.createUser(requestDto);

        StepVerifier.create(resultMono)
            .expectErrorMatches(e -> e instanceof ResponseStatusException &&
                ((ResponseStatusException) e).getStatusCode().equals(HttpStatus.CONFLICT) &&
                e.getMessage().contains("Username already exists"))
            .verify();

        verify(userRepository, times(1)).findByUsername(requestDto.getUsername());
        verify(userRepository, never()).findByEmail(anyString()); // Should not call findByEmail
        verifyNoMoreInteractions(roleRepository, passwordEncoder, userMapper);
    }

    @Test
    @DisplayName("createUser should throw CONFLICT if email exists")
    void createUser_emailConflict() {
        UserRegistrationRequestDto requestDto = buildRegistrationRequest(
            "newuser", "existing@example.com", "password123");
        User existingUser = User.builder().email(requestDto.getEmail()).build();

        when(userRepository.findByUsername(requestDto.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.of(existingUser));

        Mono<UserResponseDto> resultMono = userService.createUser(requestDto);

        StepVerifier.create(resultMono)
            .expectErrorMatches(e -> e instanceof ResponseStatusException &&
                ((ResponseStatusException) e).getStatusCode().equals(HttpStatus.CONFLICT) &&
                e.getMessage().contains("Email already exists"))
            .verify();

        verify(userRepository, times(1)).findByUsername(requestDto.getUsername());
        verify(userRepository, times(1)).findByEmail(requestDto.getEmail());
        verifyNoMoreInteractions(roleRepository, passwordEncoder, userMapper);
    }

    @Test
    @DisplayName("createUser should throw IllegalStateException if default role not found")
    void createUser_defaultRoleNotFound() {
        UserRegistrationRequestDto requestDto = buildRegistrationRequest(
            "newuser", "newuser@example.com", "password123");

        when(userRepository.findByUsername(requestDto.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());

        Mono<UserResponseDto> resultMono = userService.createUser(requestDto);

        StepVerifier.create(resultMono)
            .expectErrorMatches(e -> e instanceof IllegalStateException &&
                e.getMessage().contains("Default role 'ROLE_USER' not found"))
            .verify();

        verify(userRepository, times(1)).findByUsername(requestDto.getUsername());
        verify(userRepository, times(1)).findByEmail(requestDto.getEmail());
        verify(roleRepository, times(1)).findByName("ROLE_USER");
        verifyNoMoreInteractions(passwordEncoder, userMapper, userRepository);
    }

    // --- Tests for updateUserData ---

    @Test
    @DisplayName("updateUserData should successfully update user details")
    void updateUserData_success() {
        // Test-specific data
        UserUpdateRequestDto requestDto = buildUserUpdateRequest(
            "Updated", "User", "updated@example.com");
        Role userRole = buildRole(10L, "ROLE_USER",
            new HashSet<>(Arrays.asList(buildPrivilege(1L, "PRIV_READ"), buildPrivilege(2L, "PRIV_WRITE"))));
        User existingUser = buildTestUser(1L, "originaluser", "original@example.com", "pass",
            new HashSet<>(Collections.singletonList(userRole)));
        User updatedUser = buildTestUser(1L, "originaluser", requestDto.getEmail(), "pass",
            new HashSet<>(Collections.singletonList(userRole)));
        updatedUser.setFirstName(requestDto.getFirstName());
        updatedUser.setLastName(requestDto.getLastName());
        updatedUser.setUpdatedAt(Instant.now()); // Simulate update time

        UserResponseDto updatedUserResponseDto = buildUserResponseDto(updatedUser,
            new HashSet<>(Collections.singletonList("ROLE_USER")),
            new HashSet<>(Arrays.asList("PRIV_READ", "PRIV_WRITE")));

        when(userRepository.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty()); // New email, no conflict
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toDto(any(User.class))).thenReturn(updatedUserResponseDto);

        Mono<UserResponseDto> resultMono = userService.updateUserData(existingUser.getId(), requestDto);

        StepVerifier.create(resultMono)
            .assertNext(dto -> {
                assertThat(dto, is(notNullValue()));
                assertThat(dto.getFirstName(), is(equalTo("Updated")));
                assertThat(dto.getLastName(), is(equalTo("User")));
                assertThat(dto.getEmail(), is(equalTo("updated@example.com")));
                assertThat(dto.getUpdatedAt(), is(notNullValue()));
            })
            .verifyComplete();

        verify(userRepository, times(1)).findById(existingUser.getId());
        verify(userRepository, times(1)).findByEmail(requestDto.getEmail());
        verify(userRepository, times(1)).save(argThat(u ->
            u.getFirstName().equals("Updated") &&
                u.getLastName().equals("User") &&
                u.getEmail().equals("updated@example.com") &&
                u.getUpdatedAt() != null));
        verify(userMapper, times(1)).toDto(any(User.class));
    }

    @Test
    @DisplayName("updateUserData should throw NOT_FOUND if user does not exist")
    void updateUserData_userNotFound() {
        UserUpdateRequestDto requestDto = buildUserUpdateRequest("F", "L", "e@e.com");

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Mono<UserResponseDto> resultMono = userService.updateUserData(99L, requestDto);

        StepVerifier.create(resultMono)
            .expectErrorMatches(e -> e instanceof ResponseStatusException &&
                ((ResponseStatusException) e).getStatusCode().equals(HttpStatus.NOT_FOUND) &&
                e.getMessage().contains("User not found"))
            .verify();

        verify(userRepository, times(1)).findById(99L);
        verifyNoMoreInteractions(userRepository, roleRepository, userMapper);
    }

    @Test
    @DisplayName("updateUserData should throw CONFLICT if new email exists for another user")
    void updateUserData_emailConflict() {
        // Test-specific data
        UserUpdateRequestDto requestDto = buildUserUpdateRequest("Updated", "User", "other@example.com");
        Role userRole = buildRole(10L, "ROLE_USER", null);
        User existingUser = buildTestUser(1L, "originaluser", "original@example.com", "pass",
            new HashSet<>(Collections.singletonList(userRole)));
        User otherUser = buildTestUser(2L, "otheruser", "other@example.com", "pass",
            new HashSet<>(Collections.singletonList(userRole)));

        when(userRepository.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.of(otherUser));

        Mono<UserResponseDto> resultMono = userService.updateUserData(existingUser.getId(), requestDto);

        StepVerifier.create(resultMono)
            .expectErrorMatches(e -> e instanceof ResponseStatusException &&
                ((ResponseStatusException) e).getStatusCode().equals(HttpStatus.CONFLICT) &&
                e.getMessage().contains("Email already exists"))
            .verify();

        verify(userRepository, times(1)).findById(existingUser.getId());
        verify(userRepository, times(1)).findByEmail(requestDto.getEmail());
        verifyNoMoreInteractions(userRepository, roleRepository, userMapper);
    }

    @Test
    @DisplayName("updateUserData should not throw CONFLICT if email is same for same user")
    void updateUserData_sameEmailNoConflict() {

        // Test-specific data
        Role userRole = buildRole(10L, "ROLE_USER",
            new HashSet<>(Arrays.asList(buildPrivilege(1L, "PRIV_READ"), buildPrivilege(2L, "PRIV_WRITE"))));
        User existingUser = buildTestUser(1L, "originaluser", "original@example.com", "pass",
            new HashSet<>(Collections.singletonList(userRole)));
        UserUpdateRequestDto requestDto = buildUserUpdateRequest("Updated", "User",
            existingUser.getEmail()); // Same email as existingUser
        User updatedUser = buildTestUser(1L, "originaluser", existingUser.getEmail(), "pass",
            new HashSet<>(Collections.singletonList(userRole)));
        updatedUser.setFirstName(requestDto.getFirstName());
        updatedUser.setLastName(requestDto.getLastName());
        updatedUser.setUpdatedAt(Instant.now());

        UserResponseDto updatedUserResponseDto = buildUserResponseDto(updatedUser,
            new HashSet<>(Collections.singletonList("ROLE_USER")),
            new HashSet<>(Arrays.asList("PRIV_READ", "PRIV_WRITE")));

        when(userRepository.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toDto(any(User.class))).thenReturn(updatedUserResponseDto);

        Mono<UserResponseDto> resultMono = userService.updateUserData(existingUser.getId(), requestDto);

        StepVerifier.create(resultMono)
            .assertNext(dto -> {
                assertThat(dto, is(notNullValue()));
                assertThat(dto.getEmail(), is(equalTo(existingUser.getEmail())));
            })
            .verifyComplete();

        verify(userRepository, times(1)).findById(existingUser.getId());
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toDto(any(User.class));
    }

    // --- Tests for getUserById ---

    @Test
    @DisplayName("getUserById should return user by ID successfully")
    void getUserById_success() {
        // Test-specific data
        Role userRole = buildRole(10L, "ROLE_USER",
            new HashSet<>(Arrays.asList(buildPrivilege(1L, "PRIV_READ"), buildPrivilege(2L, "PRIV_WRITE"))));
        User user = buildTestUser(1L, "testuser", "test@example.com", "encodedPassword",
            new HashSet<>(Collections.singletonList(userRole)));
        UserResponseDto userResponseDto = buildUserResponseDto(user,
            new HashSet<>(Collections.singletonList("ROLE_USER")),
            new HashSet<>(Arrays.asList("PRIV_READ", "PRIV_WRITE")));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userMapper.toDto(any(User.class))).thenReturn(userResponseDto);

        Mono<UserResponseDto> resultMono = userService.getUserById(user.getId());

        StepVerifier.create(resultMono)
            .assertNext(dto -> {
                assertThat(dto, is(notNullValue()));
                assertThat(dto.getId(), is(equalTo(user.getId())));
                assertThat(dto.getUsername(), is(equalTo(user.getUsername())));
                assertThat(dto.getRoles(), hasItem("ROLE_USER"));
                assertThat(dto.getPrivileges(), hasItem("PRIV_READ"));
            })
            .verifyComplete();

        verify(userRepository, times(1)).findById(user.getId());
        verify(userMapper, times(1)).toDto(any(User.class));
    }

    @Test
    @DisplayName("getUserById should throw NOT_FOUND if user by ID does not exist")
    void getUserById_notFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Mono<UserResponseDto> resultMono = userService.getUserById(99L);

        StepVerifier.create(resultMono)
            .expectErrorMatches(e -> e instanceof ResponseStatusException &&
                ((ResponseStatusException) e).getStatusCode().equals(HttpStatus.NOT_FOUND) &&
                e.getMessage().contains("User not found"))
            .verify();

        verify(userRepository, times(1)).findById(99L);
        verifyNoInteractions(userMapper);
    }

    // --- Tests for deleteUser ---

    @Test
    @DisplayName("deleteUser should successfully delete an existing user")
    void deleteUser_success() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(userId);

        Mono<Void> resultMono = userService.deleteUser(userId);

        StepVerifier.create(resultMono)
            .verifyComplete(); // Expect completion for Mono<Void>

        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("deleteUser should throw NOT_FOUND if user to delete does not exist")
    void deleteUser_notFound() {
        Long userId = 99L;
        when(userRepository.existsById(anyLong())).thenReturn(false);

        Mono<Void> resultMono = userService.deleteUser(userId);

        StepVerifier.create(resultMono)
            .expectErrorMatches(e -> e instanceof ResponseStatusException &&
                ((ResponseStatusException) e).getStatusCode().equals(HttpStatus.NOT_FOUND) &&
                e.getMessage().contains("User not found"))
            .verify();

        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, never()).deleteById(anyLong());
    }

    // --- Tests for updateUserStatus ---

    @Test
    @DisplayName("updateUserStatus should update user status fields and roles")
    void updateUserStatus_success() {
        // Test-specific data
        AdminUserUpdateRequestDto requestDto = buildAdminUserUpdateRequest(false, false,
            Collections.singleton("ROLE_ADMIN"));
        Role userRole = buildRole(10L, "ROLE_USER", null); // Original role
        Role adminRole = buildRole(11L, "ROLE_ADMIN", new HashSet<>(
            Collections.singletonList(buildPrivilege(1L, "PRIV_READ")))); // New role with specific privilege

        User existingUser = buildTestUser(1L, "originaluser", "original@example.com", "pass",
            new HashSet<>(Collections.singletonList(userRole)));
        User updatedUser = buildTestUser(1L, "originaluser", "original@example.com", "pass",
            new HashSet<>(Collections.singletonList(adminRole)));
        updatedUser.setEnabled(false);
        updatedUser.setAccountNonLocked(false);
        updatedUser.setUpdatedAt(Instant.now());

        UserResponseDto updatedUserResponseDto = buildUserResponseDto(updatedUser,
            new HashSet<>(Collections.singletonList("ROLE_ADMIN")),
            new HashSet<>(Collections.singletonList("PRIV_READ")));

        when(userRepository.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toDto(any(User.class))).thenReturn(updatedUserResponseDto);

        // Adjust common mocks for fetchUserWithRolesAndPrivileges when it's called with the updated user
        when(roleRepository.findRolesByUserId(existingUser.getId()))
            .thenReturn(Collections.singletonList(adminRole)); // Simulate fetching admin role for updated user
        when(roleRepository.findPrivilegesByRoleId(adminRole.getId()))
            .thenReturn(Collections.singletonList(buildPrivilege(1L, "PRIV_READ"))); // Simulate admin's privileges

        Mono<UserResponseDto> resultMono = userService.updateUserStatus(existingUser.getId(), requestDto);

        StepVerifier.create(resultMono)
            .assertNext(dto -> {
                assertThat(dto, is(notNullValue()));
                assertThat(dto.isEnabled(), is(false));
                assertThat(dto.isAccountNonLocked(), is(false));
                assertThat(dto.getRoles(), hasItem("ROLE_ADMIN"));
                assertThat(dto.getRoles(), not(hasItem("ROLE_USER"))); // Original role removed
                assertThat(dto.getPrivileges(), hasItem("PRIV_READ"));
                assertThat(dto.getPrivileges(), not(hasItem("PRIV_WRITE"))); // Privileges changed with role
            })
            .verifyComplete();

        verify(userRepository, times(1)).findById(existingUser.getId());
        verify(roleRepository, times(1)).findByName("ROLE_ADMIN");
        verify(userRepository, times(1)).save(argThat(u ->
            !u.isEnabled() && !u.isAccountNonLocked() && u.getRoles().contains(adminRole)));
        verify(userMapper, times(1)).toDto(any(User.class));
    }

    @Test
    @DisplayName("updateUserStatus should throw NOT_FOUND if user does not exist")
    void updateUserStatus_userNotFound() {
        AdminUserUpdateRequestDto requestDto = new AdminUserUpdateRequestDto();
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Mono<UserResponseDto> resultMono = userService.updateUserStatus(99L, requestDto);

        StepVerifier.create(resultMono)
            .expectErrorMatches(e -> e instanceof ResponseStatusException &&
                ((ResponseStatusException) e).getStatusCode().equals(HttpStatus.NOT_FOUND) &&
                e.getMessage().contains("User not found"))
            .verify();

        verify(userRepository, times(1)).findById(99L);
        verifyNoMoreInteractions(userRepository, roleRepository, userMapper);
    }

    @Test
    @DisplayName("updateUserStatus should throw BAD_REQUEST if role not found")
    void updateUserStatus_roleNotFound() {
        // Test-specific data
        AdminUserUpdateRequestDto requestDto = buildAdminUserUpdateRequest(true, true,
            Collections.singleton("NON_EXISTENT_ROLE"));
        Role userRole = buildRole(10L, "ROLE_USER", null);
        User existingUser = buildTestUser(1L, "originaluser", "original@example.com", "pass",
            new HashSet<>(Collections.singletonList(userRole)));

        when(userRepository.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));
        when(roleRepository.findByName("NON_EXISTENT_ROLE")).thenReturn(Optional.empty());

        Mono<UserResponseDto> resultMono = userService.updateUserStatus(existingUser.getId(), requestDto);

        StepVerifier.create(resultMono)
            .expectErrorMatches(e -> e instanceof ResponseStatusException &&
                ((ResponseStatusException) e).getStatusCode().equals(HttpStatus.BAD_REQUEST) &&
                e.getMessage().contains("Role not found: NON_EXISTENT_ROLE"))
            .verify();

        verify(userRepository, times(1)).findById(existingUser.getId());
        verify(roleRepository, times(1)).findByName("NON_EXISTENT_ROLE");
        verify(userRepository, never()).save(any(User.class));
        verifyNoMoreInteractions(userMapper);
    }

    // --- Tests for getAllUsers ---

    @Test
    @DisplayName("getAllUsers should return all users successfully")
    void getAllUsers_success() {
        // Test-specific data for multiple users
        Role userRole = buildRole(10L, "ROLE_USER",
            new HashSet<>(Arrays.asList(buildPrivilege(1L, "PRIV_READ"), buildPrivilege(2L, "PRIV_WRITE"))));

        User user1 = buildTestUser(1L, "testuser1", "test1@example.com", "pass1",
            new HashSet<>(Collections.singletonList(userRole)));
        User user2 = buildTestUser(2L, "testuser2", "test2@example.com", "pass2",
            new HashSet<>(Collections.singletonList(userRole)));

        UserResponseDto user1Dto = buildUserResponseDto(user1,
            new HashSet<>(Collections.singletonList("ROLE_USER")),
            new HashSet<>(Arrays.asList("PRIV_READ", "PRIV_WRITE")));
        UserResponseDto user2Dto = buildUserResponseDto(user2,
            new HashSet<>(Collections.singletonList("ROLE_USER")),
            new HashSet<>(Arrays.asList("PRIV_READ", "PRIV_WRITE")));

        List<User> allUsers = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(allUsers);

        when(userMapper.toDto(user1)).thenReturn(user1Dto);
        when(userMapper.toDto(user2)).thenReturn(user2Dto);

        // Adjust common mocks for fetchUserWithRolesAndPrivileges to return appropriate roles/privileges
        // for each user as they are fetched.
        when(roleRepository.findRolesByUserId(user1.getId()))
            .thenReturn(Collections.singletonList(userRole));
        when(roleRepository.findRolesByUserId(user2.getId()))
            .thenReturn(Collections.singletonList(userRole));
        when(roleRepository.findPrivilegesByRoleId(userRole.getId()))
            .thenReturn(Arrays.asList(buildPrivilege(1L, "PRIV_READ"), buildPrivilege(2L, "PRIV_WRITE")));

        StepVerifier.create(userService.getAllUsers())
            .expectNextMatches(dto -> dto.getUsername().equals(user1.getUsername()))
            .expectNextMatches(dto -> dto.getUsername().equals(user2.getUsername()))
            .verifyComplete();

        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).toDto(user1);
        verify(userMapper, times(1)).toDto(user2);
        verify(roleRepository, times(1)).findRolesByUserId(user1.getId());
        verify(roleRepository, times(1)).findRolesByUserId(user2.getId());
        verify(roleRepository, times(2)).findPrivilegesByRoleId(userRole.getId());
    }

    @Test
    @DisplayName("getAllUsers should return empty flux if no users found")
    void getAllUsers_noUsers() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        StepVerifier.create(userService.getAllUsers())
            .expectNextCount(0)
            .verifyComplete();

        verify(userRepository, times(1)).findAll();
        verifyNoInteractions(userMapper, roleRepository); // No interactions if no users
    }
}