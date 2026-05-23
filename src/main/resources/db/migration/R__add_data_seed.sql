-- CHILD TABLES
TRUNCATE TABLE
    consultation_reviews,
    consultations,
    consultation_sessions,
    appointments,
    schedule_slots,
    consultant_stats,
    consultant_educations,
    articles,
    comments,
    messages,
    chat_sessions,
    notifications,
    certifications,
    assignments,
    users_confirmations,
    patient_profiles,
    consultant_profiles,
    permissions,
    roles_permissions,
    roles
    RESTART IDENTITY CASCADE;

-- CORE USER TABLES
TRUNCATE TABLE
    patients,
    consultants
    RESTART IDENTITY CASCADE;

-- LOOKUP TABLES
TRUNCATE TABLE
    medical_categories,
    roles
    RESTART IDENTITY CASCADE;

---------------------------------------------------
-- RE-SEED BASE DATA
---------------------------------------------------

-- ==================================== ROLES =====================================================
INSERT INTO roles (name)
VALUES ('ROLE_ADMIN'),
       ('ROLE_PATIENT'),
       ('ROLE_CONSULTANT'),
       ('ROLE_SUPER_ADMIN'),
       ('ROLE_SYSTEM')
ON CONFLICT (name) DO NOTHING;

-- ================================== PERMISSIONS =================================================
-- -------------------------------------------------------------------------------------------------
---                                     SUPER_ADMIN                                               --

INSERT INTO permissions (scope, resource, action)
VALUES ('*', 'any', ARRAY ['*'])
ON CONFLICT DO NOTHING;

INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         CROSS JOIN permissions p
WHERE r.name = 'SUPER_ADMIN'
  AND p.resource = '*'
  AND p.scope = 'any'
ON CONFLICT
    DO NOTHING;


-- =============================== ADMIN PERMISSIONS ================================================
INSERT INTO permissions (scope, resource, action)
VALUES ('any', 'patient', ARRAY ['create', 'read','update', 'delete']),
       ('any', 'consultant', ARRAY ['create', 'read','update', 'delete']),
       ('any', 'appointment', ARRAY ['create', 'read','update', 'cancel']),
       ('any', 'prescription', ARRAY ['create', 'read','update', 'delete']),
       ('any', 'schedule', ARRAY [ 'read','update']),
       ('any', 'medication', ARRAY [ 'read','update']),
       ('own', 'admin', ARRAY [ 'read','update','create', 'delete']),
       ('any', 'message', ARRAY [ 'read','update','create', 'delete']),
       ('any', 'article', ARRAY [ 'read','update','create', 'delete']),
       ('any', 'consultation', ARRAY [ 'read','update','create', 'delete']);
INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         JOIN permissions p ON p.scope = 'any' || (p.scope = 'own' AND p.resource = 'admin')
WHERE r.name = 'ROLE_ADMIN'
ON CONFLICT DO NOTHING;
-- =============================== CONSULTANT PERMISSIONS ================================================
INSERT INTO permissions (scope, resource, action)
VALUES ('assigned', 'patient', ARRAY ['read']),
       ('own', 'consultant', ARRAY ['create', 'read','update', 'delete']),
       ('own', 'profile', ARRAY ['create', 'read','update']),
       ('assigned', 'appointment', ARRAY ['read','update']),
       ('assigned', 'prescription', ARRAY ['create', 'read','update']),
       ('own', 'schedule', ARRAY [ 'read','update','delete','create']),
       ('assigned', 'medication', ARRAY [ 'read','update','create']),
       ('assigned', 'message', ARRAY [ 'read','update','create', 'delete']),
       ('own', 'article', ARRAY [ 'read','update','create', 'delete']),
       ('assigned', 'consultation', ARRAY [ 'read','update'])
ON CONFLICT DO NOTHING;
INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         JOIN permissions p ON (p.scope in ('assigned') OR p.scope = 'own' AND resource != 'admin')
WHERE r.name = 'ROLE_CONSULTANT'
ON CONFLICT DO NOTHING;

-- =============================== PATIENT PERMISSIONS ================================================
INSERT INTO permissions (scope, resource, action)
VALUES ('own', 'patient', ARRAY ['read', 'create','update','delete']),
       ('any', 'consultant', ARRAY ['read']),
       ('own', 'appointment', ARRAY ['read','create']),
       ('own', 'prescription', ARRAY [ 'read']),
       ('any', 'schedule', ARRAY [ 'read']),
       ('own', 'medication', ARRAY [ 'read']),
       ('any', 'article', ARRAY [ 'read']),
       ('assigned', 'consultation', ARRAY [ 'read','update']);
INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         JOIN permissions p
              ON ((p.scope in ('own') AND p.resource IN ('patient', 'profile', 'appointment', 'medication')) OR
                  (p.scope = 'any' AND p.resource IN ('article', 'schedule', 'consultant') AND action = ARRAY ['read']) OR
                  (p.scope = 'assigned' AND p.resource = 'consultation') AND action = ARRAY ['read','update'])
WHERE r.name = 'ROLE_PATIENT'
ON CONFLICT DO NOTHING;



