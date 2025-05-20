DELETE FROM users;




INSERT INTO users (id, user_id, username, email, phone_number, address, user_type,gender,role, created_by, updated_by, user_stage,dtype)
VALUES (0,
         'f317a5b3-bdd3-4d48-a21d-d4ef2b6548f3',
        '{
            "firstname": "",
            "lastname": ""
        }'::jsonb,
        'system@gmail.com',
        '07046641978',
        '{
            "city": "",
            "state": "",
            "country": ""
        }'::jsonb,
        'ADMIN',
        'MALE',
        'SYSTEM',
        0,
        0,
        'ACTIVE_USER',
        'Admin'
);

