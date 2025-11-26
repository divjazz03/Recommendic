package com.divjazz.recommendic.consultation.IT;

import com.divjazz.recommendic.BaseIntegrationTest;
import com.divjazz.recommendic.appointment.enums.AppointmentHistory;
import com.divjazz.recommendic.appointment.enums.AppointmentStatus;
import com.divjazz.recommendic.appointment.model.Appointment;
import com.divjazz.recommendic.appointment.model.Schedule;
import com.divjazz.recommendic.appointment.repository.AppointmentRepository;
import com.divjazz.recommendic.appointment.repository.ScheduleRepository;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.consultation.enums.ConsultationStatus;
import com.divjazz.recommendic.consultation.model.Consultation;
import com.divjazz.recommendic.consultation.model.ConsultationSession;
import com.divjazz.recommendic.consultation.repository.ConsultationRepository;
import com.divjazz.recommendic.consultation.repository.ConsultationSessionRepository;
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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;


@AutoConfigureMockMvc
@Slf4j
public class ConsultationIT extends BaseIntegrationTest {

    public static final String CONSULTATION_BASE_ENDPOINT = "/api/v1/consultations";
    public static final Faker FAKER = new Faker();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ConsultantRepository consultantRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private ConsultationRepository consultationRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;

    private Patient patient;
    private Consultant consultant;
    private Appointment appointment;
    private Schedule schedule;

    @Autowired
    private RoleService roleService;
    @Autowired
    private MedicalCategoryService medicalCategoryService;
    private Role consultantRole;
    private Role patientRole;
    private MedicalCategoryEntity medicalCategory;
    @Autowired
    private ConsultationSessionRepository consultationSessionRepository;

    @BeforeEach
    void setup() {
        consultantRole = roleService.getRoleByName(ConsultantService.CONSULTANT_ROLE_NAME);
        patientRole = roleService.getRoleByName(PatientService.PATIENT_ROLE_NAME);
        medicalCategory = medicalCategoryService.getMedicalCategoryByName("cardiology");

        Patient unsavedPatient = new Patient(
                FAKER.internet().emailAddress(),
                Gender.MALE,
                new UserCredential(FAKER.text().text(20)),patientRole
        );
        unsavedPatient.getUserPrincipal().setEnabled(true);
        unsavedPatient.addMedicalCategory(medicalCategory);
        unsavedPatient.setUserStage(UserStage.ACTIVE_USER);

        PatientProfile patientProfile = PatientProfile.builder()
                .address(new Address(FAKER.address().city(), FAKER.address().state(), FAKER.address().country()))
                .phoneNumber(FAKER.phoneNumber().phoneNumber())
                .userName(new UserName(FAKER.name().firstName(), FAKER.name().lastName()))
                .patient(unsavedPatient)
                .build();
        unsavedPatient.setPatientProfile(patientProfile);
        patient = patientRepository.save(unsavedPatient);

        Consultant unsavedConsultant = new Consultant(
                FAKER.internet().emailAddress(),
                Gender.MALE,
                new UserCredential(FAKER.text().text(20)), consultantRole
        );
        unsavedConsultant.getUserPrincipal().setEnabled(true);
        unsavedConsultant.setSpecialization(medicalCategory);
        unsavedConsultant.setUserStage(UserStage.ACTIVE_USER);

        ConsultantProfile consultantProfile = ConsultantProfile.builder()
                .address(new Address(FAKER.address().city(), FAKER.address().state(), FAKER.address().country()))
                .phoneNumber(FAKER.phoneNumber().phoneNumber())
                .userName(new UserName(FAKER.name().firstName(), FAKER.name().lastName()))
                .consultant(unsavedConsultant)
                .build();
        unsavedConsultant.setProfile(consultantProfile);
        consultant = consultantRepository.save(unsavedConsultant);

        Schedule unsavedSchedule = Schedule.builder()
                .zoneOffset(ZoneOffset.ofHours(1))
                .isActive(true)
                .endTime(LocalTime.now().plusHours(1))
                .startTime(LocalTime.now())
                .consultationChannels(Set.of(ConsultationChannel.ONLINE).toArray(ConsultationChannel[]::new))
                .consultant(consultant)
                .name("First ScheduleRecurrence")
                .build();

        schedule = scheduleRepository.save(unsavedSchedule);

        Appointment unsavedAppointment = Appointment.builder()
                .appointmentDate(LocalDate.now())
                .schedule(schedule)
                .consultant(consultant)
                .patient(patient)
                .status(AppointmentStatus.CONFIRMED)
                .consultationChannel(ConsultationChannel.ONLINE)
                .reason("Test reason")
                .history(AppointmentHistory.NEW)
                .build();
        appointment = appointmentRepository.save(unsavedAppointment);
    }


