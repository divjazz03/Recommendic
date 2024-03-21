package com.divjazz.recommendic.user.repository.certificationRepo;

import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.certification.Certification;
import com.divjazz.recommendic.user.model.certification.CertificationID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.security.cert.Certificate;
import java.util.Optional;
@Repository
public interface ResumeRepository extends JpaRepository<Certification, CertificationID> {

    Optional<Certification> findByOwnerOfCertification(Consultant consultant);
}
