DELETE FROM users WHERE id = 0;
DELETE FROM admin WHERE id = 0;
DELETE FROM roles WHERE id = 0;
DELETE FROM roles WHERE id = 1;
DELETE FROM roles WHERE id = 2;
DELETE FROM roles WHERE id = 3;
DELETE FROM roles WHERE id = 4;

INSERT INTO roles (id, name, permissions)
VALUES (0,
        'ROLE_PATIENT',
        'PATIENT'
);

INSERT INTO roles (id, name, permissions)
VALUES (1,
        'ROLE_CONSULTANT',
        'CONSULTANT'
       );

INSERT INTO roles (id, name, permissions)
VALUES (2,
        'ROLE_ADMIN',
        'ADMIN');

INSERT INTO roles (id, name, permissions)
VALUES (3,
        'ROLE_SUPER_ADMIN',
        'SUPER_ADMIN'
       );
INSERT INTO roles (id, name, permissions)
VALUES (4,
        'ROLE_SYSTEM',
        'SYSTEM'
       );




INSERT INTO users (id, reference_id, user_id, first_name, last_name, email, phone_number, country, state, city, zip_code, gender,role_id, created_by, updated_by)
VALUES (0,
        '805b80b4-2b31-4322-8ddc-3533ab44d5b7',
        'f779d895-001b-4109-a02e-bd5b6ddd0535',
        'System',
        'System',
        'system@gmail.com',
        '07046641978',
        'Nigeria',
        'Anambra',
        'Awka',
        '2020202',
        'MALE',
        2,
        0,
        0
);


INSERT INTO users_credential (id, reference_id, user_id, password, created_by, updated_by)
VALUES (
        0,
        'f317a5b3-bdd2-4d48-a21d-d4ef2b6548f3',
        0,
        'june12003',
        0,
        0
       );

INSERT INTO admin (id)
VALUES (
        0
       );

