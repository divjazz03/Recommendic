package com.divjazz.recommendic.user.repository.confirmation;

import com.divjazz.recommendic.user.model.userAttributes.confirmation.UserConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserConfirmationRepository extends JpaRepository<UserConfirmation, Long> {

}
