package com.divjazz.recommendic.user.repository.certificationRepo;

import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.certification.ConsultantEducation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;


public interface ConsultantEducationRepository extends JpaRepository<ConsultantEducation, Long> {
    Set<ConsultantEducation> findAllByConsultant(Consultant consultant);
    Set<ConsultantEducation> findAllByConsultant_UserId(String userId);
}
