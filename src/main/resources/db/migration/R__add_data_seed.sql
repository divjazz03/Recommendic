-- CHILD TABLES
TRUNCATE TABLE
    consultation_review,
    consultation,
    consultation_session,
    appointment,
    schedule_slot,
    consultant_stat,
    consultant_education,
    consultant_recommendation,
    article_recommendation,
    article,
    comment,
    message,
    chat_session,
    app_notification,
    search,
    certification,
    assignment,
    users_confirmation,
    patient_notification_setting,
    consultant_notification_setting,
    patient_security_setting,
    consultant_security_setting,
    consultation_patient_data,
    patient_medical_category,
    patient_profiles,
    consultant_profiles
    RESTART IDENTITY CASCADE;

-- CORE USER TABLES
TRUNCATE TABLE
    patient,
    consultant
    RESTART IDENTITY CASCADE;

-- LOOKUP TABLES
TRUNCATE TABLE
    medical_category,
    role
    RESTART IDENTITY CASCADE;

---------------------------------------------------
-- RE-SEED BASE DATA
---------------------------------------------------

INSERT INTO role (name)
VALUES
    ('ROLE_ADMIN'),
    ('ROLE_PATIENT'),
    ('ROLE_CONSULTANT'),
    ('ROLE_SUPER_ADMIN'),
    ('ROLE_SYSTEM');

INSERT INTO medical_category (icon, medical_category_id, name, description)
VALUES
    ('👶','pediatrics','Pediatrics','Children''s health'),
    ('❤️','cardiology','Cardiology','Heart and cardiovascular system'),
    ('🎗️','oncology','Oncology','Cancer treatment'),
    ('🧴','dermatology','Dermatology','Skin, hair, and nails'),
    ('🦴','orthopedics','Orthopedics','Bones, joints, and muscles'),
    ('🧠','neurology','Neurology','Brain and nervous system'),
    ('🤰','gynecology','Gynecology','Women''s reproductive health'),
    ('🧘','psychiatry','Psychiatry','Mental health'),
    ('🦷','dentistry','Dentistry','Oral health'),
    ('👁️','ophthalmology','Ophthalmology','Eye care'),
    ('🩺','endocrinology','Endocrinology','Hormones'),
    ('🫃','gastroenterology','Gastroenterology','Digestive system'),
    ('🫁','pulmonology','Pulmonology','Respiratory system'),
    ('💧','urology','Urology','Urinary tract'),
    ('👂','ent','ENT','Ear, nose, and throat'),
    ('🏥','general','General Practice','Primary care');

COMMIT;


BEGIN;

---------------------------------------------------
-- BULK SEED DATA (DEV / TEST)
---------------------------------------------------

-- PATIENTS
INSERT INTO patient (email, user_type, user_stage, gender, role, enabled, created_by, updated_by)
SELECT
    'patient' || g || '@example.com',
    'PATIENT',
    'ACTIVE_USER',
    CASE WHEN g % 2 = 0 THEN 'MALE' ELSE 'FEMALE' END,
    (SELECT id FROM role WHERE name='ROLE_PATIENT'),
    TRUE,
    'SEED','SEED'
FROM generate_series(1,50) g;

-- CONSULTANTS
INSERT INTO consultant (
    email, user_type, user_stage, gender, role, specialization, certified, created_by, updated_by
)
SELECT
    'consultant' || g || '@example.com',
    'CONSULTANT',
    'ACTIVE_USER',
    CASE WHEN g % 2 = 0 THEN 'MALE' ELSE 'FEMALE' END,
    (SELECT id FROM role WHERE name='ROLE_CONSULTANT'),
    (SELECT id FROM medical_category ORDER BY random() LIMIT 1),
    TRUE,
    'SEED','SEED'
FROM generate_series(1,20) g;

