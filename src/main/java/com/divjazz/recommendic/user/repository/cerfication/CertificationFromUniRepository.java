package com.divjazz.recommendic.user.repository.cerfication;

import com.divjazz.recommendic.user.model.certification.CertificationFromUni;
import com.divjazz.recommendic.user.model.certification.CertificationID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificationFromUniRepository extends JpaRepository<CertificationFromUni, CertificationID> {
}
