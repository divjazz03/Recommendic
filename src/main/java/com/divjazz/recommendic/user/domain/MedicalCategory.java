package com.divjazz.recommendic.user.domain;

import io.swagger.v3.oas.annotations.media.Schema;

public record MedicalCategory(
        @Schema(name = "Category Name", example = "Cardiology")
        String name,
        @Schema(name = "Unique identifier")
        String id,
        @Schema(name = "Category Description", example = "Dealing with treatment of the heart")
        String description,
        String icon) {

}
