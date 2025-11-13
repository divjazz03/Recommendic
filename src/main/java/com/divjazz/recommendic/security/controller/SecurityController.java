package com.divjazz.recommendic.security.controller;

import com.divjazz.recommendic.global.Response;
import com.divjazz.recommendic.security.controller.payload.UserSecuritySettingUpdateRequest;
import com.divjazz.recommendic.security.dto.UserSecuritySettingDTO;
import com.divjazz.recommendic.security.service.SecurityService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.divjazz.recommendic.global.RequestUtils.getResponse;

@RestController
@RequestMapping("/api/v1/security")
@RequiredArgsConstructor
@Tag(name = "Security Settings API")
public class SecurityController {


    private final SecurityService securityService;
    @GetMapping
    public ResponseEntity<Response<UserSecuritySettingDTO>> getUserSecuritySetting() {
        var result = securityService.getUserSecuritySetting();
        return ResponseEntity.ok(getResponse(result, HttpStatus.OK));
    }
    @PatchMapping
    public ResponseEntity<Response<UserSecuritySettingDTO>> updateUserSecuritySetting(
            @RequestBody UserSecuritySettingUpdateRequest updateRequest,
            HttpServletRequest request) {
        var result = securityService.updateUserSecuritySetting(updateRequest, request);

        return ResponseEntity.ok(getResponse(result,HttpStatus.OK));
    }
}
