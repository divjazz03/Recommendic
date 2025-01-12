package com.divjazz.recommendic.user.enums;

public enum MedicalCategory {
    PEDIATRICIAN("Dealing with care and basic treatment of children"),
    CARDIOLOGY("Dealing with treatment of the heart"),
    ONCOLOGY("Dealing with treatment of Cancer"),
    DERMATOLOGY("Dealing with treatment of the skin"),
    ORTHOPEDIC_SURGERY("Dealing with surgery relating to the bones"),
    NEUROSURGERY("Dealing with surgery relating to the brain"),
    CARDIOVASCULAR_SURGERY("Dealing with surgery relating to the heart"),
    GYNECOLOGY("Dealing with women's reproductive health"),
    PSYCHIATRY("Dealing with mental health disorders"),
    DENTISTRY("Dealing with oral health"),
    OPHTHALMOLOGY("Dealing with eye care"),
    PHYSICAL_THERAPY("Dealing with recovery of patients rom injuries or surgeries");

    private final String description;

    MedicalCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
