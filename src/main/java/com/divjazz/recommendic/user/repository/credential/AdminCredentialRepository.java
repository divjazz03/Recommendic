package com.divjazz.recommendic.user.repository.credential;

import com.divjazz.recommendic.user.model.userAttributes.credential.AdminCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminCredentialRepository extends JpaRepository<AdminCredential, Long> {
    public Optional<AdminCredential> getAdminCredentialByAdmin_Id(Long id);
}
