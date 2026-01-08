package com.divjazz.recommendic.medication.IT;

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
import com.divjazz.recommendic.consultation.repository.ConsultationRepository;
import com.divjazz.recommendic.medication.constants.DurationType;
import com.divjazz.recommendic.medication.model.Medication;
import com.divjazz.recommendic.medication.model.Prescription;
import com.divjazz.recommendic.medication.repository.PrescriptionRepository;
import com.divjazz.recommendic.medication.utils.PrescriptionUtils;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@AutoConfigureMockMvc
public class PrescriptionGetUseCaseIT extends BaseIntegrationTest {

    public static final Faker FAKER = new Faker();

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RoleService roleService;
    @Autowired
    private MedicalCategoryService medicalCategoryService;
    @Autowired
    private ConsultantRepository consultantRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;
    @Autowired
    private ObjectMapper objectMapper;


    @Nested
    class ConsultantUseCase {

        private Consultant consultant;

        @BeforeEach
        void setupConsultantUseCase() {
            Role consultantRole = roleService.getRoleByName(ConsultantService.CONSULTANT_ROLE_NAME);
            Role patientRole = roleService.getRoleByName(PatientService.PATIENT_ROLE_NAME);
            MedicalCategoryEntity medicalCategory = medicalCategoryService.getMedicalCategoryById("cardiology");
            Consultant unSavedConsultant = new Consultant(
                    FAKER.internet().emailAddress(),
                    Gender.FEMALE,
                    new UserCredential("password"), consultantRole);
            unSavedConsultant.getUserPrincipal().setEnabled(true);
            unSavedConsultant.setSpecialization(medicalCategory);
            unSavedConsultant.setUserStage(UserStage.ACTIVE_USER);
            unSavedConsultant.setCertified(true);


            ConsultantProfile consultantProfile = ConsultantProfile.builder()
                    .consultant(unSavedConsultant)
                    .languages(new String[]{"English"})
                    .locationOfInstitution(FAKER.careProvider().hospitalName())
                    .title(FAKER.job().title())
                    .dateOfBirth(FAKER.timeAndDate().birthday())
                    .yearsOfExperience(3)
                    .userName(new UserName(FAKER.name().firstName(), FAKER.name().lastName()))
                    .address(new Address(FAKER.address().city(), FAKER.address().state(), FAKER.address().country()))
                    .bio(FAKER.text().text(200))
                    .build();
            unSavedConsultant.setProfile(consultantProfile);
            consultant = consultantRepository.save(unSavedConsultant);

            Patient unsavedPatient = new Patient(
                    FAKER.internet().emailAddress(),
                    Gender.MALE,
                    new UserCredential(FAKER.text().text(20)), patientRole
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

            Patient patient = patientRepository.save(unsavedPatient);

            Schedule unsavedSchedule = Schedule.builder()
                    .zoneOffset(ZoneOffset.ofHours(1))
                    .isActive(true)
                    .endTime(LocalTime.now().plusHours(1))
                    .startTime(LocalTime.now())
                    .consultationChannels(Set.of(ConsultationChannel.ONLINE).toArray(ConsultationChannel[]::new))
                    .consultant(consultant)
                    .name("First ScheduleRecurrence")
                    .build();

            var schedule = scheduleRepository.save(unsavedSchedule);

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
            var appointment = appointmentRepository.save(unsavedAppointment);

            var unsavedConsultation = Consultation.builder()
                    .consultationStatus(ConsultationStatus.ONGOING)
                    .channel(ConsultationChannel.ONLINE)
                    .appointment(appointment)
                    .startedAt(LocalDateTime.now())
                    .build();

            Consultation consultation = consultationRepository.save(unsavedConsultation);

            var prescription = Prescription.builder()
                    .selfReported(false)
                    .consultation(consultation)
                    .prescribedTo(patient)
                    .prescriberId(consultant.getUserId())
                    .diagnosis("Headache")
                    .build();
            var startDate = LocalDate.now();
            Set<Medication> medications = Set.of(Medication.builder()
                    .name("Paracetamol")
                    .dosage("Two tablets")
                    .frequency("Thrice daily")
                    .prescription(prescription)
                    .instructions("Take after meal")
                    .startDate(startDate)
                    .endDate(PrescriptionUtils.getEndDate(startDate,
                            2,
                            DurationType.valueOf("WEEK")))
                    .build()
            );
            prescription.setMedications(medications);

            var savedPrescription = prescriptionRepository.save(prescription);

            log.info("{}", savedPrescription.getPrescriberId());

        }


        @Test
        void shouldGetAllPrescriptionsPrescribedByConsultantIfAny() throws Exception {

            var prescriptions = prescriptionRepository.findAll().stream().map(Prescription::getPrescriberId).toList();
            log.info(prescriptions.getFirst());

            mockMvc.perform(
                            get("/api/v1/prescription")
                                    .with(user(consultant.getUserPrincipal()))
                    ).andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data").isNotEmpty());
        }
    }

