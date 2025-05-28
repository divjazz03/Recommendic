package com.divjazz.recommendic.user.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MedicalCategoryEnum {
    PEDIATRICIAN("pediatrician","Dealing with care and basic treatment of children"),
    CARDIOLOGY("cardiology","Dealing with treatment of the heart"),
    ONCOLOGY("oncology","Dealing with treatment of Cancer"),
    DERMATOLOGY("dermatology","Dealing with treatment of the skin"),
    ORTHOPEDIC_SURGERY("orthopedic_surgery","Dealing with surgery relating to the bones"),
    NEUROSURGERY("neurosurgery","Dealing with surgery relating to the brain"),
    CARDIOVASCULAR_SURGERY("cardiovascular_surgery","Dealing with surgery relating to the heart"),
    GYNECOLOGY("gynecology","Dealing with women's reproductive health"),
    PSYCHIATRY("psychiatry","Dealing with mental health disorders"),
    DENTISTRY("dentistry","Dealing with oral health"),
    OPHTHALMOLOGY("ophthalmology","Dealing with eye care"),
    PHYSICAL_THERAPY("physical_therapy","Dealing with recovery of patients rom injuries or surgeries");

    private final String description;

    @JsonValue
    private final String value;

    MedicalCategoryEnum(String value, String description) {
        this.description = description;
        this.value = value;
    }

    public String getDescription() {
        return description;
    }
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static MedicalCategoryEnum fromValue(String value) {
        if (value != null) {
            for (MedicalCategoryEnum medicalCategoryEnum : MedicalCategoryEnum.values()) {
                if (medicalCategoryEnum.value.equalsIgnoreCase(value)) {
                    return medicalCategoryEnum;
                }
            }
            throw new IllegalArgumentException("Invalid medical category: %s".formatted(value));
        }
        throw new IllegalArgumentException("name cannot be null");
    }
}
