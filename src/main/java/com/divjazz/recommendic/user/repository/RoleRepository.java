package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.model.userAttributes.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long>  {
}
