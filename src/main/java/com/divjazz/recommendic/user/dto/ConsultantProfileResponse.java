package com.divjazz.recommendic.user.dto;

import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.ProfilePicture;
import com.divjazz.recommendic.user.model.userAttributes.UserName;

public record ConsultantProfileResponse(
        UserName userName,

        String phoneNumber,


        Address address,

        ProfilePicture profilePicture
) {
}
