package com.divjazz.recommendic.consultation.repository;

import com.divjazz.recommendic.appointment.enums.AppointmentStatus;
import com.divjazz.recommendic.appointment.model.Appointment;
import com.divjazz.recommendic.appointment.model.Schedule;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.consultation.enums.ConsultationStatus;
import com.divjazz.recommendic.consultation.model.Consultation;
import com.divjazz.recommendic.global.converter.ZoneOffsetConverter;
import com.divjazz.recommendic.security.UserPrincipal;
import com.divjazz.recommendic.user.dto.PatientMedicalData;
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
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class ConsultationCustomRepository {

    private final JdbcClient jdbcClient;

    public Set<PatientMedicalData> getPatientMedicalDataFromOngoingConsultations() {
        var sql = """
                SELECT pp.username ->> 'full_name' as name,
                       c.consultation_id as consultationId,
                       p.gender as gender,
                       p.user_id as id,
                       age(current_date, pp.date_of_birth) as age
                FROM consultation c
                LEFT JOIN appointment a on c.appointment_id = a.id
                LEFT JOIN patient p on a.patient_id = p.id
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
        return new PatientMedicalData(patientId,consultationId, fullName,age, gender, "MRNOOPII");
    }


}
