package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.model.userAttributes.ProfilePicture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProfilePictureRepository extends JpaRepository<ProfilePicture, UUID> {
    Optional<ProfilePicture> findByPictureUrl(String profilePictureUrl);
}
