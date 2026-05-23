package com.divjazz.recommendic.security.repository;

import com.divjazz.recommendic.security.model.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {
    Optional<AuthToken> findByTokenAndUserId(String token, String userId);
    Optional<AuthToken> findByToken(String token);
}
