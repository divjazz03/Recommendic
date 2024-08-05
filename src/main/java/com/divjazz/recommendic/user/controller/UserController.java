package com.divjazz.recommendic.user.controller;


import com.divjazz.recommendic.user.service.GeneralUserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/user")
public class UserController {

    private final GeneralUserService userService;

    public UserController(GeneralUserService userService) {
        this.userService = userService;
    }
}
