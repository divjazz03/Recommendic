package com.divjazz.recommendic.user.controller;

import com.divjazz.recommendic.Response;
import com.divjazz.recommendic.user.dto.MedicalCategoryResponse;
import com.divjazz.recommendic.user.enums.MedicalCategoryEnum;
import com.divjazz.recommendic.security.utils.RequestUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/medical_categories")
public class MedicalCategoriesController {

    @GetMapping("/")
    @Operation(summary = "Get all Medical categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Medical Categories found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Response.class))}),
    })
    public Response<Set<MedicalCategoryResponse>> getAllMedicalCategories() {
        var medicalCategories = Arrays.stream(MedicalCategoryEnum.values())
                .map(category -> new MedicalCategoryResponse(category.toString(), category.getDescription()))
                .collect(Collectors.toSet());
        return RequestUtils.getResponse( medicalCategories, "found", HttpStatus.OK);
    }
}

