package com.divjazz.recommendic.appointment.service;

import com.divjazz.recommendic.appointment.controller.payload.*;
import com.divjazz.recommendic.appointment.domain.RecurrenceRule;
import com.divjazz.recommendic.appointment.dto.ScheduleResponseDTO;
import com.divjazz.recommendic.appointment.dto.ScheduleWithAppointmentDetail;
import com.divjazz.recommendic.appointment.model.Schedule;
import com.divjazz.recommendic.appointment.repository.ScheduleCustomRepository;
import com.divjazz.recommendic.appointment.repository.ScheduleRepository;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.dto.UserDTO;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.userAttributes.ConsultantProfile;
import com.divjazz.recommendic.user.model.userAttributes.ConsultantStat;
import com.divjazz.recommendic.user.repository.ConsultantStatRepository;
import com.divjazz.recommendic.user.repository.projection.UserProjection;
import com.divjazz.recommendic.user.service.ConsultantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {

    public final ScheduleCustomRepository scheduleCustomRepository;
    private final ScheduleRepository scheduleRepository;
    private final AuthUtils authUtils;
    private final ConsultantService consultantService;
    private final ConsultantStatRepository consultantStatRepository;
    private final AppointmentService appointmentService;

    @Transactional
    public ScheduleResponseDTO createSchedule(List<ScheduleCreationRequest> creationRequests) {
        UserDTO userProjection =  authUtils.getCurrentUser();
        Consultant consultant = consultantService.getReference(userProjection.id());
        List<Schedule> schedules = creationRequests.stream()
                .map(creationRequest -> {
                    var schedule = Schedule.builder()
                        .name(creationRequest.name())
                        .consultant(consultant)
                        .consultationChannels(toConsultationChannels(creationRequest.channels()))
                        .startTime(LocalTime.parse(creationRequest.startTime(), DateTimeFormatter.ISO_TIME))
                        .endTime(LocalTime.parse(creationRequest.endTime(), DateTimeFormatter.ISO_TIME))
                        .isActive(creationRequest.isActive())
                        .zoneOffset(ZoneOffset.of(creationRequest.zoneOffset()))
                        .build();

                    if (Objects.nonNull(creationRequest.recurrenceRule())) {
                        var recurrenceRule = new RecurrenceRule(
                                creationRequest.recurrenceRule().frequency(),
                                creationRequest.recurrenceRule().weekDays(),
                                creationRequest.recurrenceRule().interval(),
                                creationRequest.recurrenceRule().endDate()
                        );
                        schedule.setRecurrenceRule(recurrenceRule);
                    }
                    return schedule;

                })
                .toList();

        schedules = scheduleRepository.saveAll(schedules);

        return toScheduleResponseDTO(schedules.getFirst());
    }

    @Transactional(readOnly = true)
    public Set<ScheduleDisplay> getMySchedules() {
        return scheduleCustomRepository.findAllScheduleDisplaysByConsultantId(authUtils.getCurrentUser().id(), Pageable.ofSize(10));
    }

    public ScheduleResponseDTO getScheduleById(long id) {
        var schedule = scheduleRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Schedule with id %s not found".formatted(id)));
        return toScheduleResponseDTO(schedule);
    }

    public ConsultantSchedulesResponse getSchedulesByConsultantIdHandler(String consultantId) {
        ConsultantProfile consultantProfile = consultantService.getConsultantProfileByConsultantId(consultantId);
        ConsultantStat consultantStat = consultantStatRepository
                .findConsultantStatByConsultantId(consultantId).orElse(ConsultantStat.ofEmpty());
        Set<ScheduleWithAppointmentDetail> schedules = new HashSet<>(getSchedulesByConsultantId(consultantId));

        return new ConsultantSchedulesResponse(
                schedules,
                new ConsultantSchedulesResponse.ScheduleConsultantProfile(
                        consultantProfile.getUserName().getFullName(),
                        consultantProfile.getTitle(),
                        consultantStat.getRating(),
                        consultantProfile.getProfilePicture().getPictureUrl(),
                        new ConsultationFee(30,15),
                        consultantProfile.getLocationOfInstitution()
                )
        );

    }

    public List<ScheduleWithAppointmentDetail> getSchedulesByConsultantId(String consultantId) {
        return scheduleRepository.findAllByConsultant_UserId(consultantId)
                .stream()
                .map(schedule -> {
                    var appointmentDateAndTime = appointmentService.getAppointmentDatesAndTimeForSchedule(schedule);
                    return new ScheduleWithAppointmentDetail(
                            ScheduleService.toScheduleResponseDTO(schedule),
                            appointmentDateAndTime
                    );
                }).toList();
    }

    @Transactional
    public ScheduleResponseDTO modifySchedule(long id, ScheduleModificationRequest modificationRequest) {
        log.info("{}", modificationRequest.toString());
        var schedule = scheduleRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Schedule with id %s does not exist or has been deleted".formatted(id)));
        if (!schedule.getConsultant().getUserId().equals(authUtils.getCurrentUser().userId())) {
            throw new AuthorizationDeniedException("You are not the owner of this schedule, hence cannot modify this resource");
        }

        if (Objects.nonNull(modificationRequest.name())) {
            schedule.setName(modificationRequest.name());
        }
        if (Objects.nonNull(modificationRequest.channels()) && !modificationRequest.channels().isEmpty()) {
            schedule.setConsultationChannels(toConsultationChannels(modificationRequest.channels()));
        }
        if (Objects.nonNull(modificationRequest.endTime())) {
            schedule.setEndTime(LocalTime.parse(modificationRequest.endTime()));
        }
        if (Objects.nonNull(modificationRequest.startTime())) {
            schedule.setStartTime(LocalTime.parse(modificationRequest.startTime()));
        }
        if (Objects.nonNull(modificationRequest.zoneOffset())) {
            schedule.setZoneOffset(ZoneOffset.of(modificationRequest.zoneOffset()));
        }
        schedule.setActive(modificationRequest.isActive());
        if (Objects.nonNull(modificationRequest.recurrenceRule())) {
            schedule.setRecurrenceRule(new RecurrenceRule(modificationRequest.recurrenceRule().frequency(),
                    modificationRequest.recurrenceRule().weekDays(),
                    modificationRequest.recurrenceRule().interval(),
                    modificationRequest.recurrenceRule().endDate()));
        }

        return toScheduleResponseDTO(schedule);
    }


    private ConsultationChannel[] toConsultationChannels(Set<String> consultationChannelStrings) {
        return consultationChannelStrings.stream()
                .map(channel -> ConsultationChannel.valueOf(channel.toUpperCase()))
                .toArray(ConsultationChannel[]::new);
    }

    private static Set<String> fromConsultationChannels(ConsultationChannel[] consultationChannels) {
        return Arrays.stream(consultationChannels)
                .map(consultationChannel -> consultationChannel.toString().toLowerCase())
                .collect(Collectors.toSet());
    }

    private static ScheduleDisplay toScheduleDisplay(Schedule schedule) {
        return new ScheduleDisplay(
                schedule.getId(),
                schedule.getName(),
                schedule.getStartTime().format(DateTimeFormatter.ISO_TIME),
                schedule.getEndTime().format(DateTimeFormatter.ISO_TIME),
                schedule.getZoneOffset().toString(),
                fromConsultationChannels(schedule.getConsultationChannels()),
                schedule.getRecurrenceRule(),
                schedule.isActive(),
                schedule.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                0
        );
    }

    public static ScheduleResponseDTO toScheduleResponseDTO(Schedule schedule) {
        return new ScheduleResponseDTO(
                schedule.getScheduleId(),
                schedule.getName(),
                schedule.getStartTime().format(DateTimeFormatter.ISO_TIME),
                schedule.getEndTime().format(DateTimeFormatter.ISO_TIME),
                schedule.getZoneOffset().getId(),
                fromConsultationChannels(schedule.getConsultationChannels()),
                schedule.getRecurrenceRule() != null
                        ? schedule.getRecurrenceRule() : null,
                schedule.isActive()
        );
    }

    public void deleteScheduleById(long id) {
        var schedule = scheduleRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Schedule with id %s does not exist or has been deleted".formatted(id)));
        if (!schedule.getConsultant().getUserId().equals(authUtils.getCurrentUser().userId())) {
            throw new AuthorizationDeniedException("You are not authorized to delete this resource");
        }
        scheduleRepository.deleteById(id);
    }
}
