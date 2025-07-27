-- Insert Users
INSERT INTO users (username, password, email, first_name, last_name, created_at, updated_at)
VALUES ('testuser', 'encodedpassword1', 'test@example.com', 'Test', 'User', NOW(), NOW()),
       ('adminuser', 'encodedpassword2', 'admin@example.com', 'Admin', 'User', NOW(), NOW()),
       ('anotheruser', 'encodedpassword3', 'another@example.com', 'another', 'User', NOW(), NOW());
