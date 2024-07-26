package com.divjazz.recommendic.user.repository.confirmation;

import com.divjazz.recommendic.user.model.userAttributes.confirmation.ConsultantConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConsultationConfirmationRepository extends JpaRepository<ConsultantConfirmation, UUID> {
    public Optional<ConsultantConfirmation> getConsultantConfirmationByConsultant_Id(UUID id);
}
