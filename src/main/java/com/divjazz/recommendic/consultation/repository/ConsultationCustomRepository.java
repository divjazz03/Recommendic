package com.divjazz.recommendic.consultation.repository;

import com.divjazz.recommendic.consultation.dto.ConsultantData;
import com.divjazz.recommendic.consultation.dto.ConsultationResponse;
import com.divjazz.recommendic.consultation.dto.PatientData;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.consultation.enums.ConsultationStatus;
import com.divjazz.recommendic.global.SqlQueryLoader;
import com.divjazz.recommendic.user.dto.PatientMedicalData;
import com.divjazz.recommendic.user.enums.BloodType;
import com.divjazz.recommendic.user.enums.Gender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;


@Slf4j
@Repository
@RequiredArgsConstructor
public class ConsultationCustomRepository {

    private static final String PATH_TO_GET_CONSULTATIONS_BY_CONSULTANT =
            "consultations/getConsultationsForConsultant.sql";
    private static final String PATH_TO_GET_CONSULTATIONS_BY_PATIENT =
            "consultations/getConsultationsForPatient.sql";
    private static final String PATH_TO_GET_CONSULTATIONS_BY_CONSULTANT_COUNT =
            "consultations/getConsultationsForConsultantCount.sql";
    private static final String PATH_TO_GET_CONSULTATIONS_BY_PATIENT_COUNT =
            "consultations/getConsultationsForPatientCount.sql";
    private final JdbcClient jdbcClient;
    private final SqlQueryLoader sqlQueryLoader;
    private final ObjectMapper objectMapper;


    public Set<PatientMedicalData> getPatientMedicalDataFromOngoingConsultations() {
        var sql = """
                SELECT pp.username ->> 'full_name' as name,
                       c.consultation_id as consultationId,
                       p.gender as gender,
                       p.user_id as id,
                       age(current_date, pp.date_of_birth) as age
                FROM consultations c
                LEFT JOIN appointments a on c.appointment_id = a.id
                LEFT JOIN patients p on a.patient_id = p.id
                LEFT JOIN patient_profiles pp on p.id = pp.id
                WHERE c.status = 'ONGOING'
                """;

        return jdbcClient.sql(sql).query(this::mapRowToPatientMedicalData).set();
    }

    private PatientMedicalData mapRowToPatientMedicalData(ResultSet rs, int rowNumber) throws SQLException {
        String patientId = rs.getString("id");
        String fullName = rs.getString("name");
        Gender gender = Gender.valueOf(rs.getString("gender"));
        String age = rs.getString("age");
        String consultationId = rs.getString("consultationId");
        return new PatientMedicalData(patientId, consultationId, fullName, age, gender, "MRNOOPII");
    }

    public Page<ConsultationResponse> getConsultationsByConsultationIdPaged(String consultantId, Pageable pageable) {
        String sql = sqlQueryLoader.load(PATH_TO_GET_CONSULTATIONS_BY_CONSULTANT);
        String countSql = sqlQueryLoader.load(PATH_TO_GET_CONSULTATIONS_BY_CONSULTANT_COUNT);
        int total = jdbcClient.sql(countSql).param("userId", consultantId)
                .query((rs, rowNum) -> rs.getInt(1)).single();
        List<ConsultationResponse> result = jdbcClient
                .sql(sql)
                .param("offset", pageable.getOffset())
                .param("limit", pageable.getPageSize())
                .param("userId", consultantId)
                .query(this::mapRowToConsultationResponse).list();
        return new PageImpl<>(result, pageable, total);

    }

    public Page<ConsultationResponse> getConsultationsByPatientIdPaged(String patientId, Pageable pageable) {
        String sql = sqlQueryLoader.load(PATH_TO_GET_CONSULTATIONS_BY_PATIENT);
        String countSql = sqlQueryLoader.load(PATH_TO_GET_CONSULTATIONS_BY_PATIENT_COUNT);
        int total = jdbcClient.sql(countSql).param("userId", patientId)
                .query((rs, rowNum) -> rs.getInt(1)).single();
        List<ConsultationResponse> result = jdbcClient
                .sql(sql)
                .param("offset", pageable.getOffset())
                .param("limit", pageable.getPageSize())
                .param("userId", patientId)
                .query(this::mapRowToConsultationResponse).list();
        return new PageImpl<>(result, pageable, total);

    }

    private List<String> getStringArray(ResultSet rs, String column)
            throws SQLException {

        Array array = rs.getArray(column);

        if (array == null) {
            return List.of();
        }

        return List.of((String[]) array.getArray());
    }

