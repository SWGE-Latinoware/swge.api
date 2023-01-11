INSERT INTO users (name, tag_name, country, zip_code, state, city, address_line1, email, password, admin,
                   email_communication, enabled, confirmed, github_id)
VALUES ('Rafael', 'Rafael', 'BR', '89562123', 'PR', 'Foz do Iguaçu', 'Rua das rosas, 00',
        'rafael@gmail.teste.com',
        '$2a$10$62vjXA8eARNq4du7TWuhjORpo08S8Fqc1gxftUpiT6o2Q5puWIL7G', true, true, true, true, '001');

INSERT INTO users (name, tag_name, country, zip_code, state, city, address_line1, email, password, admin,
                   email_communication, enabled, confirmed)
VALUES ('Gabriel', 'Gabriel', 'BR', '88562123', 'PR', 'Foz do Iguaçu', 'Rua das folhas, 002',
        'gabriel@gmail.teste.com',
        '$2a$10$62vjXA8eARNq4du7TWuhjORpo08S8Fqc1gxftUpiT6o2Q5puWIL7G', false, true, false, false);

INSERT INTO users (name, tag_name, country, zip_code, state, city, address_line1, email, password, admin,
                   email_communication, enabled, confirmed, google_id)
VALUES ('Default Admin', 'Admin', 'BR', '89564123', 'PR', 'Foz do Iguaçu', 'Jardim do centro, 00',
        'admin@gmail.teste.com',
        '$2a$10$62vjXA8eARNq4du7TWuhjORpo08S8Fqc1gxftUpiT6o2Q5puWIL7G', true, true, true, true, '003');
