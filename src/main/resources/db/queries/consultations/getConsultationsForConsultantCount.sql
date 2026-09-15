SELECT count(*)
FROM public.consultations ctn
         LEFT JOIN public.appointments a on ctn.appointment_id = a.id
         LEFT JOIN public.consultants cons on a.consultant_id = cons.id
WHERE cons.user_id = :userId