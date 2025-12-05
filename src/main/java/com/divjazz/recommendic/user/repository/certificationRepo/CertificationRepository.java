package com.divjazz.recommendic.user.repository.certificationRepo;

import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.certification.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {


    Optional<Certification> findByOwnerOfCertification_UserId(String userId);
}
