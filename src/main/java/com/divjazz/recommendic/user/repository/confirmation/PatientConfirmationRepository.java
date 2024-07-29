package com.divjazz.recommendic.user.repository.confirmation;

import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.model.userAttributes.confirmation.PatientConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientConfirmationRepository extends JpaRepository<PatientConfirmation, Long> {
    public Optional<PatientConfirmation> getPatientConfirmationByPatient_Id(Long user);
    public Optional<PatientConfirmation> findByKey(String key);
}
