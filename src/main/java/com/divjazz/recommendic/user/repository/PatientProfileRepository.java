package com.divjazz.recommendic.user.repository;
import com.divjazz.recommendic.user.model.userAttributes.PatientProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientProfileRepository extends JpaRepository<PatientProfile, Long> {
}
