package com.divjazz.recommendic.user.repository.cerfication;

import com.divjazz.recommendic.user.model.certification.CertificationID;
import com.divjazz.recommendic.user.model.certification.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<Resume, CertificationID> {
}
