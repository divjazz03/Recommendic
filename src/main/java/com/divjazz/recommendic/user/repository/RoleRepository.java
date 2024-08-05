package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.model.userAttributes.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long>  {
    Optional<Role> getRoleByName(String name);
}
