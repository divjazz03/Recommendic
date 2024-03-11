package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.model.userAttributes.AdminPassword;
import com.divjazz.recommendic.user.model.userAttributes.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminPasswordRepository extends JpaRepository<AdminPassword, UserId> {
}
