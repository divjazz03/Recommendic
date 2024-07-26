package com.divjazz.recommendic.user.repository.certificationRepo;

import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.certification.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.security.cert.Certificate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResumeRepository extends JpaRepository<Certification, UUID> {

    Optional<Certification> findByOwnerOfCertification(Consultant consultant);
}
