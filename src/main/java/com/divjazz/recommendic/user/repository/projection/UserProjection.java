package com.divjazz.recommendic.user.repository.projection;

import com.divjazz.recommendic.user.dto.UserDTO;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.enums.UserType;

import java.time.LocalDateTime;

public interface UserProjection {
    Long getId();
    String getUserId();
    Gender getGender();
    LocalDateTime getLastLogin();
    UserType getUserType();
    UserStage getUserStage();
    UserPrincipalProjection getUserPrincipal();

    default UserDTO toUserDTO(){
        return new UserDTO(
                getId(),
                getUserId(),
                getGender(),
                getLastLogin(),
                getUserType(),
                getUserStage(),
                getUserPrincipal().toUserPrincipal()
        );
    }
}
