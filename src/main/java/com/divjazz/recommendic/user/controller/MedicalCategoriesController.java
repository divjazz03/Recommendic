package com.divjazz.recommendic.user.controller;

import com.divjazz.recommendic.Response;
import com.divjazz.recommendic.user.enums.MedicalCategory;
import com.divjazz.recommendic.utils.RequestUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/medical_categories")
public class MedicalCategoriesController {

    @GetMapping("/")
    @Operation(summary = "Get all Medical categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Medical Categories found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Response.class))}),
    })
    public Response getAllMedicalCategories(HttpServletRequest request) {
        var medicalCategories = Arrays.stream(MedicalCategory.values())
                .map(category -> category.toString().toUpperCase())
                .collect(Collectors.toSet());
        return RequestUtils.getResponse(request,Map.of("categories", medicalCategories), "found",HttpStatus.OK);
    }
}
