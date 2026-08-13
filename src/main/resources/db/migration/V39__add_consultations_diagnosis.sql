
CREATE TABLE consultations_diagnosis (
    consultation_id BIGINT REFERENCES consultations(id) NOT NULL ,
    code TEXT NOT NULL ,
    coding_system TEXT NOT NULL ,
    name TEXT NOT NULL,
    type TEXT NOT NULL ,
    status TEXT NOT NULL
)