INSERT INTO medical_categories (icon, medical_category_id, name, description)
VALUES ('👶', 'pediatrics', 'Pediatrics', 'Children''s health'),
       ('❤️', 'cardiology', 'Cardiology', 'Heart and cardiovascular system'),
       ('🎗️', 'oncology', 'Oncology', 'Cancer treatment'),
       ('🧴', 'dermatology', 'Dermatology', 'Skin, hair, and nails'),
       ('🦴', 'orthopedics', 'Orthopedics', 'Bones, joints, and muscles'),
       ('🧠', 'neurology', 'Neurology', 'Brain and nervous system'),
       ('🤰', 'gynecology', 'Gynecology', 'Women''s reproductive health'),
       ('🧘', 'psychiatry', 'Psychiatry', 'Mental health'),
       ('🦷', 'dentistry', 'Dentistry', 'Oral health'),
       ('👁️', 'ophthalmology', 'Ophthalmology', 'Eye care'),
       ('🩺', 'endocrinology', 'Endocrinology', 'Hormones'),
       ('🫃', 'gastroenterology', 'Gastroenterology', 'Digestive system'),
       ('🫁', 'pulmonology', 'Pulmonology', 'Respiratory system'),
       ('💧', 'urology', 'Urology', 'Urinary tract'),
       ('👂', 'ent', 'ENT', 'Ear, nose, and throat'),
       ('🏥', 'general', 'General Practice', 'Primary care');

COMMIT;


BEGIN;

---------------------------------------------------
-- BULK SEED DATA (DEV / TEST)
---------------------------------------------------

-- PATIENTS
INSERT INTO patients (user_id, email, user_type, user_stage, gender, role, enabled, created_by, updated_by)
SELECT 'seedpatient' || g,
       'patients' || g || '@example.com',
       'PATIENT',
       'ACTIVE_USER',
       CASE WHEN g % 2 = 0 THEN 'MALE' ELSE 'FEMALE' END,
       (SELECT id FROM roles WHERE name = 'ROLE_PATIENT'),
       TRUE,
       'SEED',
       'SEED'
FROM generate_series(1, 50) g;

-- CONSULTANTS
INSERT INTO consultants (user_id, email, user_type, user_stage, gender, role, specialization, certified, created_by,
                         updated_by)
SELECT 'seedconsultant' || g,
       'consultants' || g || '@example.com',
       'CONSULTANT',
       'ACTIVE_USER',
       CASE WHEN g % 2 = 0 THEN 'MALE' ELSE 'FEMALE' END,
       (SELECT id FROM roles WHERE name = 'ROLE_CONSULTANT'),
       (SELECT id FROM medical_categories ORDER BY random() LIMIT 1),
       TRUE,
       'SEED',
       'SEED'
FROM generate_series(1, 20) g;

-- PATIENT PROFILES
INSERT INTO patient_profiles (id, phone_number, date_of_birth, username, address, created_by, updated_by)
SELECT p.id,
       '080' || (10000000 + p.id),
       DATE '1980-01-01' + ((p.id % 10000)::INTEGER),
       jsonb_build_object('first_name', 'Patient', 'last_name', p.id),
       jsonb_build_object('city', 'Lagos', 'country', 'Nigeria'),
       'SEED',
       'SEED'
FROM patients p;

-- CONSULTANT PROFILES
INSERT INTO consultant_profiles (id, phone_number, username, experience, title, languages, location,
                                 online_consultation_fee, consultation_duration, profile_picture, created_by,
                                 updated_by)
SELECT c.id,
       '070' || (20000000 + c.id),
       jsonb_build_object('first_name', 'Doctor', 'last_name', c.id),
       (c.id % 15) + 1,
       'Consultant',
       ARRAY ['English'],
       'Nigeria',
       10000 + (c.id * 100),
       30,
       jsonb_build_object('picture_url',
                          'https://gravatar.com/avatar/07a4106f7f6b632e6a313b8d2df67f34?s=200&d=robohash&r=x', 'name',
                          'unknown'),
       'SEED',
       'SEED'
FROM consultants c;

-- SCHEDULE SLOTS
INSERT INTO schedule_slots(schedule_id, consultant_id, start_time, end_time, utf_offset,
                           consultation_channels, name, is_active, created_by, updated_by)
SELECT 'seedschedule_slots5' || c.id,
       c.id,
       '09:00',
       '11:00',
       '+01:00',
       ARRAY ['ONLINE','IN_PERSON'],
       'Default Slot',
       TRUE,
       'SEED',
       'SEED'
FROM consultants c;

-- APPOINTMENTS
INSERT INTO appointments (appointment_id, patient_id, consultant_id, schedule_slot_id,
                          date, selected_channel, reason, symptoms, priority,
                          created_by, updated_by)
SELECT 'seedappointment' || p.id,
       p.id,
       c.id,
       s.id,
       CURRENT_DATE + ((p.id % 5)::INTEGER),
       'ONLINE',
       'General Checkup',
       'Fatigue',
       'LOW',
       'SEED',
       'SEED'