-- PATIENT PROFILES
INSERT INTO patient_profiles (
    id, phone_number, date_of_birth, username, address, created_by, updated_by
)
SELECT
    p.id,
    '080' || (10000000 + p.id),
    DATE '1980-01-01' + ((p.id % 10000)::INTEGER),
    jsonb_build_object('first_name','Patient','last_name',p.id),
    jsonb_build_object('city','Lagos','country','Nigeria'),
    'SEED','SEED'
FROM patient p;

-- CONSULTANT PROFILES
INSERT INTO consultant_profiles (
    id, phone_number, username, experience, title, languages, location,
    online_consultation_fee, consultation_duration,profile_picture, created_by, updated_by
)
SELECT
    c.id,
    '070' || (20000000 + c.id),
    jsonb_build_object('first_name','Doctor','last_name',c.id),
    (c.id % 15) + 1,
    'Consultant',
    ARRAY['English'],
    'Nigeria',
    10000 + (c.id * 100),
    30,
    jsonb_build_object('picture_url', 'https://gravatar.com/avatar/07a4106f7f6b632e6a313b8d2df67f34?s=200&d=robohash&r=x', 'name','unknown'),
    'SEED','SEED'
FROM consultant c;

-- SCHEDULE SLOTS
INSERT INTO schedule_slot (
    consultant_id, start_time, end_time, utf_offset,
    consultation_channel, name, is_active, created_by, updated_by
)
SELECT
    c.id,
    '09:00','11:00','+01:00',
    ARRAY['ONLINE'::session_channel,'IN_PERSON'::session_channel],
    'Default Slot',
    TRUE,
    'SEED','SEED'
FROM consultant c;

-- APPOINTMENTS
INSERT INTO appointment (
    patient_id, consultant_id, schedule_slot_id,
    date, selected_channel, reason, symptoms, priority,
    created_by, updated_by
)
SELECT
    p.id,
    c.id,
    s.id,
    CURRENT_DATE + ((p.id % 5)::INTEGER),
    'ONLINE',
    'General Checkup',
    'Fatigue',
    'LOW',
    'SEED','SEED'
FROM patient p
         JOIN consultant c ON c.id = ((p.id % 20) + 1)
         JOIN schedule_slot s ON s.consultant_id = c.id;

-- CONSULTATION SESSIONS
INSERT INTO consultation_session (
    patient_id, consultant_id, patient_status, condition, created_by, updated_by
)
SELECT
    a.patient_id,
    a.consultant_id,
    'STABLE',
    'Routine',
    'SEED','SEED'
FROM appointment a;

-- CONSULTATIONS
INSERT INTO consultation (
    channel, status, appointment_id, session_id,
    summary, started_at, ended_at, created_by, updated_by
)
SELECT
    'ONLINE',
    'COMPLETED',
    a.id,
    cs.id,
    'Routine consultation',
    now() - interval '20 minutes',
    now(),
    'SEED','SEED'
FROM appointment a
         JOIN consultation_session cs ON cs.patient_id = a.patient_id AND cs.consultant_id = a.consultant_id;

COMMIT;

BEGIN;

---------------------------------------------------
-- CHAT SESSIONS (1 per appointment pair)
---------------------------------------------------
INSERT INTO chat_session (
    patient_id,
    consultant_id,
    created_by,
    updated_by
)
SELECT DISTINCT
    p.user_id,
    c.user_id,
    'SEED','SEED'
FROM appointment a
         JOIN patient p ON p.id = a.patient_id
         JOIN consultant c ON c.id = a.consultant_id;

---------------------------------------------------
-- MESSAGES (3 per chat session, deterministic)
---------------------------------------------------
INSERT INTO message (
    sender_id,
    receiver_id,
    content,
    delivered,
    session,
    created_by,
    updated_by
)
SELECT
    cs.patient_id,
    cs.consultant_id,
    'Hello doctor, I need help.',
    TRUE,
    cs.chat_session_id,
    'SEED','SEED'
FROM chat_session cs;

