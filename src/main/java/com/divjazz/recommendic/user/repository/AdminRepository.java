package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.model.Admin;
import com.divjazz.recommendic.user.model.userAttributes.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, UserId> {

}
