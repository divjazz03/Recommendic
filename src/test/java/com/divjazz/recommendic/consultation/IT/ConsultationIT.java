package com.divjazz.recommendic.consultation.IT;

import com.divjazz.recommendic.BaseIntegrationTest;
import com.divjazz.recommendic.appointment.enums.AppointmentStatus;
import com.divjazz.recommendic.appointment.model.Appointment;
import com.divjazz.recommendic.appointment.model.Schedule;
import com.divjazz.recommendic.appointment.repository.AppointmentRepository;
import com.divjazz.recommendic.appointment.repository.ScheduleRepository;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.consultation.enums.ConsultationStatus;
import com.divjazz.recommendic.consultation.model.Consultation;
import com.divjazz.recommendic.consultation.repository.ConsultationRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.*;
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

    @BeforeEach
    void setup() {
        Patient unsavedPatient = new Patient(
                FAKER.internet().emailAddress(),
                Gender.MALE,
                new UserCredential(FAKER.text().text(20))
        );
        unsavedPatient.getUserPrincipal().setEnabled(true);
        unsavedPatient.setMedicalCategories(new String[]{});
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
                new UserCredential(FAKER.text().text(20))
        );
        unsavedConsultant.getUserPrincipal().setEnabled(true);
        unsavedConsultant.setMedicalCategory(MedicalCategoryEnum.CARDIOLOGY);
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
                .isRecurring(false)
                .isActive(true)
                .endTime(LocalTime.now().plusHours(1))
                .startTime(LocalTime.now())
                .consultationChannels(Set.of(ConsultationChannel.VOICE).toArray(ConsultationChannel[]::new))
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
                .consultationChannel(ConsultationChannel.VOICE)
                .build();
        appointment = appointmentRepository.save(unsavedAppointment);
    }


    @Test
    void shouldNotStartConsultationIfAppointmentNotFound() throws Exception {
        mockMvc.perform(
                post("%s/%s/start".formatted(CONSULTATION_BASE_ENDPOINT, 400000L))
                        .with(user(consultant.getUserPrincipal()))
        ).andExpect(status().isNotFound());
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
                post("%s/%s/start".formatted(CONSULTATION_BASE_ENDPOINT, appointment.getId()))
                        .with(user(consultant.getUserPrincipal()))
        ).andExpect(status().isConflict());
        consultationRepository.delete(startedConsultation);
    }
    @Test
    void shouldNotStartLessThan15MinutesToAppointedTime() throws Exception {
        LocalTime startTime = LocalTime.now().plusMinutes(15).plusSeconds(30);
        Schedule unsavedSchedule = Schedule.builder()
                .zoneOffset(ZoneOffset.ofHours(1))
                .isRecurring(false)
                .isActive(true)
                .endTime(startTime.plusHours(2).plusMinutes(30))
                .startTime(startTime)
                .consultationChannels(Set.of(ConsultationChannel.VOICE).toArray(ConsultationChannel[]::new))
                .consultant(consultant)
                .name("First ScheduleRecurrence")
                .build();

        var savedSchedule = scheduleRepository.save(unsavedSchedule);

        Appointment unsavedAppointment = Appointment.builder()
                .appointmentDate(LocalDate.of(2025, Month.OCTOBER, 23))
                .schedule(savedSchedule)
                .consultant(consultant)
                .patient(patient)
                .status(AppointmentStatus.CONFIRMED)
                .consultationChannel(ConsultationChannel.VOICE)
                .build();
        var appointment = appointmentRepository.save(unsavedAppointment);
        mockMvc.perform(
                post("%s/%s/start".formatted(CONSULTATION_BASE_ENDPOINT, appointment.getId()))
                        .with(user(consultant.getUserPrincipal()))
        ).andExpect(status().isBadRequest());
    }
    @Test
    void shouldStartConsultationIfAppointmentExistsAndNotStarted() throws Exception {
        var result = mockMvc.perform(
                post("%s/%s/start".formatted(CONSULTATION_BASE_ENDPOINT, appointment.getId()))
                        .with(user(consultant.getUserPrincipal()))
        ).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
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
                post("%s/%s/start".formatted(CONSULTATION_BASE_ENDPOINT, appointment.getId()))
                        .with(user(consultant.getUserPrincipal()))
        ).andExpect(status().isConflict());
        consultationRepository.delete(startedConsultation);
    }

    @Test
    void shouldCompleteConsultationSessionWhenExists() throws Exception {
        Consultation startedConsultation = Consultation.builder()
                .appointment(appointment)
                .consultationStatus(ConsultationStatus.ONGOING)
                .channel(appointment.getConsultationChannel())
                .endedAt(LocalDateTime.now())
                .startedAt(LocalDateTime.now())
                .build();
        String jsonSummary = """
                {
                    "summary": "Summary of the consultation"
                }
                """;
        consultationRepository.save(startedConsultation);
        mockMvc.perform(
                post("%s/%s/complete".formatted(CONSULTATION_BASE_ENDPOINT, startedConsultation.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonSummary)
                        .with(user(consultant.getUserPrincipal()))
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));

    }
}
