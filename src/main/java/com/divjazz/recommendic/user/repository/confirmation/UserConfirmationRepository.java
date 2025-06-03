package com.divjazz.recommendic.user.repository.confirmation;

import com.divjazz.recommendic.user.model.UserConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserConfirmationRepository extends JpaRepository<UserConfirmation, Long> {
    Optional<UserConfirmation> findByKey(String key);
}
