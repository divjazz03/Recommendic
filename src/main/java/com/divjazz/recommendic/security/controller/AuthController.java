package com.divjazz.recommendic.security.controller;

import com.divjazz.recommendic.global.Response;
import com.divjazz.recommendic.security.SessionUser;
import com.divjazz.recommendic.security.controller.payload.RefreshRequest;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import static com.divjazz.recommendic.global.RequestUtils.getResponse;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "User Login and Logout Api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public AuthController.CurrentUser me(@AuthenticationPrincipal Jwt jwt) {
        var principal = authService.getUserFromJwt(jwt);
        return new AuthController.CurrentUser(principal.getUsername());
    }

    @PostMapping("/login")
    @Operation(summary = "Log User in")
    public Response<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        var result = authService.handleUserLogin(loginRequest);

        return getResponse(result, HttpStatus.OK);
    }

    @PostMapping("/logout")
    @Operation(summary = "Log user out")
    @ResponseStatus(HttpStatus.OK)
    public void logout(@RequestBody String userId) {
    }

    @PostMapping("/email-token")
    @Operation(summary = "Confirm token got from email send after account creation")
    public Response<String> verifyEmailConfirmationToken(@RequestParam("token") String token) {
        String response = authService.handleConfirmationTokenValidation(token);
        return getResponse(response, HttpStatus.OK);
    }
    @PostMapping("/refresh-token")
    @Operation(summary = "Confirm token got from email send after account creation")
    public Response<String> verifyEmailConfirmationToken(@RequestBody RefreshRequest request) {
        String response = authService.handleTokenRefresh(request.refreshToken());
        return getResponse(response, HttpStatus.OK);
    }

    public record CurrentUser(String principal){}
}
