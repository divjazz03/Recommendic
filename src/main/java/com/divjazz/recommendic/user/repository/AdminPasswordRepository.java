package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.model.userAttributes.AdminPassword;
import com.divjazz.recommendic.user.model.userAttributes.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AdminPasswordRepository extends JpaRepository<AdminPassword, UUID> {
}