FROM patients p
         JOIN consultants c ON c.id = ((p.id % 20) + 1)
         JOIN schedule_slots s ON s.consultant_id = c.id;

-- consultations SESSIONS
INSERT INTO consultation_sessions (session_id, patient_id, consultant_id, patient_status, condition, created_by,
                                   updated_by)
SELECT 'seedconsultationsessionid' || a.id,
       a.patient_id,
       a.consultant_id,
       'STABLE',
       'Routine',
       'SEED',
       'SEED'
FROM appointments a;

-- CONSULTATIONS
INSERT INTO consultations (consultation_id, channel, status, appointment_id, session_id,
                           summary, started_at, ended_at, created_by, updated_by)
SELECT 'seedconsultationid' || a.id,
       'ONLINE',
       'COMPLETED',
       a.id,
       cs.id,
       'Routine consultations',
       now() - interval '20 minutes',
       now(),
       'SEED',
       'SEED'
FROM appointments a
         JOIN consultation_sessions cs ON cs.patient_id = a.patient_id AND cs.consultant_id = a.consultant_id;

COMMIT;

BEGIN;

---------------------------------------------------
-- CHAT SESSIONS (1 per appointments pair)
---------------------------------------------------
INSERT INTO chat_sessions (chat_session_id,
                           patient_id,
                           consultant_id,
                           created_by,
                           updated_by)
SELECT DISTINCT 'seedchatsessionid' || a.id,
                p.user_id,
                c.user_id,
                'SEED',
                'SEED'
FROM appointments a
         JOIN patients p ON p.id = a.patient_id
         JOIN consultants c ON c.id = a.consultant_id;

---------------------------------------------------
-- MESSAGES (3 per chat session, deterministic)
---------------------------------------------------
INSERT INTO messages (sender_id,
                      receiver_id,
                      content,
                      delivered,
                      session,
                      created_by,
                      updated_by)
SELECT cs.patient_id,
       cs.consultant_id,
       'Hello doctor, I need help.',
       TRUE,
       cs.chat_session_id,
       'SEED',
       'SEED'
FROM chat_sessions cs;

INSERT INTO messages (sender_id,
                      receiver_id,
                      content,
                      delivered,
                      session,
                      created_by,
                      updated_by)
SELECT cs.consultant_id,
       cs.patient_id,
       'Hello, please describe your symptoms.',
       TRUE,
       cs.chat_session_id,
       'SEED',
       'SEED'
FROM chat_sessions cs;

INSERT INTO messages (sender_id,
                      receiver_id,
                      content,
                      delivered,
                      session,
                      created_by,
                      updated_by)
SELECT cs.patient_id,
       cs.consultant_id,
       'I feel fatigue and mild pain.',
       TRUE,
       cs.chat_session_id,
       'SEED',
       'SEED'
FROM chat_sessions cs;

---------------------------------------------------
-- CONSULTANT STATS (1 per consultants)
---------------------------------------------------
INSERT INTO consultant_stats (id,
                              patients_helped,
                              successes,
                              response_times,
                              average_response,
                              follow_ups,
                              created_by,
                              updated_by)
SELECT c.id,
       ARRAY [
           format('patients-%s', p.id)
           ],
       ARRAY [
           format('success-%s', c.id)
           ],
       ARRAY [5,7,6],
       6,
       ARRAY ['Follow-up scheduled'],
       'SEED',
       'SEED'
FROM consultants c
         JOIN patients p ON p.id = ((c.id - 1) % 50) + 1;

---------------------------------------------------
-- NOTIFICATIONS
---------------------------------------------------

-- appointments notification
INSERT INTO notifications (notification_id,
                           header,
                           summary,
                           category,
                           seen,
                           user_id,
                           subject_id,
                           created_by,
                           updated_by)
SELECT 'seedappointmentnotificationid' || a.id,
       'appointments Reminder',
       'You have an upcoming appointments',
       'appointments',
       FALSE,
       p.user_id,
       a.appointment_id,
       'SEED',
       'SEED'
FROM appointments a
         JOIN patients p ON p.id = a.patient_id;

-- Chat notification
INSERT INTO notifications (notification_id,
                           header,
                           summary,
                           category,
                           seen,
                           user_id,
                           subject_id,
                           created_by,
                           updated_by)
SELECT 'seedchatnotificationid' || cs.id,
       'New messages',
       'You have a new chat messages',
       'CHAT',
       FALSE,
       cs.consultant_id,
       cs.chat_session_id,
       'SEED',
       'SEED'
FROM chat_sessions cs;

-- consultations notification
INSERT INTO notifications (notification_id,
                           header,
                           summary,
                           category,
                           seen,
                           user_id,
                           subject_id,
                           created_by,
                           updated_by)
SELECT 'seedconsultationsnotifcationid' || c.id,
       'consultations Completed',
       'Your consultations has been completed',
       'consultations',
       FALSE,
       p.user_id,
       c.consultation_id,
       'SEED',
       'SEED'
FROM consultations c
         JOIN appointments a ON a.id = c.appointment_id
         JOIN patients p ON p.id = a.patient_id;

COMMIT;
