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
import com.divjazz.recommendic.security.UserPrincipal;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.dto.UserDTO;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.MedicalCategoryEntity;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.ConsultantProfile;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.service.ConsultantService;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
    @Mock
    private ConsultantService consultantService;
    private Consultant consultant;

    private final UserDTO currentUser = new UserDTO(1,
            "",
            Gender.MALE,
            LocalDateTime.now(),
            UserType.CONSULTANT,
            UserStage.ONBOARDING,
            new UserPrincipal("",
                    new UserCredential("password"),
                    new Role("Admin","")));

    @BeforeEach
    void setup() {
        consultant = new Consultant(
                faker.internet().emailAddress(),
                Gender.MALE,
                new UserCredential(faker.text().text(20)),
                new Role(1L,"ROLE_TEST", "")
        );
        consultant.getUserPrincipal().setEnabled(true);
        consultant.setSpecialization(new MedicalCategoryEntity());
        consultant.setUserStage(UserStage.ACTIVE_USER);
        ConsultantProfile consultantProfile = ConsultantProfile.builder()
                .address(new Address(faker.address().city(), faker.address().state(), faker.address().country()))
                .phoneNumber(faker.phoneNumber().phoneNumber())
                .userName(new UserName(faker.name().firstName(), faker.name().lastName()))
                .locationOfInstitution(faker.location().work())
                .title(faker.job().title())
                .consultant(consultant)
                .build();
        consultant.setProfile(consultantProfile);
        consultant.setUserId(UUID.randomUUID().toString());
    }
    @Test
    void shouldCreateScheduleWithoutRecurrenceRule() {
        var creationRequest = new ScheduleCreationRequest(
                "My ScheduleRecurrence",
                "13:30:00",
                "15:30:00",
                "+01:00",
                Set.of("online"),
                null,
                true
        );

        var schedule = Schedule.builder()
                .name(creationRequest.name())
                .startTime(LocalTime.parse(creationRequest.startTime(), DateTimeFormatter.ISO_TIME))
                .endTime(LocalTime.parse(creationRequest.endTime(), DateTimeFormatter.ISO_TIME))
                .isActive(creationRequest.isActive())
                .zoneOffset(ZoneOffset.of(creationRequest.zoneOffset()))
                .consultationChannels(creationRequest.channels().stream().map(channel -> ConsultationChannel.valueOf(channel.toUpperCase()))
                        .toArray(ConsultationChannel[]::new))
                .build();

        given(authUtils.getCurrentUser()).willReturn(currentUser);
        given(scheduleRepository.saveAll(anyList())).willReturn(List.of(schedule));
        given(consultantService.getReference(anyLong())).willReturn(consultant);

        var response = scheduleService.createSchedule(Collections.singletonList(creationRequest));
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
                Set.of("online"),
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
                .zoneOffset(ZoneOffset.of(creationRequest.zoneOffset()))
                .recurrenceRule(recurrenceRule)
                .consultationChannels(creationRequest.channels().stream().map(channel -> ConsultationChannel.valueOf(channel.toUpperCase()))
                        .toArray(ConsultationChannel[]::new))
                .build();

        given(authUtils.getCurrentUser()).willReturn(currentUser);
        given(scheduleRepository.saveAll(anyList())).willReturn(List.of(schedule));
        given(consultantService.getReference(anyLong())).willReturn(consultant);

        var response = scheduleService.createSchedule(Collections.singletonList(creationRequest));
        assertThat(response.name()).isEqualTo(creationRequest.name());
        assertThat(response.startTime()).isEqualTo(creationRequest.startTime());

    }
}
