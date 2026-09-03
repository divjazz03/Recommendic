
SELECT
    a.date as startDate,
    a.reason as reason,
    ss.start_time as startTime,
    ss.end_time as endTime,
    ctn.started_at as startedAt,
    ctn.ended_at as endedAt,
    ctn.status as consultationStatus,
    ctn.channel as consultationChannel,
    ctn.consultation_id as consultationId,
    p.user_id as patientId,
    p.gender as patientGender,
    p.email as patientEmail,
    pp.phone_number as patientPhoneNumber,
    pp.blood_type as patientBloodType,
    pp.username ->> 'full_name' as patientFullName,
    cpd.allergies as patientAllergies,
    cpd.conditions as patientConditions,
    cpd.last_visit as patientLastVisit,
    cpd.insurance as patientInsurance,
    cpd.last_recorded_vitals as patientLastRecordedVitals,
    cpd.patient_reported as patientReported,
    EXTRACT(YEAR FROM AGE(pp.date_of_birth)) as age,
    cons.user_id as consultantId,
    cons.title as consultantTitle,
    cons.gender as consultantGender,
    mc.name as consultantSpecialty,
    cons.experience as consultantExperience,
    cp.username ->> 'full_name' as consultantFullName,
    cp.location as consultantLocation,
    cp.profile_picture ->> 'pictureUrl' as consultantProfilePictureUrl
    FROM public.consultations ctn
    LEFT JOIN public.appointments a on ctn.appointment_id = a.id
    LEFT JOIN public.consultants cons on a.consultant_id = cons.id
    LEFT JOIN public.schedule_slots ss on a.schedule_slot_id = ss.id
    LEFT JOIN public.patients p on a.patient_id = p.id
    LEFT JOIN public.patient_profiles pp on p.id = pp.id
    LEFT JOIN public.consultant_profiles cp on cons.id = cp.id
    LEFT JOIN consultation_patient_data cpd on cpd.id = p.id
    LEFT JOIN public.medical_categories mc on cons.specialization = mc.id
    WHERE cons.user_id = :userId
    ORDER BY a.date DESC, ss.start_time DESC
    LIMIT :limit
    OFFSET :offset
