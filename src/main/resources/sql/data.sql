DELETE FROM admin_credential WHERE id = 0;
DELETE FROM admin WHERE id = 0;

INSERT INTO admin (id, reference_id, first_name, last_name, email, phone_number, country, state, city, zip_code, gender, created_by, updated_by)
VALUES (0,
    '805b80b4-2b31-4322-8ddc-3533ab44d5b7',
    'System',
    'System',
    'system@gmail.com',
    '07046641978',
    'Nigeria',
    'Anambra',
    'Awka',
    '2020202',
    'MALE',
    0,
    0
);

INSERT INTO admin_credential (id, reference_id, admin_id, password, created_by, updated_by)
VALUES (
        0,
        'f317a5b3-bdd2-4d48-a21d-d4ef2b6548f3',
        0,
        'june12003',
        0,
        0
       );
