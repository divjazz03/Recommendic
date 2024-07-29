package com.divjazz.recommendic.user.repository.credential;

import com.divjazz.recommendic.user.model.userAttributes.credential.PatientCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientCredentialRepository extends JpaRepository<PatientCredential, Long> {
    public Optional<PatientCredential> getPatientCredentialByPatient_Id(Long id);
}
