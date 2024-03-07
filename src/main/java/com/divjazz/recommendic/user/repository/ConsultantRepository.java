package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.userAttributes.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsultantRepository extends JpaRepository<Consultant, UserId> {
}
