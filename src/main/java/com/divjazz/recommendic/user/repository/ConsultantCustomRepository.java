package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.repository.projection.ConsultantProfileProjection;
import com.divjazz.recommendic.user.transformer.ConsultantProfileProjectionTransformer;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ConsultantCustomRepository {
    private final JdbcClient jdbcClient;
    private final ConsultantProfileProjectionTransformer consultantProfileProjectionTransformer;

    public Optional<ConsultantProfileProjection> findConsultantProjectionByUserId(String userId) {

        String sql = """
                SELECT
                        c.user_id as userId,
                       cf.userName as userName,
                       c.email as email,
                       cf.phone_number as phoneNumber,
                       cf.date_of_birth as dateOfBirth,
                       c.gender as gender,
                       cf.location as location,
                       cf.address as address,
                       c.specialization as specialty,
                       cf.experience as experience,
                       cf.languages as languages,
                       cf.bio as bio,
                       cf.profile_picture as profilePicture,
                       ce.year as educationYear,
                       ce.id as educationId,
                       ce.institution as educationInstitution,
                       ce.degree as educationDegree,
                       mc.id as specialtyId,
                       mc.name as specialtyName,
                       mc.description as specialtyDesc
                FROM consultant c
                LEFT JOIN consultant_profiles cf on c.id = cf.id
                LEFT JOIN consultant_education ce on c.id = ce.consultant_id
                LEFT JOIN medical_category mc on c.specialization = mc.id
                WHERE c.user_id = :userId
                """;
        return jdbcClient.sql(sql)
                .param("userId", userId)
                .query(rs -> {
                    try {
                        return consultantProfileProjectionTransformer.transform(rs);
                    } catch (JsonProcessingException ex) {
                        throw new IllegalStateException("Problem parsing json string", ex);
                    }
                });
    }
}
