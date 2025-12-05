package com.divjazz.recommendic.user.model.userAttributes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LifeStyleInfo {
    private String smokingStatus;
    private String alcoholConsumption;
    private String exerciseFrequency;
    private String dietaryRestrictions;
}
