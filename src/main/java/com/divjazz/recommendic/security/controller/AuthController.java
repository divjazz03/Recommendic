package com.divjazz.recommendic.security.controller;

import com.divjazz.recommendic.global.Response;
import com.divjazz.recommendic.security.SessionUser;
import com.divjazz.recommendic.security.service.AuthService;
import com.divjazz.recommendic.user.dto.LoginRequest;
import com.divjazz.recommendic.user.dto.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.Session;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static com.divjazz.recommendic.global.RequestUtils.getResponse;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "User Login and Logout Api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/login")
    @Operation(summary = "Log User in")
    public ResponseEntity<Response<LoginResponse>> login(@RequestBody LoginRequest loginRequest,
                                                         HttpServletRequest httpServletRequest) {
        var result = authService.handleUserLogin(loginRequest,httpServletRequest);

        return ResponseEntity.ok(getResponse(result, HttpStatus.OK));
    }

    @PostMapping("/logout")
    @Operation(summary = "Log user out")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/email-token")
    @Operation(summary = "Confirm token got from email send after account creation")
    public ResponseEntity<Response<String>> verifyEmailConfirmationToken(@RequestParam("token") String token) {
        String response = authService.handleConfirmationTokenValidation(token);
        return ResponseEntity.ok(getResponse(response, HttpStatus.OK));
    }

    public record CurrentUser(String principal){}
}
