package com.divjazz.recommendic.appointment.controller;

import com.divjazz.recommendic.appointment.controller.payload.ConsultantSchedulesResponse;
import com.divjazz.recommendic.appointment.controller.payload.ScheduleCreationRequest;
import com.divjazz.recommendic.appointment.controller.payload.ScheduleDisplay;
import com.divjazz.recommendic.appointment.controller.payload.ScheduleModificationRequest;
import com.divjazz.recommendic.appointment.dto.ScheduleResponseDTO;
import com.divjazz.recommendic.appointment.service.ScheduleService;
import com.divjazz.recommendic.global.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

import static com.divjazz.recommendic.global.RequestUtils.getResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schedules")
public class ScheduleController {
    private final ScheduleService scheduleService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    public ResponseEntity<Response<ScheduleResponseDTO>> createSchedule(
            @RequestBody @Valid ScheduleCreationRequest scheduleCreationRequest) {
        var schedule = scheduleService.createSchedule(scheduleCreationRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(getResponse(schedule, HttpStatus.CREATED));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    public ResponseEntity<Response<Set<ScheduleDisplay>>> getMySchedules() {
        var schedules = scheduleService.getMySchedules();
        return ResponseEntity.ok(getResponse(schedules, HttpStatus.OK));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<ScheduleResponseDTO>> getScheduleById(@PathVariable long id) {
        var schedule = scheduleService.getScheduleById(id);
        return ResponseEntity.ok(getResponse(schedule, HttpStatus.OK));
    }
    @GetMapping("/consultant/{id}")
    public ResponseEntity<Response<ConsultantSchedulesResponse>> getScheduleByConsultantId (
            @PathVariable String id
    ) {
        var schedules = scheduleService.getSchedulesByConsultantIdHandler(id);
        return ResponseEntity.ok(getResponse(schedules, HttpStatus.OK));
    }
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    public ResponseEntity<Response<ScheduleResponseDTO>> modifySchedule(@PathVariable long id,
                                                                        @RequestBody @Valid ScheduleModificationRequest modificationRequest) {
        var schedule = scheduleService.modifySchedule(id, modificationRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(getResponse(schedule, HttpStatus.CREATED));
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_CONSULTANT','ROLE_ADMIN')")
    public ResponseEntity<Response<Void>> deleteSchedule(@PathVariable long id) {
        scheduleService.deleteScheduleById(id);
        return ResponseEntity.noContent().build();
    }
}
