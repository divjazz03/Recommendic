package com.divjazz.recommendic.appointment.controller;

import com.divjazz.recommendic.appointment.dto.AppointmentCreationRequest;
import com.divjazz.recommendic.appointment.dto.AppointmentDTO;
import com.divjazz.recommendic.appointment.service.AppointmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/appointment")
@RequiredArgsConstructor
@Tag(name = "Appointment API")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/schedule")
    public ResponseEntity<AppointmentDTO> scheduleAppointment(
            @RequestBody AppointmentCreationRequest request, HttpServletRequest servletRequest) {
        AppointmentDTO appointmentDTO = appointmentService.createAppointment(request);

        return ResponseEntity.created(URI.create(servletRequest.getRequestURI())).body(appointmentDTO);
    }
}
