package com.divjazz.recommendic.user.dto;

import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.ProfilePicture;
import com.divjazz.recommendic.user.model.userAttributes.UserName;

public record PatientProfileResponse(
         UserName userName,
         String age,
         Address address,
         ProfilePicture profilePicture

) {
}
