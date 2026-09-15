SELECT count(*)
FROM public.consultations ctn
         LEFT JOIN public.appointments a on ctn.appointment_id = a.id
         LEFT JOIN public.patients p on a.consultant_id = p.id
WHERE p.user_id = :userId