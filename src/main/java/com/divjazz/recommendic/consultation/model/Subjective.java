package com.divjazz.recommendic.consultation.model;

import java.util.List;

public record Subjective(String majorComplaints, List<String> symptoms, List<String> histories, List<String> allergies) {
}
