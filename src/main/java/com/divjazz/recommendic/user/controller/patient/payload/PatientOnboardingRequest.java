package com.divjazz.recommendic.user.controller.patient.payload;

import com.divjazz.recommendic.user.enums.BloodType;

import java.util.Set;

public record PatientOnboardingRequest(
        String dateOfBirth,
        String phone,
        String emergencyContact,
        String emergencyPhone,
        BloodType bloodType,
        String allergies,
        String chronicConditions,
        String currentMedications,
        String pastSurgeries,
        String familyHistory,
        String smokingStatus,
        String alcoholConsumption,
        String exerciseFrequency,
        String dietaryRestrictions,
        Set<String> specializations
) {}
