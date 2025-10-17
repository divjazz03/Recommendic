package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.projection.PatientProfileProjection;
import com.divjazz.recommendic.user.repository.projection.UserProjection;
import com.divjazz.recommendic.user.repository.projection.UserSecurityProjectionDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;


    public Optional<UserSecurityProjectionDTO> findByEmail_Security_Projection(String email ) {
        String sql = """
                SELECT id, email, user_id,user_credential from find_user_sec_detail_by_email(?)
                """;

        UserSecurityProjectionDTO securityProjectionDTO = jdbcTemplate.query(
                sql, (rs) -> {
                    try {
                        return new UserSecurityProjectionDTO(
                           rs.getLong("id"),
                           rs.getString("email"),
                           rs.getString("user_id"),
                           objectMapper.readValue(rs.getString("user_credential"), UserCredential.class)
                        );
                    } catch (JsonProcessingException e) {
                        throw new IllegalStateException("Couldn't parse user credential json from the database",e);
                    }
                },
                email
        );

        return Optional.ofNullable(securityProjectionDTO);
    }

    
    public Optional<String> findByEmail_ReturningCredentialsJsonB(String email) {
        String sql = """
                SELECT find_user_credentials_by_email(?)
                """;

        String credentialJsonB = jdbcTemplate.queryForObject(
                sql, String.class,
                email
        );

        return Optional.ofNullable(credentialJsonB);
    }

    
    public Optional<String> findByUserId_ReturningCredentialsJsonB(String email) {
        String sql = """
                SELECT find_user_credentials_by_userid(?)
                """;

        String credentialJsonB = jdbcTemplate.queryForObject(
                sql, String.class,
                email
        );

        return Optional.ofNullable(credentialJsonB);
    }
    public void setUserLastLogin(UserProjection userProjection) {
        String sql = switch (userProjection.getUserType()) {
            case PATIENT -> """
                UPDATE patient set last_login = now() where user_id = ?;
                """;
            case CONSULTANT -> """
                UPDATE consultant set last_login = now() where user_id = ?;
                """;
            case ADMIN -> """
                UPDATE admin set last_login = now() where user_id = ?;
                """;
        };
        jdbcTemplate.update(sql, userProjection.getUserId());
    }
}
