package com.divjazz.recommendic.consultation.repository;

import com.divjazz.recommendic.appointment.enums.AppointmentStatus;
import com.divjazz.recommendic.appointment.model.Appointment;
import com.divjazz.recommendic.appointment.model.Schedule;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.consultation.enums.ConsultationStatus;
import com.divjazz.recommendic.global.converter.ZoneOffsetConverter;
import com.divjazz.recommendic.security.UserPrincipal;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.MedicalCategoryEntity;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.*;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class ConsultationCustomRepository {

    private final JdbcClient jdbcClient;
    private final ObjectMapper objectMapper;


    public Stream<ConsultationProjection> findConsultationDetailsByConsultantUserId(String consultantUserId) {
        String sql = """
                SELECT
                       p.user_credential as patient_credential,
                       p.gender as patient_gender,
                       p.user_stage as patient_stage,
                       p.user_id as patient_user_id,
                       p.email as patient_email,
                       p.enabled as patient_enabled,
                       p.medical_categories as patient_medical,
                
                       co.user_credential as consultant_credential,
                       co.gender as consultant_gender,
                       co.user_stage as consultant_stage,
                       co.user_id as consultant_user_id,
                       co.email as consultant_email,
                       co.enabled as consultant_enabled,
                       co.specialization as consultant_specialization,
                       co.certified as consultant_certified,
                
                       ss.consultation_channel as session_channels,
                       ss.end_time as session_end_time,
                       ss.start_time as session_start_time,
                       ss.is_active as session_active,
                       ss.utf_offset as session_zone_offset,
                
                       a.selected_channel as selected_channel,
                       a.status as appointment_status,
                       a.date as appointment_date,
                
                       pf.address as patient_address,
                       pf.phone_number as patient_phone_number,
                       pf.username as patient_username,
                       pf.id as patient_id,
                
                       cf.address as consultant_address,
                       cf.phone_number as consultant_phone_number,
                       cf.username as consultant_username,
                       cf.id as consultant_id,
                       cf.title as consultant_title,
                       cf.location as consultant_location,
                       cf.languages as consultant_languages,
                
                       c.summary as consultation_summary,
                       c.id as consultation_id,
                       c.status as consultation_status,
                       c.channel as consultation_channel,
                       c.ended_at as consultation_ended_at,
                       c.started_at as consultation_started_at,
                
                       r.id as role_id,
                       r.name as role_name,
                       r.permissions as role_permissions,
                
                       mc.id as category_id,
                       mc.name as category_name,
                       mc.description as category_desc
                FROM consultation c
                LEFT JOIN appointment a ON c.appointment_id = a.id
                LEFT JOIN schedule_slot ss ON a.schedule_slot_id = ss.id
                LEFT JOIN patient p ON a.patient_id = p.id
                LEFT JOIN consultant co ON a.consultant_id = co.id
                LEFT JOIN patient_profiles pf ON co.id = pf.id
                LEFT JOIN consultant_profiles cf ON co.id = cf.id
                LEFT JOIN role r ON r.id = co.role
                LEFT JOIN medical_category mc ON mc.id = co.specialization
                WHERE co.user_id = :id
                """;
        return jdbcClient
                .sql(sql)
                .param("id", consultantUserId)
                .query((rs, rowNum) -> getConsultationProjectionFromResultSet(rs))
                .stream();
    }


    public Stream<ConsultationProjection> findConsultationDetailsByPatientUserId(String patientUserId) {
        String sql = """
                SELECT
                       p.user_credential as patient_credential,
                       p.gender as patient_gender,
                       p.user_stage as patient_stage,
                       p.user_id as patient_user_id,
                       p.email as patient_email,
                       p.enabled as patient_enabled,
                       p.medical_categories as patient_medical,
                       
                       co.user_credential as consultant_credential,
                       co.gender as consultant_gender,
                       co.user_stage as consultant_stage,
                       co.user_id as consultant_user_id,
                       co.email as consultant_email,
                       co.enabled as consultant_enabled,
                       co.specialization as consultant_specialization,
                       co.certified as consultant_certified,
                       
                       ss.consultation_channel as session_channels,
                       ss.end_time as session_end_time,
                       ss.start_time as session_start_time,
                       ss.is_active as session_active,
                       ss.utf_offset as session_zone_offset,
                       
                       a.selected_channel as selected_channel,
                       a.status as appointment_status,
                       a.date as appointment_date,
                       
                       pf.address as patient_address,
                       pf.phone_number as patient_phone_number,
                       pf.username as patient_username,
                       pf.id as patient_id,
                       
                       cf.address as consultant_address,
                       cf.phone_number as consultant_phone_number,
                       cf.username as consultant_username,
                       cf.id as consultant_id,
                       cf.title as consultant_title,
                       cf.location as consultant_location,
                       cf.languages as consultant_languages,
                       
                       c.summary as consultation_summary,
                       c.id as consultation_id,
                       c.status as consultation_status,
                       c.channel as consultation_channel,
                       c.ended_at as consultation_ended_at,
                       c.started_at as consultation_started_at,
                       
                       r.id as role_id,
                       r.permissions as role_permissions,
                       r.name as role_name,
                       
                       mc.id as category_id,
                       mc.name as category_name,
                       mc.description as category_desc
                FROM consultation c
                LEFT JOIN appointment a ON c.appointment_id = a.id
                LEFT JOIN schedule_slot ss ON a.schedule_slot_id = ss.id
                LEFT JOIN patient p ON a.patient_id = p.id
                LEFT JOIN consultant co ON a.consultant_id = co.id
                LEFT JOIN patient_profiles pf ON co.id = pf.id
                LEFT JOIN consultant_profiles cf ON co.id = cf.id
                LEFT JOIN role r on r.id = p.role
                LEFT JOIN medical_category mc ON mc.id = co.specialization
                WHERE p.user_id = :id
                """;
        return jdbcClient.sql(sql)
                .param("id", patientUserId)
                .query((rs, rowNum) -> getConsultationProjectionFromResultSet(rs))
                .stream();

    }
    private ConsultationProjection getConsultationProjectionFromResultSet(ResultSet resultSet) throws SQLException {
        Patient patient = (Patient) Patient.builder()
                .userPrincipal(UserPrincipal.builder()
                        .userCredential(objectMapper.convertValue(resultSet.getString("patient_credential"),
                                UserCredential.class))
                        .enabled(resultSet.getBoolean("patient_enabled"))
                        .role(
                                new Role(resultSet.getLong("role_id"),
                                        resultSet.getString("role_name"),
                                        resultSet.getString("role_permissions")
                                ))
                        .email(resultSet.getString("patient_email"))
                        .accountNonExpired(true)
                        .accountNonLocked(true)
                        .build())
                .gender(Gender.valueOf(resultSet.getString("patient_gender")))
                .userType(UserType.PATIENT)
                .userId(resultSet.getString("patient_user_id"))
                .userStage(UserStage.valueOf(resultSet.getString("patient_stage")))
                .build();
        patient.setMedicalCategories((String[]) resultSet.getArray("patient_medical").getArray());

        Consultant consultant = (Consultant) Consultant.builder()
                .userPrincipal(UserPrincipal.builder()
                        .email(resultSet.getString("consultant_email"))
                        .role(new Role(resultSet.getLong("role_id"),
                                resultSet.getString("role_name"),
                                resultSet.getString("role_permissions")
                        ))
                        .userCredential(objectMapper.convertValue(resultSet.getString("consultant_credential"),
                                UserCredential.class))
                        .enabled(resultSet.getBoolean("consultant_enabled"))
                        .accountNonExpired(true)
                        .accountNonLocked(true)
                        .build())
                .gender(Gender.valueOf(resultSet.getString("consultant_gender")))
                .userType(UserType.CONSULTANT)
                .userId(resultSet.getString("consultant_user_id"))
                .userStage(UserStage.valueOf(resultSet.getString("consultant_stage")))
                .build();
        consultant.setSpecialization(new MedicalCategoryEntity(
                resultSet.getLong("category_id"),
                resultSet.getString("category_name"),
                resultSet.getString("category_desc")
        ));
        consultant.setCertified(resultSet.getBoolean("consultant_certified"));

        var schedule = Schedule.builder()
                .consultant(consultant)
                .consultationChannels(Arrays.stream(((String[])resultSet.getArray("session_channels").getArray())).map(ConsultationChannel::valueOf)
                        .toArray(ConsultationChannel[]::new))
                .endTime(resultSet.getTime("session_end_time").toLocalTime())
                .startTime(resultSet.getTime("session_start_time").toLocalTime())
                .isActive(resultSet.getBoolean("session_active"))
                .zoneOffset(new ZoneOffsetConverter().convertToEntityAttribute(resultSet.getString("session_zone_offset")))
                .build();

        var appointment = Appointment.builder()
                .consultationChannel(ConsultationChannel.valueOf(resultSet.getString("selected_channel")))
                .status(AppointmentStatus.valueOf(resultSet.getString("appointment_status")))
                .patient(patient)
                .consultant(consultant)
                .schedule(schedule)
                .appointmentDate(resultSet.getDate("appointment_date").toLocalDate())
                .build();

        var patientProfile = PatientProfile.builder()
                .patient(patient)
                .address(objectMapper.convertValue(resultSet.getString("patient_address"), Address.class))
                .userName(objectMapper.convertValue(resultSet.getString("patient_username"), UserName.class))
                .phoneNumber(resultSet.getString("patient_phone_number"))
                .id(resultSet.getLong("patient_id"))
                .build();

        var consultantProfile = ConsultantProfile.builder()
                .consultant(consultant)
                .address(objectMapper.convertValue(resultSet.getString("consultant_address"), Address.class))
                .userName(objectMapper.convertValue(resultSet.getString("consultant_username"), UserName.class))
                .phoneNumber(resultSet.getString("consultant_phone_number"))
                .title(resultSet.getString("consultant_title"))
                .id(resultSet.getLong("consultant_id"))
                .locationOfInstitution(resultSet.getString("consultant_location"))
                .languages((String[]) resultSet.getArray("consultant_languages").getArray())
                .build();

        return new ConsultationProjection(
                resultSet.getString("consultation_id"),
                appointment,
                patientProfile,
                consultantProfile,
                resultSet.getString("consultation_summary"),
                ConsultationStatus.valueOf(resultSet.getString("consultation_status")),
                ConsultationChannel.valueOf(resultSet.getString("consultation_channel")),
                resultSet.getTimestamp("consultation_ended_at").toLocalDateTime(),
                resultSet.getTimestamp("consultation_started_at").toLocalDateTime()
                );
    }
}
