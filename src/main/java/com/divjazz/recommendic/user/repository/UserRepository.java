package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.repository.projection.UserSecurityProjectionDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public Optional<UserSecurityProjectionDTO> findByEmail_Security_Projection(String email ) {
        String sql = """
                SELECT * FROM find_user_sec_detail_by_email(?);
                """;

        UserSecurityProjectionDTO securityProjectionDTO = jdbcTemplate.query(
                sql, (rs) -> {
                    return new UserSecurityProjectionDTO(
                       rs.getLong("id"),
                       rs.getString("email"),
                       rs.getString("user_id"),
                       rs.getString("user_credential")
                    );
                },
                email
        );

        return Optional.ofNullable(securityProjectionDTO);
    }

    
    public Optional<String> findByEmail_ReturningCredentialsJsonB(String email) {
        String sql = """
                SELECT * FROM find_user_credentials_by_email(?);
                """;

        String credentialJsonB = jdbcTemplate.query(
                sql, (rs) -> {
                    return rs.getString("user_credential");
                },
                email
        );

        return Optional.ofNullable(credentialJsonB);
    }

    
    Optional<String> findByUserId_ReturningCredentialsJsonB(String email) {
        String sql = """
                SELECT * FROM find_user_credentials_by_userid(?);
                """;

        String credentialJsonB = jdbcTemplate.query(
                sql, (rs) -> {
                    return rs.getString("user_credential");
                },
                email
        );

        return Optional.ofNullable(credentialJsonB);
    }

//    @Query(value = """
//                select
//                u.id,
//                u.email,
//                u.userId,
//                u.userCredential
//                from User u
//                where u.userId=?1
//            """)
//    Optional<UserSecurityProjectionDTO> findById_Security_Projection(String userId);
}
