package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.dto.PatientMedicalData;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.repository.projection.PatientProfileProjection;
import com.divjazz.recommendic.user.transformer.PatientProfileProjectionTransformer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PatientCustomRepository {

    private final JdbcClient jdbcClient;
    private final ObjectMapper objectMapper;


    public Optional<PatientProfileProjection> getFullPatientProfileByUserId(String userId) {
        final String sql = """
            SELECT      p.user_id as userId,
                        pf.username as userName,
                        p.email as email,
                        pf.phone_number as phoneNumber,
                        pf.date_of_birth as dateOfBirth,
                        p.gender as gender,
                        pf.address as address,
                        pf.profile_picture as profilePicture,
                        mc.name as medicalCategoryName,
                        mc.description as medicalDesc,
                        pf.blood_type as bloodType,
                        pf.medical_history as medicalHistory,
                        pf.lifestyle_info as lifeStyleInfo
                        FROM patient p
                        LEFT JOIN patient_profiles pf on p.id = pf.id
                        LEFT JOIN patient_medical_category pmc on p.id = pmc.patient_id
                        LEFT JOIN medical_category mc on mc.id = pmc.medical_category_id
                        WHERE p.user_id = :userId
            """;


        return jdbcClient.sql(sql)
                    .param("userId", userId)
                    .query((rs) -> {
                        try {
                            return PatientProfileProjectionTransformer.transform(rs, objectMapper);
                        } catch (JsonProcessingException ex) {
                            throw new IllegalStateException("Problem parsing json string", ex);
                        }
                    });


    }

    public Optional<PatientMedicalData> getPatientMedicalDataById(String id) {
        String sql = """
                SELECT p.user_id as id,
                       pp.username ->> 'full_name' as name,
                       p.gender as gender,
                       age(current_date, pp.date_of_birth) as age
      
                FROM patient p
                LEFT JOIN patient_profiles pp on p.id = pp.id
                WHERE p.user_id = :id
                """;

        var result = jdbcClient.sql(sql)
                .param("id", id)
                .query(PatientCustomRepository::resultSetToMedicalData);
        if (ObjectUtils.isEmpty(result)) {
            return Optional.empty();
        }
        return Optional.of(result);

    }

    private static PatientMedicalData resultSetToMedicalData (ResultSet rs) throws SQLException {
        String patientId = rs.getString("id");
        String fullName = rs.getString("name");
        Gender gender = Gender.valueOf(rs.getString("gender"));
        String age = rs.getString("age");

        return new PatientMedicalData(patientId,null, fullName,age, gender, "MRNOOPII");
    }
}
