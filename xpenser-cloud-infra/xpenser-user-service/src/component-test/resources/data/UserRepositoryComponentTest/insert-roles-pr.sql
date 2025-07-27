
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
WHERE r.name = 'ROLE_USER' AND p.name = 'READ_PRIVILEGE';

INSERT INTO role_privileges (role_id, privilege_id)
SELECT r.id, p.id
FROM roles r, privileges p
WHERE r.name = 'ROLE_ADMIN' AND p.name = 'READ_PRIVILEGE';

INSERT INTO role_privileges (role_id, privilege_id)
SELECT r.id, p.id
FROM roles r, privileges p
WHERE r.name = 'ROLE_ADMIN' AND p.name = 'WRITE_PRIVILEGE';

