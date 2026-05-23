CREATE TABLE IF NOT EXISTS medical_categories
(
    id          BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    icon        VARCHAR(4),
    medical_category_id TEXT UNIQUE NOT NULL ,
    name        TEXT UNIQUE NOT NULL,
    description TEXT NOT NULL
);


INSERT INTO medical_categories (icon, medical_category_id, name, description)
VALUES ('👶','pediatrics','Pediatrics', 'Children''s health'),
       ('❤️','cardiology','Cardiology','Heart and cardiovascular system'),
       ('🎗️','oncology','Oncology','Cancer treatment'),
       ('🧴','dermatology','Dermatology','Skin, hair, and nails'),
       ('🦴','orthopedics','Orthopedics','Bones, joints, and muscles'),
       ('🧠','neurology','Neurology','Brain and nervous system'),
       ('🤰','gynecology','Gynecology','Women''s reproductive health'),
       ('🧘','psychiatry','Psychiatry','Mental health'),
       ('🦷','dentistry','Dentistry','Dealing with oral health'),
       ('👁️','ophthalmology','Ophthalmology','Eye care and vision'),
       ('🩺','endocrinology','Endocrinology','Hormones and metabolism'),
       ('🫃','gastroenterology','Gastroenterology','Digestive system'),
       ('🫁','pulmonology','Pulmonology','Lungs and respiratory system'),
       ('💧','urology','Urology','Urinary tract and male reproduction'),
       ('👂','ent','ENT','Ear, nose, and throat'),
       ('🏥','general','General Practice','Primary care and wellness');

CREATE INDEX idx_medical_category_id ON medical_categories(medical_category_id);