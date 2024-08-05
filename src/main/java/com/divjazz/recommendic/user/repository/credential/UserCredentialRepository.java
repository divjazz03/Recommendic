package com.divjazz.recommendic.user.repository.credential;

import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {

}