    @Nested
    class PatientUseCase {

        Patient patient;

        @BeforeEach
        void setupPatientUseCase() {
            Role consultantRole = roleService.getRoleByName(ConsultantService.CONSULTANT_ROLE_NAME);
            Role patientRole = roleService.getRoleByName(PatientService.PATIENT_ROLE_NAME);
            MedicalCategoryEntity medicalCategory = medicalCategoryService.getMedicalCategoryById("cardiology");
            Consultant unSavedConsultant = new Consultant(
                    FAKER.internet().emailAddress(),
                    Gender.FEMALE,
                    new UserCredential("password"), consultantRole);
            unSavedConsultant.getUserPrincipal().setEnabled(true);
            unSavedConsultant.setSpecialization(medicalCategory);
            unSavedConsultant.setUserStage(UserStage.ACTIVE_USER);
            unSavedConsultant.setCertified(true);


            ConsultantProfile consultantProfile = ConsultantProfile.builder()
                    .consultant(unSavedConsultant)
                    .languages(new String[]{"English"})
                    .locationOfInstitution(FAKER.careProvider().hospitalName())
                    .title(FAKER.job().title())
                    .dateOfBirth(FAKER.timeAndDate().birthday())
                    .yearsOfExperience(3)
                    .userName(new UserName(FAKER.name().firstName(), FAKER.name().lastName()))
                    .address(new Address(FAKER.address().city(), FAKER.address().state(), FAKER.address().country()))
                    .bio(FAKER.text().text(200))
                    .build();
            unSavedConsultant.setProfile(consultantProfile);
            Consultant consultant = consultantRepository.save(unSavedConsultant);

            Patient unsavedPatient = new Patient(
                    FAKER.internet().emailAddress(),
                    Gender.MALE,
                    new UserCredential(FAKER.text().text(20)), patientRole
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

            Schedule unsavedSchedule = Schedule.builder()
                    .zoneOffset(ZoneOffset.ofHours(1))
                    .isActive(true)
                    .endTime(LocalTime.now().plusHours(1))
                    .startTime(LocalTime.now())
                    .consultationChannels(Set.of(ConsultationChannel.ONLINE).toArray(ConsultationChannel[]::new))
                    .consultant(consultant)
                    .name("First ScheduleRecurrence")
                    .build();

            var schedule = scheduleRepository.save(unsavedSchedule);

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
            var appointment = appointmentRepository.save(unsavedAppointment);

            var unsavedConsultation = Consultation.builder()
                    .consultationStatus(ConsultationStatus.ONGOING)
                    .channel(ConsultationChannel.ONLINE)
                    .appointment(appointment)
                    .startedAt(LocalDateTime.now())
                    .build();

            Consultation consultation = consultationRepository.save(unsavedConsultation);

            var prescription = Prescription.builder()
                    .selfReported(false)
                    .consultation(consultation)
                    .prescribedTo(patient)
                    .prescriberId(consultant.getUserId())
                    .diagnosis("Headache")
                    .build();
            var startDate = LocalDate.now();
            Set<Medication> medications = Set.of(Medication.builder()
                    .name("Paracetamol")
                    .dosage("Two tablets")
                    .frequency("Thrice daily")
                    .prescription(prescription)
                    .instructions("Take after meal")
                    .startDate(startDate)
                    .endDate(PrescriptionUtils.getEndDate(startDate,
                            2,
                            DurationType.valueOf("WEEK")))
                    .build()
            );
            prescription.setMedications(medications);

            var savedPrescription = prescriptionRepository.save(prescription);

            log.info("{}", savedPrescription.getPrescriberId());

        }

        @Test
        void shouldGetAllPrescriptionsPrescribedForPatientIfAny() throws Exception {

            mockMvc.perform(
                            get("/api/v1/prescription")
                                    .with(user(patient.getUserPrincipal()))
                    ).andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data").isNotEmpty());
        }

        @Test
        @Transactional
        void shouldGetAllPrescriptionsForTodayIfAny() throws Exception {
            mockMvc.perform(
                    get("/api/v1/prescription/today")
                            .with(user(patient.getUserPrincipal()))
            ).andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data").isNotEmpty());
        }
    }


}
