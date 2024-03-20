package com.divjazz.recommendic.user.repository.certificationRepo;

import com.divjazz.recommendic.user.model.certification.CertificationFromUni;
import com.divjazz.recommendic.user.model.certification.CertificationID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UniCertRepository extends JpaRepository<CertificationFromUni, CertificationID> {
}
