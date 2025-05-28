package com.divjazz.recommendic.user.controller;


import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.service.GeneralUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "General User API")
public class UserController {

    private final GeneralUserService userService;

    public UserController(GeneralUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    @Operation(summary = "Get current user with assumption that user is authenticated")
    public ResponseEntity<CurrentUser> getCurrentUser(){
        User user = userService.retrieveCurrentUser();
        return ResponseEntity.ok(new CurrentUser(
                user.getUserId(),
                user.getUserNameObject().getFirstName(),
                user.getUserNameObject().getLastName(),
                user.getRole().getName(),
                user.getAddress(),
                user.getUserType(),
                user.getUserStage()
        ));
    }

    public record CurrentUser(String userId, String firstName, String lastName, String role, Address address, UserType userType, UserStage userStage){}

}