INSERT INTO message (
    sender_id,
    receiver_id,
    content,
    delivered,
    session,
    created_by,
    updated_by
)
SELECT
    cs.consultant_id,
    cs.patient_id,
    'Hello, please describe your symptoms.',
    TRUE,
    cs.chat_session_id,
    'SEED','SEED'
FROM chat_session cs;

INSERT INTO message (
    sender_id,
    receiver_id,
    content,
    delivered,
    session,
    created_by,
    updated_by
)
SELECT
    cs.patient_id,
    cs.consultant_id,
    'I feel fatigue and mild pain.',
    TRUE,
    cs.chat_session_id,
    'SEED','SEED'
FROM chat_session cs;

---------------------------------------------------
-- CONSULTANT STATS (1 per consultant)
---------------------------------------------------
INSERT INTO consultant_stat (
    id,
    patients_helped,
    successes,
    response_times,
    average_response,
    follow_ups,
    created_by,
    updated_by
)
SELECT
    c.id,
    ARRAY[
        format('patient-%s', p.id)
        ],
    ARRAY[
        format('success-%s', c.id)
        ],
    ARRAY[5,7,6],
    6,
    ARRAY['Follow-up scheduled'],
    'SEED','SEED'
FROM consultant c
         JOIN patient p ON p.id = ((c.id - 1) % 50) + 1;

---------------------------------------------------
-- CONSULTANT RECOMMENDATIONS (patients → consultants)
---------------------------------------------------
INSERT INTO consultant_recommendation (
    patient_id,
    consultant_id,
    created_by,
    updated_by
)
SELECT
    p.id,
    ((p.id - 1) % 20) + 1,
    'SEED','SEED'
FROM patient p;

---------------------------------------------------
-- ARTICLE RECOMMENDATIONS (patients → articles)
---------------------------------------------------
INSERT INTO article_recommendation (
    patient_id,
    article_id,
    created_by,
    updated_by
)
SELECT
    p.id,
    a.id,
    'SEED','SEED'
FROM patient p
         JOIN article a ON a.id = ((p.id - 1) % (SELECT COUNT(*) FROM article)) + 1;

---------------------------------------------------
-- NOTIFICATIONS
---------------------------------------------------

-- Appointment notification
INSERT INTO app_notification (
    header,
    summary,
    category,
    seen,
    user_id,
    subject_id,
    created_by,
    updated_by
)
SELECT
    'Appointment Reminder',
    'You have an upcoming appointment',
    'APPOINTMENT',
    FALSE,
    p.user_id,
    a.appointment_id,
    'SEED','SEED'
FROM appointment a
         JOIN patient p ON p.id = a.patient_id;

-- Chat notification
INSERT INTO app_notification (
    header,
    summary,
    category,
    seen,
    user_id,
    subject_id,
    created_by,
    updated_by
)
SELECT
    'New Message',
    'You have a new chat message',
    'CHAT',
    FALSE,
    cs.consultant_id,
    cs.chat_session_id,
    'SEED','SEED'
FROM chat_session cs;

-- Consultation notification
INSERT INTO app_notification (
    header,
    summary,
    category,
    seen,
    user_id,
    subject_id,
    created_by,
    updated_by
)
SELECT
    'Consultation Completed',
    'Your consultation has been completed',
    'CONSULTATION',
    FALSE,
    p.user_id,
    c.consultation_id,
    'SEED','SEED'
FROM consultation c
         JOIN appointment a ON a.id = c.appointment_id
         JOIN patient p ON p.id = a.patient_id;

INSERT INTO consultant_security_setting (
                consultant_id,
                  multi_factor_auth_enabled,
session_timeout_min,
login_alerts_enabled,
created_by, updated_by)
SELECT id,
       false,
       30,
       false,
       'SEED','SEED'
    FROM consultant c ;

INSERT INTO patient_security_setting (
    patient_id,
    multi_factor_auth_enabled,
    session_timeout_min,
    login_alerts_enabled,
    created_by, updated_by)
SELECT id,
       false,
       30,
       false,
       'SEED','SEED'
FROM patient c ;

COMMIT;
