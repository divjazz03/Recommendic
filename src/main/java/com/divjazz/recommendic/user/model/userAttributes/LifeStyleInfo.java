package com.divjazz.recommendic.user.model.userAttributes;

import com.divjazz.recommendic.user.controller.patient.payload.LifeStyleInfoDTO;
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

    public LifeStyleInfoDTO toDTO() {
        return new LifeStyleInfoDTO(
                smokingStatus,
                alcoholConsumption,
                exerciseFrequency,
                dietaryRestrictions
        );
    }
}
