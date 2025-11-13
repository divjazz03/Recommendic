package com.divjazz.recommendic.appointment.repository;

import com.divjazz.recommendic.appointment.domain.AppointmentPriority;
import com.divjazz.recommendic.appointment.dto.ConsultantAppointmentDTO;
import com.divjazz.recommendic.appointment.dto.PatientAppointmentDTO;
import com.divjazz.recommendic.appointment.enums.AppointmentStatus;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.global.general.ResponseWithCount;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class AppointmentCustomRepository {
    private final JdbcClient jdbcClient;


    public ResponseWithCount<PatientAppointmentDTO> retrievePatientAppointments(long patientId, Pageable pageable) {

        var sql = """
                SELECT a.appointment_id as id,
                       c.user_id as consultantId,
                     cf.username ->> 'full_name' as consultantName,
                     mc.name as specialty,
                     a.date as date,
                     s.start_time as time,
                     concat(extract(hour from (s.end_time::time - s.start_time::time)), 'hours ',
                            extract(minute from (s.end_time::time - s.start_time::time)), 'minutes'
                     ) as duration,
                    a.selected_channel as channel,
                    c.email as email,
                    coalesce(cf.location, 'NOT PROVIDED') as location,
                    coalesce(cf.phone_number, 'NOT_PROVIDED') as phoneNumber,
                    a.status as status,
                    coalesce(a.note, 'NOT_PROVIDED') as notes
                FROM appointment a
                LEFT JOIN patient p on a.patient_id = p.id
                LEFT JOIN patient_profiles pf on p.id = pf.id
                LEFT JOIN consultant c on a.consultant_id = c.id
                LEFT JOIN consultant_profiles cf on c.id = cf.id
                LEFT JOIN medical_category mc on c.specialization = mc.id
                LEFT JOIN schedule_slot s on a.schedule_slot_id = s.id
                WHERE a.patient_id = :patientId
                LIMIT :limit
                OFFSET ((:page + 1) * :limit) - :limit
                """;

        var countSql = """
                SELECT count(*)
                FROM appointment a
                LEFT JOIN patient p on a.patient_id = p.id
                LEFT JOIN patient_profiles pf on p.id = pf.id
                LEFT JOIN consultant c on a.consultant_id = c.id
                LEFT JOIN consultant_profiles cf on c.id = cf.id
                LEFT JOIN medical_category mc on c.specialization = mc.id
                LEFT JOIN schedule_slot s on a.schedule_slot_id = s.id
                WHERE a.patient_id = :patientId
                """;

        int total = jdbcClient.sql(countSql)
                .param("patientId", patientId)
                .query((rs, rsNum) -> rs.getInt(1))
                .single();

        RowMapper<PatientAppointmentDTO> rowMapper = (rs, index) -> {
            var id = rs.getString("id");
            var consultantName = rs.getString("consultantName");
            var consultantId = rs.getString("consultantId");
            var specialty = rs.getString("specialty");
            var date = rs.getDate("date").toLocalDate();
            var time = rs.getTime("time").toLocalTime();
            var duration = rs.getString("duration");
            var selectedChannel = rs.getString("channel");
            var location = rs.getString("location");
            var phoneNumber = rs.getString("phoneNumber");
            var status = rs.getString("status");
            var notes = rs.getString("notes");
            var email = rs.getString("email");
            return new PatientAppointmentDTO(
                    id,
                    consultantId,
                    consultantName,
                    specialty,
                    email,
                    date.format(DateTimeFormatter.ISO_DATE),
                    time.format(DateTimeFormatter.ISO_TIME),
                    duration,
                    ConsultationChannel.valueOf(selectedChannel),
                    location,
                    phoneNumber,
                    AppointmentStatus.valueOf(status),
                    notes,
                    null
            );
        };
        var patientAppointments = jdbcClient.sql(sql)
                .param("patientId", patientId)
                .param("limit", pageable.getPageSize())
                .param("page", pageable.getPageNumber())
                .query(rowMapper)
                .set();

        return new ResponseWithCount<>(
                patientAppointments,
                total
        );


    }

    public ResponseWithCount<ConsultantAppointmentDTO> retrieveConsultantAppointments(long consultantId, Pageable pageable) {
        var sql = """
                SELECT a.appointment_id as id,
                       p.user_id as patientId,
                     pf.username ->> 'full_name' as patientName,
                     extract(year from age(current_date, pf.date_of_birth)) as patientAge,
                     a.date as date,
                     s.start_time as time,
                     concat(extract(hour from (s.end_time::time - s.start_time::time)), 'hours ',
                            extract(minute from (s.end_time::time - s.start_time::time)), 'minutes'
                     ) as duration,
                    a.selected_channel as channel,
                    coalesce(cf.location, 'none') as location,
                    coalesce(pf.phone_number, 'none') as phoneNumber,
                    p.email as email,
                    a.status as status,
                    coalesce(a.note, 'none') as notes,
                    a.reason as reason,
                    date(a.created_at)  as requestedDate,
                    a.cancellation_reason as cancellation_reason,
                    a.priority as priority,
                    coalesce(symptoms, 'None Reported') as symptoms
                FROM appointment a
                LEFT JOIN patient p on a.patient_id = p.id
                LEFT JOIN patient_profiles pf on p.id = pf.id
                LEFT JOIN consultant c on a.consultant_id = c.id
                LEFT JOIN consultant_profiles cf on c.id = cf.id
                LEFT JOIN medical_category mc on c.specialization = mc.id
                LEFT JOIN schedule_slot s on a.schedule_slot_id = s.id
                WHERE a.consultant_id = :consultantId
                LIMIT :limit
                OFFSET ((:page + 1) * :limit) - :limit
                """;

        var countSql = """
                SELECT count(*)
                FROM appointment a
                LEFT JOIN patient p on a.patient_id = p.id
                LEFT JOIN patient_profiles pf on p.id = pf.id
                LEFT JOIN consultant c on a.consultant_id = c.id
                LEFT JOIN consultant_profiles cf on c.id = cf.id
                LEFT JOIN medical_category mc on c.specialization = mc.id
                LEFT JOIN schedule_slot s on a.schedule_slot_id = s.id
                WHERE a.consultant_id = :consultantId
                """;

        int total = jdbcClient.sql(countSql)
                .param("consultantId", consultantId)
                .query((rs,rowNum) -> rs.getInt(1))
                .single();
        RowMapper<ConsultantAppointmentDTO> mapper = (rs, rowNum) -> {
            var id = rs.getString("id");
            var patientId = rs.getString("patientId");
            var patientName = rs.getString("patientName");
            var patientAge = rs.getString("patientAge");
            var email = rs.getString("email");
            var date = rs.getDate("date").toLocalDate();
            var time = rs.getTime("time").toLocalTime();
            var duration = rs.getString("duration");
            var channel = rs.getString("channel");
            var location = rs.getString("location");
            var phoneNumber = rs.getString("phoneNumber");
            var status = rs.getString("status");
            var notes = rs.getString("notes");
            var reason = rs.getString("reason");
            var requestedDate = rs.getDate("requestedDate").toLocalDate();
            var priority = rs.getString("priority");
            var cancellationReason = rs.getString("cancellation_reason");
            var symptoms = rs.getString("symptoms");
            return new ConsultantAppointmentDTO(
                    id,
                    patientId,
                    patientName,
                    patientAge,
                    phoneNumber,
                    email,
                    date.format(DateTimeFormatter.ISO_DATE),
                    time.format(DateTimeFormatter.ISO_TIME),
                    duration,
                    ConsultationChannel.valueOf(channel),
                    location,
                    reason,
                    symptoms,
                    null,
                    requestedDate.format(DateTimeFormatter.ISO_DATE),
                    AppointmentPriority.valueOf(priority),
                    notes,
                    cancellationReason,
                    AppointmentStatus.valueOf(status)
                    );
        };

        var consultantAppointments =  jdbcClient.sql(sql)
                .param("consultantId", consultantId)
                .param("limit", pageable.getPageSize())
                .param("page", pageable.getPageNumber())
                .query(mapper)
                .set();

        return new ResponseWithCount<>(consultantAppointments, total);
    }
}
