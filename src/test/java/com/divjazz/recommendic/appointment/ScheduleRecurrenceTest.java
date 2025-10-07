package com.divjazz.recommendic.appointment;


import com.divjazz.recommendic.appointment.domain.DaysOfWeek;
import com.divjazz.recommendic.appointment.domain.RecurrenceFrequency;
import com.divjazz.recommendic.appointment.domain.RecurrenceRule;
import com.divjazz.recommendic.appointment.controller.payload.RecurrenceRuleRequest;
import com.divjazz.recommendic.appointment.controller.payload.ScheduleCreationRequest;
import com.divjazz.recommendic.appointment.model.Schedule;
import com.divjazz.recommendic.appointment.repository.ScheduleRepository;
import com.divjazz.recommendic.appointment.service.ScheduleService;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;



@ExtendWith(MockitoExtension.class)
public class ScheduleRecurrenceTest {

    public static final Faker faker = new Faker();
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private AuthUtils authUtils;
    @InjectMocks
    private ScheduleService scheduleService;

    private final Consultant currentUser = new Consultant(faker.internet().emailAddress(),
            Gender.MALE,
            new UserCredential("dsdjskjfskjfsk"));
    @Test
    void shouldCreateScheduleWithoutRecurrenceRule() {
        var creationRequest = new ScheduleCreationRequest(
                "My ScheduleRecurrence",
                "13:30:00",
                "15:30:00",
                "+01:00",
                Set.of("voice"),
                false,
                null,
                true
        );
        var schedule = Schedule.builder()
                .name(creationRequest.name())
                .startTime(LocalTime.parse(creationRequest.startTime(), DateTimeFormatter.ISO_TIME))
                .endTime(LocalTime.parse(creationRequest.endTime(), DateTimeFormatter.ISO_TIME))
                .isActive(creationRequest.isActive())
                .isRecurring(creationRequest.isRecurring())
                .zoneOffset(ZoneOffset.of(creationRequest.zoneOffset()))
                .consultationChannels(creationRequest.channels().stream().map(channel -> ConsultationChannel.valueOf(channel.toUpperCase()))
                        .toArray(ConsultationChannel[]::new))
                .build();

        given(authUtils.getCurrentUser()).willReturn(currentUser);
        given(scheduleRepository.save(any(Schedule.class))).willReturn(schedule);

        var response = scheduleService.createSchedule(creationRequest);
        assertThat(response.name()).isEqualTo(creationRequest.name());
        assertThat(response.startTime()).isEqualTo(creationRequest.startTime());

    }

    @Test
    void shouldCreateScheduleWithRecurrenceRule() {
        var creationRequest = new ScheduleCreationRequest(
                "My ScheduleRecurrence",
                "13:30:00",
                "15:30:00",
                "+01:00",
                Set.of("voice"),
                true,
                new RecurrenceRuleRequest(RecurrenceFrequency.DAILY, Set.of(DaysOfWeek.FRIDAY.toString()), 1, ""),
                true
        );
        var recurrenceRule = new RecurrenceRule(
                creationRequest.recurrenceRule().frequency(),
                creationRequest.recurrenceRule().weekDays(),
                creationRequest.recurrenceRule().interval(),
                creationRequest.recurrenceRule().endDate()
                );
        var schedule = Schedule.builder()
                .name(creationRequest.name())
                .startTime(LocalTime.parse(creationRequest.startTime(), DateTimeFormatter.ISO_TIME))
                .endTime(LocalTime.parse(creationRequest.endTime(), DateTimeFormatter.ISO_TIME))
                .isActive(creationRequest.isActive())
                .isRecurring(creationRequest.isRecurring())
                .zoneOffset(ZoneOffset.of(creationRequest.zoneOffset()))
                .recurrenceRule(recurrenceRule)
                .isRecurring(true)
                .consultationChannels(creationRequest.channels().stream().map(channel -> ConsultationChannel.valueOf(channel.toUpperCase()))
                        .toArray(ConsultationChannel[]::new))
                .build();

        given(authUtils.getCurrentUser()).willReturn(currentUser);
        given(scheduleRepository.save(any(Schedule.class))).willReturn(schedule);

        var response = scheduleService.createSchedule(creationRequest);
        assertThat(response.name()).isEqualTo(creationRequest.name());
        assertThat(response.startTime()).isEqualTo(creationRequest.startTime());

    }
}
