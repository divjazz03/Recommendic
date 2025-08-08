package com.divjazz.recommendic.appointment.IT;

import com.divjazz.recommendic.BaseIntegration;
import com.divjazz.recommendic.appointment.domain.RecurrenceFrequency;
import com.divjazz.recommendic.appointment.domain.RecurrenceRule;
import com.divjazz.recommendic.appointment.enums.AppointmentStatus;
import com.divjazz.recommendic.appointment.model.Appointment;
import com.divjazz.recommendic.appointment.model.Schedule;
import com.divjazz.recommendic.appointment.repository.AppointmentRepository;
import com.divjazz.recommendic.appointment.repository.ScheduleRepository;
import com.divjazz.recommendic.appointment.service.ScheduleService;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.MedicalCategoryEnum;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.ConsultantProfile;
import com.divjazz.recommendic.user.model.userAttributes.PatientProfile;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.ConsultantRepository;
import com.divjazz.recommendic.user.repository.PatientRepository;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Slf4j
public class ScheduleIT extends BaseIntegration {

    public static final String BASE_URL = "/api/v1/schedules";
    private static final Faker faker = new Faker();
    @Autowired
    private ConsultantRepository consultantRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private MockMvc mockMvc;
    private Consultant consultant;

    private static Stream<Arguments> invalidCreateScheduleRequests() {
        return Stream.of(Arguments.of("""
                {
                    "name":"My schedule",
                    "startTime": "11:30",
                    "zoneOffset": "+01:00",
                    "channels": ["voice","in_person"],
                    "recurrenceRule": null,
                    "isRecurring": false,
                    "isActive": true
                }
                """), Arguments.of("""
                {
                    "name":"My schedule",
                    "startTime": "1130",
                    "endTime": "14:400",
                    "zoneOffset": "+01:00p",
                    "channels": ["voicee","in_pearson"],
                    "recurrenceRule": {},
                    "isRecurring": true,
                    "isActive": true
                }
                """), Arguments.of("""
                {
                    "name":"My schedule",
                    "startTime": "11:30",
                    "endTime": "14:00",
                    "zoneOffset": "+01:00",
                    "channels": ["voice","in_person"],
                    "recurrenceRule": {
                        "frequency": "one-of",
                        "weekDays": ["moday", "wednsday"],
                        "interval": 2,
                        "endDate": "2023-0s1-23"
                    },
                    "isRecurring": true,
                    "isActive": true
                }
                """), Arguments.of("""
                {
                    "name":"My schedule",
                    "startTime": "11:30",
                    "endTime": "14:00",
                    "zoneOffset": "+01:00",
                    "channels": ["voice","in_person"],
                    "recurrenceRule": {
                        "frequency": "weekly",
                        "weekDays": [],
                        "interval": 2,
                        "endDate": "2023-0s1-23"
                    },
                    "isRecurring": true,
                    "isActive": true
                }
                """));
    }

    private static Stream<Arguments> createScheduleRequests() {
        return Stream.of(
                Arguments.of("""
                        {
                            "name":"My schedule",
                            "startTime": "11:30",
                            "endTime": "14:00",
                            "zoneOffset": "+01:00",
                            "channels": ["voice","in_person"],
                            "recurrenceRule": null,
                            "isActive": true
                        }
                        """),
                Arguments.of("""
                        {
                            "name":"My schedule",
                            "startTime": "11:30",
                            "endTime": "14:00",
                            "zoneOffset": "+01:00",
                            "channels": ["voice","in_person"],
                            "recurrenceRule": {
                                "frequency": "one-off",
                                "weekDays": ["monday", "wednesday"],
                                "interval": 2,
                                "endDate": "2023-01-23"
                            },
                            "isRecurring": true,
                            "isActive": true
                        }
                        """),
                Arguments.of("""
                        {
                            "name":"My schedule",
                            "startTime": "11:30",
                            "endTime": "14:00",
                            "zoneOffset": "+01:00",
                            "channels": ["voice","in_person"],
                            "recurrenceRule": {
                                "frequency": "weekly",
                                "weekDays": ["monday", "wednesday", "friday"],
                                "interval": 1,
                                "endDate": "2023-01-23"
                            },
                            "isRecurring": true,
                            "isActive": true
                        }
                        """)
        );
    }

