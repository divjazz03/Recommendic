package com.divjazz.recommendic.user.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum MedicalCategoryEnum {
    PEDIATRICIAN("pediatrician","Dealing with care and basic treatment of children"),
    CARDIOLOGY("cardiology","Dealing with treatment of the heart"),
    ONCOLOGY("oncology","Dealing with treatment of Cancer"),
    DERMATOLOGY("dermatology","Dealing with treatment of the skin"),
    ORTHOPEDIC_SURGERY("orthopedic surgery","Dealing with surgery relating to the bones"),
    NEUROSURGERY("neurosurgery","Dealing with surgery relating to the brain"),
    CARDIOVASCULAR_SURGERY("cardiovascular surgery","Dealing with surgery relating to the heart"),
    GYNECOLOGY("gynecology","Dealing with women's reproductive health"),
    PSYCHIATRY("psychiatry","Dealing with mental health disorders"),
    DENTISTRY("dentistry","Dealing with oral health"),
    OPHTHALMOLOGY("ophthalmology","Dealing with eye care"),
    PHYSICAL_THERAPY("physical therapy","Dealing with recovery of patients rom injuries or surgeries");

    private final String description;

    @JsonValue
    private final String value;

    MedicalCategoryEnum(String value, String description) {
        this.description = description;
        this.value = value;
    }

    @JsonCreator
    public static MedicalCategoryEnum fromValue(String value) throws IllegalArgumentException {
        if (value != null) {
            for (MedicalCategoryEnum medicalCategoryEnum : MedicalCategoryEnum.values()) {
                if (medicalCategoryEnum.value.equalsIgnoreCase(value)) {
                    return medicalCategoryEnum;
                }
            }
            throw new IllegalArgumentException("Invalid medical category: %s".formatted(value));
        }
        throw new IllegalArgumentException("value cannot be null");
    }
}
