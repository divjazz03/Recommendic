package com.divjazz.recommendic.consultation.model;

import java.util.List;

public record Subjective(List<String> symptoms, List<String> histories, List<String> allergies) {
}