    private ConsultationResponse mapRowToConsultationResponse(ResultSet rs, int rowNumber) throws SQLException {
        LocalDate startDate = Optional.ofNullable(rs.getDate("startDate"))
                .map(Date::toLocalDate)
                .orElse(null);
        LocalTime startTime = Optional.ofNullable(rs.getTime("startTime"))
                .map(Time::toLocalTime)
                .orElse(null);
        LocalTime endTime = rs.getTime("endTime").toLocalTime();
        String startDateTime = null;
        String endDateTime = null;
        if (Objects.nonNull(startDate)) {
            if (Objects.nonNull(startTime)) {
                startDateTime = LocalDateTime.of(startDate, startTime).toString();
            }
            if (Objects.nonNull(endTime)) {
                endDateTime = LocalDateTime.of(startDate, endTime).toString();
            }
        }

        String startedAt = Optional.ofNullable(rs.getTimestamp("startedAt"))
                .map(Timestamp::toLocalDateTime)
                .map(LocalDateTime::toString)
                .orElse(null);
        String endedAt = Optional.ofNullable(rs.getTimestamp("endedAt"))
                .map(Timestamp::toLocalDateTime)
                .map(LocalDateTime::toString)
                .orElse(null);
        ConsultationStatus consultationStatus = ConsultationStatus
                .valueOf(rs.getString("consultationStatus"));
        ConsultationChannel consultationChannel = ConsultationChannel
                .valueOf(rs.getString("consultationChannel"));
        String consultationId = rs.getString("consultationId");
        String patientId = rs.getString("patientId");
        Gender patientGender = Gender.valueOf(rs.getString("patientGender"));
        String patientEmail = rs.getString("patientEmail");
        String patientPhone = rs.getString("patientPhoneNumber");
        BloodType patientBloodType = BloodType.fromValue(rs.getString("patientBloodType"));
        String patientFullName = rs.getString("patientFullName");
        List<String> allergies = getStringArray(rs, "patientAllergies");
        List<String> conditions = getStringArray(rs, "patientConditions");
        String lastVisit = Optional.ofNullable(rs.getTimestamp("patientLastVisit"))
                .map(Timestamp::toLocalDateTime)
                .map(LocalDateTime::toString)
                .orElse(null);
        String insurance = rs.getString("patientInsurance");
        PatientData.RecordedVitals lastRecordedVitals;
        PatientData.PatientReported patientReported;
        lastRecordedVitals = Optional.ofNullable(rs.getString("patientLastRecordedVitals"))
                .map(value -> {
                    try {
                        return objectMapper.readValue(value, PatientData.RecordedVitals.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElse(null);
        patientReported = Optional.ofNullable(rs.getString("patientReported"))
                .map(value -> {
                    try {
                        return objectMapper.readValue(value, PatientData.PatientReported.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElse(null);

        String age = rs.getString("age");
        String consultantId = rs.getString("consultantId");
        String consultantTitle = rs.getString("consultantTitle");
        Gender consultantGender = Gender.valueOf(rs.getString("consultantGender"));
        String consultantSpecialty = rs.getString("consultantSpecialty");
        int consultantExperience = rs.getInt("consultantExperience");
        String consultantFullName = rs.getString("consultantFullName");
        String consultantLocation = rs.getString("consultantLocation");
        String consultantProfileUrl = rs.getString("consultantProfilePictureUrl");

        String reason = rs.getString("reason");
        return ConsultationResponse.builder()
                .consultationId(consultationId)
                .consultantData(ConsultantData.builder()
                        .name(consultantFullName)
                        .title(consultantTitle)
                        .image(consultantProfileUrl)
                        .id(consultantId)
                        .experience(consultantExperience)
                        .gender(consultantGender)
                        .location(consultantLocation)
                        .specialty(consultantSpecialty)
                        .rating(4.7f)
                        .build())
                .patientData(PatientData.builder()
                        .email(patientEmail)
                        .lastRecordedVitals(lastRecordedVitals)
                        .phoneNumber(patientPhone)
                        .bloodType(patientBloodType)
                        .gender(patientGender)
                        .name(patientFullName)
                        .lastVisit(lastVisit)
                        .patientReported(patientReported)
                        .allergies(allergies)
                        .conditions(conditions)
                        .age(age)
                        .insurance(insurance)
                        .build())
                .consultantId(consultantId)
                .consultantName(consultantFullName)
                .channel(consultationChannel)
                .endedAt(endedAt)
                .endTime(endDateTime)
                .patientName(patientFullName)
                .status(consultationStatus)
                .startedAt(startedAt)
                .patientId(patientId)
                .startTime(startDateTime)
                .reason(reason)
                .build();
    }


}
