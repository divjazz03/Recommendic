package com.divjazz.recommendic.user.controller.patient.payload;

import com.divjazz.recommendic.user.enums.BloodType;

import java.util.List;
import java.util.Set;

public record PatientOnboardingRequest(
        String dateOfBirth,
        String phone,
        String emergencyContact,
        String emergencyPhone,
        BloodType bloodType,
        List<String> allergies,
        List<String> chronicConditions,
        List<String> currentMedications,
        List<String> pastSurgeries,
        String familyHistory,
        String smokingStatus,
        String alcoholConsumption,
        String exerciseFrequency,
        String dietaryRestrictions,
        Set<String> specializations
) {}
