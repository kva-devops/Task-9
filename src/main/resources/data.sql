INSERT INTO authorities (authority)
    VALUES ('ROLE_ADMIN'), ('ROLE_USER');

INSERT INTO users (firstname, lastname, married, username, password, enabled)
    VALUES (
            'Ivan',
            'Ivanov',
            true,
            'admin',
            '$2a$10$UxcD9IBzU7yQw1xGM5ECwulSNXQjvGCh/MKejJTokulXtSB95CrA2',
            true);

INSERT INTO users_role_set (user_id, role_set_id)
    VALUES (1, 1);