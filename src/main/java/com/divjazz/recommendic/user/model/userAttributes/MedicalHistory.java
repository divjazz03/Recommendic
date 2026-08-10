package com.divjazz.recommendic.user.model.userAttributes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MedicalHistory {
    private List<String> allergies;
    private List<String> chronicConditions;
    private List<String> pastSurgeries;
    private String familyHistory;
    private List<String> currentMedications;
}
