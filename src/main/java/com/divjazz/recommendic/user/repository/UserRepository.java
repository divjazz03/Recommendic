package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.repository.projection.UserSecurityProjectionDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends UserBaseRepository<User> {

    @Query(value = """
                select
                u.id,
                u.email,
                u.userId,
                u.userCredential
                from User u
                where u.email=?1
            """)
    Optional<UserSecurityProjectionDTO> findByEmail_Security_Projection(String email);

    @Query(value = """
            select
            u.user_credential
            from users u
            where u.email=?1
        """, nativeQuery = true)
    Optional<String> findByEmail_ReturningCredentialsJsonB(String email);

    @Query(value = """
                select
                u.id,
                u.email,
                u.userId,
                u.userCredential
                from User u
                where u.userId=?1
            """)
    Optional<UserSecurityProjectionDTO> findById_Security_Projection(String userId);
}
