package com.divjazz.recommendic.user.repository.certificationRepo;

import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.certification.ConsultantEducation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsultantEducationRepository extends JpaRepository<ConsultantEducation, Long> {
    List<ConsultantEducation> findAllByConsultant(Consultant consultant);
}
