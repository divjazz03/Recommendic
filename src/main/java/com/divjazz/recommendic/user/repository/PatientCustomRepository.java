package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.repository.projection.PatientProfileProjection;
import com.divjazz.recommendic.user.transformer.PatientProfileProjectionTransformer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PatientCustomRepository {

    private final JdbcClient jdbcClient;
    private final PatientProfileProjectionTransformer patientProfileProjectionTransformer;


    public Optional<PatientProfileProjection> getFullPatientProfileByUserId(String userId) {
        final String sql = """
            SELECT      p.user_id as userId,
                        pf.username as userName,
                        p.user_credential ->> 'email' as email,
                        pf.phone_number as phoneNumber,
                        pf.date_of_birth as dateOfBirth,
                        p.gender as gender,
                        pf.address as address,
                        pf.profile_picture as profilePicture,
                        mc.name as medicalCategoryName,
                        mc.description as medicalDesc
                        FROM patient p
                        LEFT JOIN patient_profiles pf on p.id = pf.id
                        LEFT JOIN medical_category mc on p.id = mc.id
                        WHERE p.user_id = :userId
            """;


        return jdbcClient.sql(sql)
                    .param("userId", userId)
                    .query((rs) -> {
                        try {
                            return patientProfileProjectionTransformer.transform(rs);
                        } catch (JsonProcessingException ex) {
                            throw new IllegalStateException("Problem parsing json string", ex);
                        }
                    });


    }
}
