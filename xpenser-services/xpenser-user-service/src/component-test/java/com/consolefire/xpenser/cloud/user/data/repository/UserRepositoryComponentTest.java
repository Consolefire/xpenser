package com.consolefire.xpenser.cloud.user.data.repository;


import com.consolefire.xpenser.cloud.user.data.entity.User;
import com.consolefire.xpenser.testutils.testcontainers.WithTestContainers;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlConfig.TransactionMode;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@WithTestContainers
@SpringBootTest(properties = {"spring.cloud.config.enabled=false"})
@SqlGroup({
    @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_CLASS,
        config = @SqlConfig(transactionMode = TransactionMode.ISOLATED),
        scripts = {"classpath:data/UserRepositoryComponentTest/insert-roles-pr.sql"}),
    @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD,
        config = @SqlConfig(transactionMode = TransactionMode.ISOLATED),
        scripts = {"classpath:data/UserRepositoryComponentTest/insert-users.sql"}),
    @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD,
        config = @SqlConfig(transactionMode = TransactionMode.ISOLATED),
        scripts = {"classpath:data/UserRepositoryComponentTest/link-data.sql"}),
    @Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD,
        config = @SqlConfig(transactionMode = TransactionMode.ISOLATED),
        scripts = {"classpath:data/UserRepositoryComponentTest/cleanup.sql"})
})
public class UserRepositoryComponentTest {

    public static final String TEST_USER_ID = "testuser";
    public static final String TEST_USER_MAIL = "test@example.com";
    public static final String ADMIN_USER_EMAIL = "admin@example.com";
    public static final String ADMIN_USER_ID = "adminuser";
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should find a user by username")
    void findByUsername_success() {
        Optional<User> foundUser = userRepository.findByUsername(TEST_USER_ID);
        assert (foundUser.isPresent());
        assert (TEST_USER_ID.equals(foundUser.get().getUsername()));
        assert (TEST_USER_MAIL.equals(foundUser.get().getEmail()));
    }

    @Test
    @DisplayName("Should return empty optional if username not found")
    void findByUsername_notFound() {
        Optional<User> foundUser = userRepository.findByUsername("nonexistentuser");
        assert (foundUser.isEmpty());
    }

    @Test
    @DisplayName("Should find a user by email")
    void findByEmail_success() {
        Optional<User> foundUser = userRepository.findByEmail(ADMIN_USER_EMAIL);
        assert (foundUser.isPresent());
        assert (ADMIN_USER_ID.equals(foundUser.get().getUsername()));
        assert (ADMIN_USER_EMAIL.equals(foundUser.get().getEmail()));
    }

    @Test
    @DisplayName("Should return empty optional if email not found")
    void findByEmail_notFound() {
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");
        assert (foundUser.isEmpty());
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @DisplayName("Should save a new user")
    void save_newUser() {
        User newUser = User.builder()
            .username("newjpauser")
            .password("encoded_pwd")
            .email("newjpauser@example.com")
            .firstName("New")
            .lastName("JPAUser")
            .enabled(true)
            .accountNonExpired(true)
            .credentialsNonExpired(true)
            .accountNonLocked(true)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

        User savedUser = userRepository.save(newUser);
        userRepository.flush(); // Ensure data is written to DB if using transactions and not committing yet

        assert (savedUser.getId() != null);
        assert ("newjpauser".equals(savedUser.getUsername()));

        Optional<User> foundSavedUser = userRepository.findByUsername("newjpauser");
        assert (foundSavedUser.isPresent());
    }

    @Test
    @DisplayName("Should delete a user by ID")
    void deleteById_success() {
        Optional<User> byUsername = userRepository.findByUsername(TEST_USER_ID);
        assert (byUsername.isPresent());
        userRepository.deleteById(byUsername.get().getId());
        userRepository.flush(); // Ensure delete is flushed

        assert (userRepository.findById(byUsername.get().getId()).isEmpty());
    }
}
