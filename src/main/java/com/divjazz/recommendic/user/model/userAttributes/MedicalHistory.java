package com.divjazz.recommendic.user.model.userAttributes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MedicalHistory {
    private String allergies;
    private String chronicConditions;
    private String pastSurgeries;
    private String familyHistory;
    private String currentMedications;
}
