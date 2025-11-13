package com.divjazz.recommendic.appointment.controller;

import com.divjazz.recommendic.appointment.controller.payload.*;
import com.divjazz.recommendic.appointment.domain.Slot;
import com.divjazz.recommendic.appointment.dto.AppointmentDTO;
import com.divjazz.recommendic.appointment.service.AppointmentService;
import com.divjazz.recommendic.appointment.service.AvailabilityService;
import com.divjazz.recommendic.global.Response;
import com.divjazz.recommendic.global.general.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Set;

import static com.divjazz.recommendic.global.RequestUtils.getResponse;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointment API")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AvailabilityService availabilityService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_PATIENT', 'ROLE_CONSULTANT')")
    public ResponseEntity<Response<PageResponse<? extends AppointmentResponse>>> getAppointments(@PageableDefault(size = 20) Pageable pageable) {
        var results = appointmentService.getAppointmentsForThisUser(pageable);
        var pageResponse = PageResponse.fromSet(pageable, results.elements(), results.total());

        return ResponseEntity.ok(getResponse(pageResponse, HttpStatus.OK));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public ResponseEntity<Response<AppointmentCreationResponse>> scheduleAppointment(
            @RequestBody @Valid AppointmentCreationRequest request, HttpServletRequest servletRequest) {
        AppointmentCreationResponse response = appointmentService.createAppointment(request);
        return ResponseEntity.created(URI.create(servletRequest.getRequestURI()))
                .body(getResponse(response, HttpStatus.CREATED));
    }
    @PatchMapping("{id}/confirm")
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    public ResponseEntity<Void> confirmAppointment(@PathVariable String id) {
        appointmentService.confirmAppointment(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/reschedule")
    @PreAuthorize("hasAnyAuthority('ROLE_PATIENT', 'ROLE_CONSULTANT')")
    public ResponseEntity<Response<String>> rescheduleAppointment(@RequestBody @Valid AppointmentRescheduleRequest rescheduleRequest) {
        appointmentService.rescheduleRequest(rescheduleRequest);
        return ResponseEntity.ok(getResponse("Appointment Rescheduled to %s".formatted(rescheduleRequest.newDate()), HttpStatus.OK));
    }

    @GetMapping("/timeslots/{consultant_id}")
    public ResponseEntity<Response<Set<Slot>>> getConsultantsTimeSlots(@PathVariable("consultant_id") String consultantId,
                                                                       @RequestParam(value = "date") String date) {
        var timeSlots = availabilityService.getAvailableSlotsByDateAndConsultantId(date, consultantId);
        return ResponseEntity.ok(getResponse(timeSlots, HttpStatus.OK));
    }


    @PatchMapping("/cancel")
    public ResponseEntity<Response<String>> cancelAppointment(
                                                              @Valid @RequestBody AppointmentCancellationRequest appointmentCancellationRequest) {
        appointmentService.cancelAppointment(appointmentCancellationRequest);
        return ResponseEntity.ok(getResponse("Appointment Cancelled", HttpStatus.OK));
    }


}
