package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.enums.MedicalCategory;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.model.userAttributes.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConsultantRepository extends JpaRepository<Consultant, UserId> {
    Optional<Consultant> findByUser(User user);
    Optional<List<Consultant>> findByMedicalCategory(MedicalCategory category);
}
