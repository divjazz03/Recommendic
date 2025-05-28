package com.divjazz.recommendic.security.controller;

import com.divjazz.recommendic.Response;
import com.divjazz.recommendic.security.service.LoginService;
import com.divjazz.recommendic.user.dto.LoginRequest;
import com.divjazz.recommendic.user.dto.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.divjazz.recommendic.RequestUtils.getResponse;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "User Login and Logout Api")
public class LoginLogoutController {

    private final LoginService loginService;

    public LoginLogoutController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    @Operation(summary = "Log User in")
    public ResponseEntity<Response<LoginResponse>> login(@RequestBody LoginRequest loginRequest, HttpServletResponse httpServletResponse) {
        var result = loginService.handleUserLogin(loginRequest, httpServletResponse);
        return ResponseEntity.ok(getResponse(result,"success", HttpStatus.OK));
    }
    @PostMapping("/logout")
    @Operation(summary = "Log user out")
    public ResponseEntity<Void> login(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        loginService.handleLogout(request, httpServletResponse);
        return ResponseEntity.ok().build();
    }
}
