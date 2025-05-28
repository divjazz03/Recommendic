package com.divjazz.recommendic.user.controller;

import com.divjazz.recommendic.Response;
import com.divjazz.recommendic.user.domain.MedicalCategory;
import com.divjazz.recommendic.user.enums.MedicalCategoryEnum;
import com.divjazz.recommendic.RequestUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/medical_categories/")
@Tag(name = "Medical Category API")
public class MedicalCategoriesController {

    @GetMapping
    @Operation(summary = "Get all Medical categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Medical Categories found",
                    content = @Content(mediaType = "application/json")),
    })
    public Response<Set<MedicalCategory>> getAllMedicalCategories() {
        var medicalCategories = Arrays.stream(MedicalCategoryEnum.values())
                .map(category -> new MedicalCategory(category.toString(), category.getDescription()))
                .collect(Collectors.toSet());
        return RequestUtils.getResponse( medicalCategories, "found", HttpStatus.OK);
    }
}

