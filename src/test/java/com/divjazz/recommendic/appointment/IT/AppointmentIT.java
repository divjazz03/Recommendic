package com.divjazz.recommendic.appointment.IT;

import com.divjazz.recommendic.BaseIntegrationTest;
import com.divjazz.recommendic.appointment.domain.RecurrenceFrequency;
import com.divjazz.recommendic.appointment.domain.RecurrenceRule;
import com.divjazz.recommendic.appointment.enums.AppointmentHistory;
import com.divjazz.recommendic.appointment.enums.AppointmentStatus;
import com.divjazz.recommendic.appointment.model.Appointment;
import com.divjazz.recommendic.appointment.model.Schedule;
import com.divjazz.recommendic.appointment.repository.AppointmentRepository;
import com.divjazz.recommendic.appointment.repository.ScheduleRepository;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.MedicalCategoryEntity;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.*;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.ConsultantRepository;
import com.divjazz.recommendic.user.repository.PatientRepository;
import com.divjazz.recommendic.user.service.ConsultantService;
import com.divjazz.recommendic.user.service.MedicalCategoryService;
import com.divjazz.recommendic.user.service.PatientService;
import com.divjazz.recommendic.user.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Slf4j
public class AppointmentIT extends BaseIntegrationTest {
    private static final String BASE_URL = "/api/v1/appointments";
    private static final Faker faker = new Faker();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private ConsultantRepository consultantRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;

    private Consultant consultant;
    private Schedule schedule;

    private Patient patient;

    @Autowired
    private RoleService roleService;
    @Autowired
    private MedicalCategoryService medicalCategoryService;

    @BeforeEach
    void setup() {
        Role consultantRole = roleService.getRoleByName(ConsultantService.CONSULTANT_ROLE_NAME);
        Role patientRole = roleService.getRoleByName(PatientService.PATIENT_ROLE_NAME);
        MedicalCategoryEntity medicalCategory = medicalCategoryService.getMedicalCategoryById("cardiology");

        var unsavedPatient = new Patient(faker.internet().emailAddress(),
                Gender.MALE,
                new UserCredential("jvsfvbjdhbvifhbedfkcihujb"), patientRole);
        unsavedPatient.setUserStage(UserStage.ACTIVE_USER);
        unsavedPatient.addMedicalCategory(medicalCategory);
        var patientProfile = PatientProfile.builder()
                .patient(unsavedPatient)
                .phoneNumber(faker.phoneNumber().phoneNumber())
                .address(new Address(faker.address().city(), faker.address().state(), faker.address().country()))
                .userName(new UserName(faker.name().firstName(), faker.name().lastName()))
                .profilePicture(new ProfilePicture("profile Picture", faker.avatar().image()))
                .build();
        unsavedPatient.setPatientProfile(patientProfile);
        patient = patientRepository.save(unsavedPatient);

        var unSavedconsultant = new Consultant(
                faker.internet().emailAddress(),
                Gender.MALE,
                new UserCredential("sjfskjvnksjfns"), consultantRole);
        unSavedconsultant.setCertified(true);
        unSavedconsultant.setUserStage(UserStage.ACTIVE_USER);
        unSavedconsultant.setSpecialization(medicalCategory);

        var consultantProfile = ConsultantProfile.builder()
                .address(new Address(faker.address().city(), faker.address().state(), faker.address().country()))
                .phoneNumber(faker.phoneNumber().phoneNumber())
                .userName(new UserName(faker.name().firstName(), faker.name().lastName()))
                .locationOfInstitution(faker.location().work())
                .title(faker.job().title())
                .consultant(unSavedconsultant)
                .build();
        unSavedconsultant.setProfile(consultantProfile);
        consultant = consultantRepository.save(unSavedconsultant);

        var unsavedSchedule = Schedule.builder()
                .name("Some schedule")
                .recurrenceRule(new RecurrenceRule(RecurrenceFrequency.MONTHLY, Set.of("monday", "wednesday","friday"), 2, "2023-04-23"))
                .consultationChannels(new ConsultationChannel[]{ConsultationChannel.ONLINE})
                .zoneOffset(ZoneOffset.of("+01:00"))
                .isActive(true)
                .startTime(LocalTime.of(12,30))
                .endTime(LocalTime.of(15,30))
                .consultant(consultant)
                .build();
        schedule = scheduleRepository.save(unsavedSchedule);
    }
    @Test
    void shouldCreateANewAppointmentAndReturn201Created() throws Exception{
        var request = """
                        {
                            "consultantId": "%s",
                            "scheduleId": "%s",
                            "channel": "online",
                            "date": "2025-12-10",
                            "reason": "Test reason"
                        }
                        """.formatted(consultant.getUserId(), schedule.getScheduleId());
        var result = mockMvc.perform(
                post(BASE_URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(patient.getUserPrincipal()))
        ).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        

    }


