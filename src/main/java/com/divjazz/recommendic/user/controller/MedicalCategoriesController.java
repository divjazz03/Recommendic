package com.divjazz.recommendic.user.controller;

import com.divjazz.recommendic.global.Response;
import com.divjazz.recommendic.user.domain.MedicalCategory;
import com.divjazz.recommendic.global.RequestUtils;
import com.divjazz.recommendic.user.service.MedicalCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/medical-categories")
@Tag(name = "Medical Category API")
@RequiredArgsConstructor
public class MedicalCategoriesController {

    private final MedicalCategoryService medicalCategoryService;

    @GetMapping
    @Operation(summary = "Get all Medical categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Medical Categories found",
                    content = @Content(mediaType = "application/json")),
    })
    public Response<Set<MedicalCategory>> getAllMedicalCategories() {
        return RequestUtils.getResponse(medicalCategoryService.getAllMedicalCategories() ,  HttpStatus.OK);
    }
}

