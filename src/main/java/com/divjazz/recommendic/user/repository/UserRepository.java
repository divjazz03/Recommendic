package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.repository.projection.UserSecurityProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends UserBaseRepository<User> {

    @Query(value = """
                select u.id as id,
                u.email as email,
                u.userId as userId,
                r as role,
                c as userCredential
                from User u
                join Role r on u.role.id = r.id
                join UserCredential c on u.userCredential.id = c.id
                where u.email=?1
            """)
    Optional<UserSecurityProjection> findByEmail_Security_Projection(String email);

    @Query(value = """
                select u.email as email,
                u.userId as userId,
                r as role,
                c as userCredential
                from User u
                join Role r on u.role.id = r.id
                join UserCredential c on u.userCredential.id = c.id
                where u.userId=?1
            """)
    Optional<UserSecurityProjection> findById_Security_Projection(String userId);
}
