package com.divjazz.recommendic.user.controller;


import com.divjazz.recommendic.global.Response;
import com.divjazz.recommendic.security.SessionUser;
import com.divjazz.recommendic.security.controller.AuthController;
import com.divjazz.recommendic.user.dto.UserDTO;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.service.GeneralUserService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static com.divjazz.recommendic.global.RequestUtils.getResponse;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "General User API")
@RequiredArgsConstructor
public class UserController {

    private final GeneralUserService userService;


    @GetMapping
    @Operation(summary = "Get current user with assumption that user is authenticated")
    public Response<UserResponse> getCurrentUser(){
        var response = userService.retrieveCurrentUser();
        return getResponse(response, HttpStatus.OK);
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CurrentUser(String userId, String role, UserType userType, UserStage userStage){}

}
