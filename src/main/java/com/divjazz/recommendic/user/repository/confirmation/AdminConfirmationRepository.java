package com.divjazz.recommendic.user.repository.confirmation;

import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.model.userAttributes.confirmation.AdminConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminConfirmationRepository extends JpaRepository<AdminConfirmation, UUID> {
    public Optional<AdminConfirmation> findByKey(String key);
    public Optional<AdminConfirmation> getAdminConfirmationByAdmin_Id(UUID admin);

}
