package com.divjazz.recommendic.appointment.controller;

import com.divjazz.recommendic.appointment.dto.AppointmentCreationRequest;
import com.divjazz.recommendic.appointment.dto.AppointmentCreationResponse;
import com.divjazz.recommendic.appointment.dto.AppointmentDTO;
import com.divjazz.recommendic.appointment.service.AppointmentService;
import com.divjazz.recommendic.global.Response;
import com.divjazz.recommendic.security.utils.AuthUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static com.divjazz.recommendic.global.RequestUtils.getResponse;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointment API")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<Response<AppointmentCreationResponse>> scheduleAppointment(
            @RequestBody @Valid AppointmentCreationRequest request, HttpServletRequest servletRequest) {
        AppointmentCreationResponse response = appointmentService.createAppointment(request);
        return ResponseEntity.created(URI.create(servletRequest.getRequestURI()))
                .body(getResponse(response, HttpStatus.CREATED));
    }
    @PostMapping("{id}/confirm")
    @PreAuthorize("@authUtils.currentUser.userPrincipal.role.equals('ROLE_CONSULTANT')")
    public ResponseEntity<Void> confirmAppointment(@PathVariable Long id) {
        appointmentService.confirmAppointment(id);
        return ResponseEntity.ok().build();
    }
}
