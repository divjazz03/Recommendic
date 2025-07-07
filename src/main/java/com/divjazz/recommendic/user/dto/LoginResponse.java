package com.divjazz.recommendic.user.dto;

import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record LoginResponse(
        @JsonProperty("user_id") String userId,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName,
        String role,
        Address address,
        String userStage

) {
}
