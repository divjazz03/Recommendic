package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.model.userAttributes.ProfilePicture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfilePictureRepository extends JpaRepository<ProfilePicture, UserId> {
    Optional<ProfilePicture> findByPictureUrl(String profilePictureUrl);
}
