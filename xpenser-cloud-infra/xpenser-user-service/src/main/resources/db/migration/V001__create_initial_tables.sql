CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL, -- Store hashed passwords (e.g., BCrypt, up to 60 for BCrypt, but 255 is safe for future algorithms)
    email VARCHAR(100) UNIQUE,
    first_name VARCHAR(50) DEFAULT NULL,
    last_name VARCHAR(50) DEFAULT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE, -- For Spring Security's isEnabled()
    account_non_expired BOOLEAN NOT NULL DEFAULT TRUE, -- For Spring Security's isAccountNonExpired()
    credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE, -- For Spring Security's isCredentialsNonExpired()
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE, -- For Spring Security's isAccountNonLocked()
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_users_email ON users (email);

CREATE IF NOT EXISTS TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE -- E.g., 'ROLE_USER', 'ROLE_ADMIN'
);

CREATE INDEX idx_roles_name ON roles (name);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_roles_user_id ON user_roles (user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles (role_id);

CREATE TABLE IF NOT EXISTS privileges (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE -- E.g., 'READ_USERS', 'WRITE_PRODUCTS', 'DELETE_ANY_ITEM'
);

CREATE INDEX idx_privileges_name ON privileges (name);


CREATE TABLE IF NOT EXISTS role_privileges (
    role_id BIGINT NOT NULL,
    privilege_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, privilege_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (privilege_id) REFERENCES privileges(id) ON DELETE CASCADE
);

CREATE INDEX idx_role_privileges_role_id ON role_privileges (role_id);
CREATE INDEX idx_role_privileges_privilege_id ON role_privileges (privilege_id);