    @Test
    void shouldNotCreateAppointmentButReturn404BecauseScheduleDoesNotExist() throws Exception {
        var request = """
                        {
                            "consultantId": "%s",
                            "scheduleId": "%s",
                            "channel": "online",
                            "date" : "2001-03-09",
                            "reason" : "Test reason"
                        }
                        """.formatted(consultant.getUserId(), 2794879L);
        mockMvc.perform(
                post(BASE_URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(patient.getUserPrincipal()))
        ).andExpect(status().isNotFound());

        
    }

    @Test
    void shouldCancelAppointmentIfAppointmentExistsAndIsNotAlreadyCancelled() throws Exception {

        Appointment appointment = Appointment.builder()
                .consultant(consultant)
                .patient(patient)
                .schedule(schedule)
                .status(AppointmentStatus.PENDING)
                .appointmentDate(LocalDate.of(2025, 4, 21))
                .consultationChannel(ConsultationChannel.ONLINE)
                .reason("Test reason")
                .history(AppointmentHistory.NEW)
                .build();

        appointment = appointmentRepository.save(appointment);
        var requestPayLoad = "{\"reason\": \"Just because\", \"appointmentId\": \"%s\"}".formatted(appointment.getAppointmentId());




        mockMvc.perform(
                post(BASE_URL + "/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestPayLoad)
                        .with(user(patient.getUserPrincipal()))

        ).andExpect(status().isOk());
    }

    @Test
    void shouldNotCancelAppointmentIfAppointmentExistsButNotAuthorizedTo() throws Exception {
        Appointment appointment = Appointment.builder()
                .consultant(consultant)
                .patient(patient)
                .schedule(schedule)
                .status(AppointmentStatus.PENDING)
                .appointmentDate(LocalDate.of(2025, 4, 21))
                .consultationChannel(ConsultationChannel.ONLINE)
                .reason("Test reason")
                .history(AppointmentHistory.NEW)
                .build();

        appointment = appointmentRepository.save(appointment);
        var requestPayLoad = "{\"reason\": \"Just because\", \"appointmentId\": \"%s\"}".formatted(appointment.getAppointmentId());
        mockMvc.perform(
                post(BASE_URL + "/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestPayLoad)
                        .with(user(consultant.getUserPrincipal()))

        ).andExpect(status().isForbidden());
    }
    @Test
    void shouldConfirmAppointmentIfAuthorizedConsultant() throws Exception {
        Appointment appointment = Appointment.builder()
                .consultant(consultant)
                .patient(patient)
                .schedule(schedule)
                .status(AppointmentStatus.PENDING)
                .appointmentDate(LocalDate.of(2025, 12, 21))
                .consultationChannel(ConsultationChannel.ONLINE)
                .reason("Test reason")
                .history(AppointmentHistory.NEW)
                .build();

        appointment = appointmentRepository.save(appointment);

        var requestPayLoad = "{\"note\": \"Just because\", \"appointmentId\": \"%s\"}".formatted(appointment.getAppointmentId());

        mockMvc.perform(
                post(BASE_URL + "/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestPayLoad)
                        .with(user(consultant.getUserPrincipal()))
        ).andExpect(status().isOk());
    }
    @Nested
    class CreationValidation {
        @Test
        void shouldNotCreateAppointmentBecauseInvalidChannel() throws Exception {
            var request = """
                        {
                            "consultantId": "%s",
                            "scheduleId": "%s",
                            "channel": "vido",
                            "reason": "just because"
                        }
                        """.formatted(consultant.getUserId(), schedule.getId());
            mockMvc.perform(
                    post(BASE_URL)
                            .content(request)
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(user(patient.getUserPrincipal()))
            ).andExpect(status().isBadRequest());

            
        }
        @Test
        void shouldNotCreateAppointmentBecauseNoChannel() throws Exception {
            var request = """
                        {
                            "consultantId": "%s",
                            "scheduleId": "%s",
                            "reason": "just because"
                        }
                        """.formatted(consultant.getUserId(), schedule.getId());
            mockMvc.perform(
                    post(BASE_URL)
                            .content(request)
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(user(patient.getUserPrincipal()))
            ).andExpect(status().isBadRequest());

            
        }
        @Test
        void shouldNotCreateAppointmentBecauseNoReason() throws Exception {
            var request = """
                        {
                            "consultantId": "%s",
                            "scheduleId": "%s",
                            "channel": "online"
                            "reason": "just because"
                        }
                        """.formatted(consultant.getUserId(), schedule.getId());
            mockMvc.perform(
                    post(BASE_URL)
                            .content(request)
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(user(patient.getUserPrincipal()))
            ).andExpect(status().isBadRequest());

            
        }
    }
}
