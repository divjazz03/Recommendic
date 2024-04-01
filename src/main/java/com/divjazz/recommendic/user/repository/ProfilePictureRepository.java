package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.model.userAttributes.ProfilePicture;
import com.divjazz.recommendic.user.model.userAttributes.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ProfilePictureRepository extends JpaRepository<ProfilePicture, UserId> {
    Optional<ProfilePicture> findByUser(User user);
}
