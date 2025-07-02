package com.divjazz.recommendic.user.domain;

import com.divjazz.recommendic.user.enums.MedicalCategoryEnum;
import io.swagger.v3.oas.annotations.media.Schema;

public record MedicalCategory(
        @Schema(name = "Category Name", example = "Cardiology")
        String name,
        @Schema(name = "Category Description", example = "Dealing with treatment of the heart")
        String description) {


        public static MedicalCategory fromMedicalCategoryEnum(MedicalCategoryEnum categoryEnum) {
                return new MedicalCategory(categoryEnum.getValue(), categoryEnum.getDescription());
        }
}
