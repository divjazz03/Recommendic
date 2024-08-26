package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Long>  {
    Optional<Role> getRoleByName(String name);

    @Query("SELECT u FROM User u WHERE u.role.id = :id")
    Optional<Set<User>> findUsersWithRoleById(@Param("id") Long id);

}
