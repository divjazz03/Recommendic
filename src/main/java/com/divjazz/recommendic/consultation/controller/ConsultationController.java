package com.divjazz.recommendic.consultation.controller;

import com.divjazz.recommendic.Response;
import com.divjazz.recommendic.consultation.service.ConsultationService;
import com.divjazz.recommendic.user.service.GeneralUserService;
import com.divjazz.recommendic.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController("api/v1/consultation")
public class ConsultationController {


    private final ConsultationService consultationService;
    private final GeneralUserService userService;

    public ConsultationController(ConsultationService consultationService, GeneralUserService userService) {
        this.consultationService = consultationService;
        this.userService = userService;
    }

    @GetMapping("request")
    public ResponseEntity<Response> requestConsultation (@RequestParam("consultant_id") String consultantId,
                                                         Authentication authentication,
                                                         HttpServletRequest httpServletRequest) {
        var currentUser = userService.retrieveUserByEmail(((UserDetails) authentication.getPrincipal()).getUsername());
        var result = consultationService.initializeConsultation(consultantId, currentUser);
        var response = RequestUtils.getResponse(httpServletRequest, Map.of("data", result), "consultation initialized", HttpStatus.OK);
        return new ResponseEntity<>(response, response.status());
    }


}
