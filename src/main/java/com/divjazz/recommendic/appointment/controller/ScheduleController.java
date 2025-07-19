package com.divjazz.recommendic.appointment.controller;

import com.divjazz.recommendic.appointment.dto.ScheduleCreationRequest;
import com.divjazz.recommendic.appointment.dto.ScheduleResponseDTO;
import com.divjazz.recommendic.appointment.service.ScheduleService;
import com.divjazz.recommendic.global.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.divjazz.recommendic.global.RequestUtils.getResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedules")
public class ScheduleController {
    private final ScheduleService scheduleService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    public ResponseEntity<Response<ScheduleResponseDTO>> createSchedule(
            ScheduleCreationRequest scheduleCreationRequest) {
        var scheduleResponse = scheduleService.createSchedule(scheduleCreationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(getResponse(scheduleResponse, HttpStatus.CREATED));
    }
}