    @BeforeEach
    void setup() {
        var unSavedconsultant = new Consultant(
                faker.internet().emailAddress(),
                Gender.MALE,
                new UserCredential("sjfskjvnksjfns"));
        unSavedconsultant.setCertified(true);
        unSavedconsultant.setUserStage(UserStage.ACTIVE_USER);
        unSavedconsultant.setMedicalCategory(MedicalCategoryEnum.CARDIOLOGY);

        var consultantProfile = ConsultantProfile.builder()
                .address(new Address(faker.address().city(), faker.address().state(), faker.address().country()))
                .phoneNumber(faker.phoneNumber().phoneNumber())
                .userName(new UserName(faker.name().firstName(), faker.name().lastName()))
                .locationOfInstitution(faker.location().work())
                .title(faker.job().title())
                .consultant(unSavedconsultant)
                .build();
        unSavedconsultant.setProfile(consultantProfile);
        this.consultant = consultantRepository.save(unSavedconsultant);
    }

    @ParameterizedTest
    @MethodSource("createScheduleRequests")
    void shouldCreateScheduleForCurrentUser(String createScheduleRequest) throws Exception {
        var result = mockMvc.perform(
                post(BASE_URL)
                        .with(user(consultant.getUserPrincipal()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createScheduleRequest)
        ).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        log.info("Response {}", result);
    }

    @ParameterizedTest
    @MethodSource("invalidCreateScheduleRequests")
    void shouldNotCreateScheduleButReturn400(String invalidCreateScheduleRequest) throws Exception {
        var result = mockMvc.perform(
                post(BASE_URL)
                        .with(user(consultant.getUserPrincipal()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidCreateScheduleRequest)
        ).andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
        log.info("Response {}", result);
    }

    @Test
    void shouldReturnMySchedules() throws Exception {
        populateAppointmentForThisUser();
        var result = mockMvc.perform(
                get(BASE_URL + "/me")
                        .with(user(consultant.getUserPrincipal()))
        ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        log.info("result {}", result);
    }
    @Test
    void shouldNotAccessMySchedules() throws Exception {
        Patient patient = new Patient(
                faker.internet().emailAddress(),
                Gender.MALE,
                new UserCredential(faker.text().text(20))
        );
        patient.getUserPrincipal().setEnabled(true);
        patient.setMedicalCategories(new String[]{});
        patient.setUserStage(UserStage.ACTIVE_USER);

        PatientProfile patientProfile = PatientProfile.builder()
                .address(new Address(faker.address().city(), faker.address().state(), faker.address().country()))
                .phoneNumber(faker.phoneNumber().phoneNumber())
                .userName(new UserName(faker.name().firstName(), faker.name().lastName()))
                .patient(patient)
                .build();
        patient.setPatientProfile(patientProfile);
        patient = patientRepository.save(patient);

        mockMvc.perform(
                get(BASE_URL + "/me")
                        .with(user(patient.getUserPrincipal()))
        ).andExpect(status().isForbidden()).andReturn().getResponse().getContentAsString();
    }

    @Test
    void shouldModifyTheScheduleAndReturn200() throws Exception {
        populateAppointmentForThisUser();
        var schedule = scheduleService.getSchedulesByConsultantId(consultant.getUserId()).get(0);
        var modificationRequest = """
                        {
                            "name":"My schedule Modified",
                            "startTime": "11:30",
                            "endTime": "14:00",
                            "zoneOffset": "+01:00",
                            "channels": ["voice","in_person"],
                            "recurrenceRule": {
                                "frequency": "one-off",
                                "interval": 2,
                                "endDate": "2023-01-23"
                            },
                            "isRecurring": true,
                            "isActive": true
                        }
                        """;
        var result = mockMvc.perform(
                patch(BASE_URL+"/%s".formatted(schedule.getId()))
                        .with(user(consultant.getUserPrincipal()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(modificationRequest)
        ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        log.info("Response {}", result);
    }

    @Test
    void shouldNotModifyScheduleTheScheduleAndReturn403() throws Exception {
        populateAppointmentForThisUser();
        var unSavedconsultant = new Consultant(
                faker.internet().emailAddress(),
                Gender.MALE,
                new UserCredential("sjfskjvnksjfns"));
        unSavedconsultant.setCertified(true);
        unSavedconsultant.setUserStage(UserStage.ACTIVE_USER);
        unSavedconsultant.setMedicalCategory(MedicalCategoryEnum.CARDIOLOGY);

        var consultantProfile = ConsultantProfile.builder()
                .address(new Address(faker.address().city(), faker.address().state(), faker.address().country()))
                .phoneNumber(faker.phoneNumber().phoneNumber())
                .userName(new UserName(faker.name().firstName(), faker.name().lastName()))
                .locationOfInstitution(faker.location().work())
                .title(faker.job().title())
                .consultant(unSavedconsultant)
                .build();
        unSavedconsultant.setProfile(consultantProfile);
        var consultant = consultantRepository.save(unSavedconsultant);
        var schedule = scheduleService.getSchedulesByConsultantId(this.consultant.getUserId()).get(0);
        var modificationRequest = """
                        {
                            "name":"My schedule Modified",
                            "startTime": "11:30",
                            "endTime": "14:00",
                            "zoneOffset": "+01:00",
                            "channels": ["voice","in_person"],
                            "recurrenceRule": {
                                "frequency": "one-off",
                                "interval": 2,
                                "endDate": "2023-01-23"
                            },
                            "isRecurring": true,
                            "isActive": true
                        }
                        """;
        var result = mockMvc.perform(
                patch(BASE_URL+"/%s".formatted(schedule.getId()))
                        .with(user(consultant.getUserPrincipal()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(modificationRequest)
        ).andExpect(status().isForbidden()).andReturn().getResponse().getContentAsString();

        log.info("Response {}", result);
    }


    @Test
    void shouldDeleteScheduleAndReturn200() throws Exception {
        populateAppointmentForThisUser();

        var schedule = scheduleService.getSchedulesByConsultantId(consultant.getUserId()).get(0);
        mockMvc.perform(
                delete(BASE_URL+"/%s".formatted(schedule.getId()))
                        .with(user(consultant.getUserPrincipal()))
        ).andExpect(status().isNoContent());
    }
    @Test
    void shouldNotDeleteScheduleAndReturn403() throws Exception {
        populateAppointmentForThisUser();
        var unSavedconsultant = new Consultant(
                faker.internet().emailAddress(),
                Gender.MALE,
                new UserCredential("sjfskjvnksjfns"));
        unSavedconsultant.setCertified(true);
        unSavedconsultant.setUserStage(UserStage.ACTIVE_USER);
        unSavedconsultant.setMedicalCategory(MedicalCategoryEnum.CARDIOLOGY);

        var consultantProfile = ConsultantProfile.builder()
                .address(new Address(faker.address().city(), faker.address().state(), faker.address().country()))
                .phoneNumber(faker.phoneNumber().phoneNumber())
                .userName(new UserName(faker.name().firstName(), faker.name().lastName()))
                .locationOfInstitution(faker.location().work())
                .title(faker.job().title())
                .consultant(unSavedconsultant)
                .build();
        unSavedconsultant.setProfile(consultantProfile);
        var consultant = consultantRepository.save(unSavedconsultant);
        var schedule = scheduleService.getSchedulesByConsultantId(this.consultant.getUserId()).get(0);
        mockMvc.perform(
                delete(BASE_URL+"/%s".formatted(schedule.getId()))
                        .with(user(this.consultant.getUserPrincipal()))
        ).andExpect(status().isNoContent());
    }


















    private void populateAppointmentForThisUser() {

        Patient patient = new Patient(
                faker.internet().emailAddress(),
                Gender.MALE,
                new UserCredential(faker.text().text(20))
        );
        patient.getUserPrincipal().setEnabled(true);
        patient.setMedicalCategories(new String[]{});
        patient.setUserStage(UserStage.ACTIVE_USER);

        PatientProfile patientProfile = PatientProfile.builder()
                .address(new Address(faker.address().city(), faker.address().state(), faker.address().country()))
                .phoneNumber(faker.phoneNumber().phoneNumber())
                .userName(new UserName(faker.name().firstName(), faker.name().lastName()))
                .patient(patient)
                .build();
        patient.setPatientProfile(patientProfile);
        patient = patientRepository.save(patient);

        var schedule = Schedule.builder()
                .name("Some schedule")
                .recurrenceRule(new RecurrenceRule(RecurrenceFrequency.MONTHLY, Set.of("monday", "wednesday","friday"), 2, "2023-04-23"))
                .isRecurring(true)
                .consultationChannels(new ConsultationChannel[]{ConsultationChannel.VIDEO})
                .zoneOffset(ZoneOffset.of("+01:00"))
                .isActive(true)
                .startTime(LocalTime.of(12,30))
                .endTime(LocalTime.of(15,30))
                .consultant(consultant)
                .build();
        schedule = scheduleRepository.save(schedule);
        var appointment1 = Appointment.builder()
                .consultant(consultant)
                .patient(patient)
                .schedule(schedule)
                .status(AppointmentStatus.CONFIRMED)
                .appointmentDate(LocalDate.of(2025, 4, 21))
                .consultationChannel(ConsultationChannel.VOICE)
                .build();
        var appointment2 = Appointment.builder()
                .consultant(consultant)
                .patient(patient)
                .schedule(schedule)
                .status(AppointmentStatus.REQUESTED)
                .appointmentDate(LocalDate.of(2025, 4, 21))
                .consultationChannel(ConsultationChannel.VOICE)
                .build();
        List<Appointment> appointments = List.of(appointment1,appointment2);
        appointmentRepository.saveAll(appointments);

    }


}
