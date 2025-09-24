-- Insert Users
INSERT INTO users (username, password, email, first_name, last_name, created_at, updated_at)
VALUES (
    'admin',
    '$2a$10$eSOnTSwnAGiFydC71DokJOvNs1hwHxQm.ZboN3/ZsI1ysRweUFVpK',
    'admin@consolefire.com', 'Admin', 'User', NOW(), NOW()
 );

-- Insert Roles
 INSERT INTO roles (name)
 VALUES ('ROLE_USER'),
        ('ROLE_ADMIN');

 -- Insert Privileges
 INSERT INTO privileges (name)
 VALUES ('READ_PRIVILEGE'),
        ('WRITE_PRIVILEGE');


 INSERT INTO role_privileges (role_id, privilege_id)
  SELECT r.id, p.id
  FROM roles r, privileges p
  WHERE r.name = 'ROLE_ADMIN' AND p.name = 'READ_PRIVILEGE';

 INSERT INTO role_privileges (role_id, privilege_id)
  SELECT r.id, p.id
  FROM roles r, privileges p
  WHERE r.name = 'ROLE_ADMIN' AND p.name = 'WRITE_PRIVILEGE';


 INSERT INTO user_roles (user_id, role_id)
 SELECT u.id, r.id
 FROM users u, roles r
 WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN';
