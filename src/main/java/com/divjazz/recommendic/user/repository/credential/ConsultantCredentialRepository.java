package com.divjazz.recommendic.user.repository.credential;

import com.divjazz.recommendic.user.model.userAttributes.credential.ConsultantCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConsultantCredentialRepository extends JpaRepository<ConsultantCredential, Long> {
    public Optional<ConsultantCredential> getConsultantCredentialByConsultant_Id(Long id);
}