    @Test
    void shouldNotStartConsultationIfAppointmentNotFound() throws Exception {
        mockMvc.perform(
                post("%s/%s/start/%s".formatted(CONSULTATION_BASE_ENDPOINT, 400000L,LocalDateTime.now().toString()))
                        .with(user(consultant.getUserPrincipal()))
        ).andExpect(status().isNotFound());
    }
    @Test
    void shouldNotStartConsultationIfNotAPartOfTheConsultation() throws Exception {
        Patient unsavedPatient = new Patient(
                FAKER.internet().emailAddress(),
                Gender.MALE,
                new UserCredential(FAKER.text().text(20)),patientRole
        );
        unsavedPatient.getUserPrincipal().setEnabled(true);
        unsavedPatient.addMedicalCategory(medicalCategory);
        unsavedPatient.setUserStage(UserStage.ACTIVE_USER);
        PatientProfile patientProfile = PatientProfile.builder()
                .address(new Address(FAKER.address().city(), FAKER.address().state(), FAKER.address().country()))
                .phoneNumber(FAKER.phoneNumber().phoneNumber())
                .userName(new UserName(FAKER.name().firstName(), FAKER.name().lastName()))
                .patient(unsavedPatient)
                .build();
        unsavedPatient.setPatientProfile(patientProfile);
        patient = patientRepository.save(unsavedPatient);

        Consultant unsavedConsultant = new Consultant(
                FAKER.internet().emailAddress(),
                Gender.MALE,
                new UserCredential(FAKER.text().text(20)), consultantRole
        );
        unsavedConsultant.getUserPrincipal().setEnabled(true);
        unsavedConsultant.setSpecialization(medicalCategory);
        unsavedConsultant.setUserStage(UserStage.ACTIVE_USER);
        mockMvc.perform(
                post("%s/%s/start/%s".formatted(CONSULTATION_BASE_ENDPOINT, appointment.getAppointmentId(),appointment.getStartDateAndTime().toString()))
                        .with(user(patient.getUserPrincipal()))
        ).andExpect(status().isForbidden());
    }
    @Test
    void shouldNotRestartConsultationIfAlreadyStarted() throws Exception {
        Consultation startedConsultation = Consultation.builder()
                .appointment(appointment)
                .consultationStatus(ConsultationStatus.ONGOING)
                .channel(appointment.getConsultationChannel())
                .endedAt(LocalDateTime.now())
                .startedAt(LocalDateTime.now())
                .build();

        consultationRepository.save(startedConsultation);
        mockMvc.perform(
                post("%s/%s/start/%s".formatted(CONSULTATION_BASE_ENDPOINT, appointment.getAppointmentId(),appointment.getStartDateAndTime().toString()))
                        .with(user(consultant.getUserPrincipal()))
        ).andExpect(status().isConflict());
        consultationRepository.delete(startedConsultation);
    }
    @Test
    void shouldNotStartLessThan15MinutesToAppointedTime() throws Exception {
        LocalDateTime startTime = LocalDateTime.now();
        Schedule unsavedSchedule = Schedule.builder()
                .zoneOffset(ZoneOffset.ofHours(1))
                .isActive(true)
                .endTime(startTime.toLocalTime().plusHours(2).plusMinutes(30))
                .startTime(startTime.toLocalTime().plusMinutes(20))
                .consultationChannels(Set.of(ConsultationChannel.ONLINE).toArray(ConsultationChannel[]::new))
                .consultant(consultant)
                .name("First ScheduleRecurrence")
                .build();

        var savedSchedule = scheduleRepository.save(unsavedSchedule);

        Appointment unsavedAppointment = Appointment.builder()
                .appointmentDate(startTime.toLocalDate())
                .schedule(savedSchedule)
                .consultant(consultant)
                .patient(patient)
                .status(AppointmentStatus.CONFIRMED)
                .consultationChannel(ConsultationChannel.ONLINE)
                .reason("Test reason")
                .history(AppointmentHistory.NEW)
                .build();
        var appointment = appointmentRepository.save(unsavedAppointment);
        mockMvc.perform(
                post("%s/%s/start/%s".formatted(CONSULTATION_BASE_ENDPOINT, appointment.getAppointmentId(),startTime.format(DateTimeFormatter.ISO_DATE_TIME)))
                        .with(user(consultant.getUserPrincipal()))
        ).andExpect(status().isBadRequest());
    }
    @Test
    void shouldStartConsultationIfAppointmentExistsAndNotStarted() throws Exception {
        var result = mockMvc.perform(
                post("%s/%s/start/%s".formatted(CONSULTATION_BASE_ENDPOINT, appointment.getAppointmentId(),appointment.getStartDateAndTime().toString()))
                        .with(user(consultant.getUserPrincipal()))
        ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        log.info("result: {}", result);

    }
    @Test
    void shouldNotStartConsultationIfAlreadyStarted() throws Exception {
        Consultation startedConsultation = Consultation.builder()
                .appointment(appointment)
                .consultationStatus(ConsultationStatus.ONGOING)
                .channel(appointment.getConsultationChannel())
                .endedAt(LocalDateTime.now())
                .startedAt(LocalDateTime.now())
                .build();

        consultationRepository.save(startedConsultation);
        mockMvc.perform(
                post("%s/%s/start/%s".formatted(CONSULTATION_BASE_ENDPOINT, appointment.getAppointmentId(), appointment.getStartDateAndTime().toString()))
                        .with(user(consultant.getUserPrincipal()))
        ).andExpect(status().isConflict());
        consultationRepository.delete(startedConsultation);
    }

    @Test
    void shouldCompleteConsultationSessionWhenExists() throws Exception {
        ConsultationSession session = new ConsultationSession(
                patient,
                consultant
        );
        var consultationSession = consultationSessionRepository.save(session);

        Consultation consultationToStart = Consultation.builder()
                .appointment(appointment)
                .consultationStatus(ConsultationStatus.ONGOING)
                .channel(appointment.getConsultationChannel())
                .endedAt(LocalDateTime.now())
                .startedAt(LocalDateTime.now())
                .session(consultationSession)
                .build();

        String request = """
                {
                    "summary": "Summary of the consultation",
                    "consultationId": "%s",
                    "patientStatus": "stable"
                }
                """;
        consultationToStart = consultationRepository.save(consultationToStart);
        consultationSession.addConsultation(consultationToStart);
        consultationSessionRepository.save(consultationSession);

        mockMvc.perform(
                post("%s/complete".formatted(CONSULTATION_BASE_ENDPOINT))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request.formatted(consultationToStart.getConsultationId()))
                        .with(user(consultant.getUserPrincipal()))
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));

    }
}
