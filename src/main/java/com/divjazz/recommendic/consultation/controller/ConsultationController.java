package com.divjazz.recommendic.consultation.controller;

import com.divjazz.recommendic.Response;
import com.divjazz.recommendic.consultation.dto.ConsultationResponse;
import com.divjazz.recommendic.consultation.service.ConsultationService;
import com.divjazz.recommendic.user.domain.RequestContext;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.security.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController()
@RequestMapping("/api/v1/consultation")
public class ConsultationController {


    private final ConsultationService consultationService;
    private final AuthUtils authUtils;

    public ConsultationController(ConsultationService consultationService,AuthUtils authUtils) {
        this.consultationService = consultationService;
        this.authUtils = authUtils;
    }

    @GetMapping("/request")
    public ResponseEntity<Response<ConsultationResponse>> requestConsultation(@RequestParam("consultant_id") String consultantId) {
        var currentUser = authUtils.getCurrentUser();
        RequestContext.reset();
        RequestContext.setUserId(currentUser.getId());
        var result = consultationService.initializeConsultation(consultantId, currentUser);
        var response = RequestUtils.getResponse(result, "consultation initialized", HttpStatus.OK);
        return new ResponseEntity<>(response, response.status());
    }

    @GetMapping("/accept")
    public ResponseEntity<Response<ConsultationResponse>> acceptConsultation(@RequestParam("consultation_id") String consultationId) {
        var currentUser = authUtils.getCurrentUser();
        RequestContext.reset();
        RequestContext.setUserId(currentUser.getId());
        var result = consultationService.acknowledgeConsultation(consultationId);
        var response = RequestUtils.getResponse(result, "consultation acknowledged", HttpStatus.OK);
        return new ResponseEntity<>(response, response.status());
    }


}
