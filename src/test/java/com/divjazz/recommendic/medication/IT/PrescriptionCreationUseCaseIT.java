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
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Slf4j
public class PrescriptionCreationUseCaseIT extends BaseIntegrationTest {

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

    private Consultant consultant;
    private Patient patient;
    private Consultation consultation;
    private Role consultantRole;
    private Role patientRole;
    private MedicalCategoryEntity medicalCategory;

    @BeforeEach
    void setup() {
        consultantRole = roleService.getRoleByName(ConsultantService.CONSULTANT_ROLE_NAME);
        patientRole = roleService.getRoleByName(PatientService.PATIENT_ROLE_NAME);
        medicalCategory = medicalCategoryService.getMedicalCategoryById("cardiology");
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

        consultation = consultationRepository.save(unsavedConsultation);

    }


    @Test
    void shouldCreatePrescriptionIfValidConsultation_PatientExists_AuthorizedAndValidRequest() throws Exception {

        var request = """
                {
                    "consultationId": "%s",
                    "prescribedTo": "%s",
                    "diagnosis": "Head ache",
                    "medications": [{
                        "name": "paracetamol",
                        "dosage": "two tablets",
                        "medicationFrequency": "Three times daily",
                        "durationValue": 1,
                        "durationType": "WEEK",
                        "instructions": "Take after meal"
                    }],
                    "notes": "Please follow the prescription judiciously"
                }
                """.formatted(consultation.getConsultationId(),
                patient.getUserId());

        var results = mockMvc.perform(
                post("/api/v1/prescription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                        .with(user(consultant.getUserPrincipal()))
        ).andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        
    }

    @Test
    void shouldCreatePrescriptionIfValidConsultation_PatientNotValid_AuthorizedAndValidRequest() throws Exception {

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

        var request = """
                {
                    "consultationId": "%s",
                    "prescribedTo": "%s",
                    "diagnosis": "Head ache",
                    "medications": [{
                        "name": "paracetamol",
                        "dosage": "two tablets",
                        "medicationFrequency": "Three times daily",
                        "durationValue": 1,
                        "durationType": "WEEK",
                        "instructions": "Take after meal"
                    }],
                    "notes": "Please follow the prescription judiciously"
                }
                """.formatted(consultation.getConsultationId(),
                patient.getUserId());

        var results = mockMvc.perform(
                        post("/api/v1/prescription")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                                .with(user(consultant.getUserPrincipal()))
                ).andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        
    }



    @Test
    void shouldCreatePrescriptionIfValidConsultation_PatientExists_NotAuthorizedAndValidRequest() throws Exception {

        var request = """
                {
                    "consultationId": "%s",
                    "prescribedTo": "%s",
                    "diagnosis": "Head ache",
                    "medications": [{
                        "name": "paracetamol",
                        "dosage": "two tablets",
                        "medicationFrequency": "Three times daily",
                        "durationValue": 1,
                        "durationType": "WEEK",
                        "instructions": "Take after meal"
                    }],
                    "notes": "Please follow the prescription judiciously"
                }
                """.formatted(consultation.getConsultationId(),
                patient.getUserId());

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
        var unAuthorizedConsultant = consultantRepository.save(unSavedConsultant);

        var results = mockMvc.perform(
                        post("/api/v1/prescription")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                                .with(user(unAuthorizedConsultant.getUserPrincipal()))
                ).andExpect(status().isForbidden())
                .andReturn().getResponse().getContentAsString();

        
    }

    @Test
    void shouldNotCreatePrescriptionIfInvalidConsultation_PatientExistsAndAuthorizedAndValidRequest() throws Exception {
        var request = """
                {
                    "consultationId": "%s",
                    "prescribedTo": "%s",
                    "diagnosis": "Head ache",
                    "medications": [{
                        "name": "paracetamol",
                        "dosage": "two tablets",
                        "medicationFrequency": "Three times daily",
                        "durationValue": 1,
                        "durationType": "WEEK",
                        "instructions": "Take after meal"
                    }],
                    "notes": "Please follow the prescription judiciously"
                }
                """.formatted("Wrong consultation Id",
                patient.getUserId());

        var results = mockMvc.perform(
                        post("/api/v1/prescription")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                                .with(user(consultant.getUserPrincipal()))
                ).andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        
    }

    @Test
    void shouldNotCreatePrescriptionIfConsultation_NotPatientExistsAndAuthorizedAndValidRequest() throws Exception {
        var request = """
                {
                    "consultationId": "%s",
                    "prescribedTo": "%s",
                    "diagnosis": "Head ache",
                    "medications": [{
                        "name": "paracetamol",
                        "dosage": "two tablets",
                        "medicationFrequency": "Three times daily",
                        "durationValue": 1,
                        "durationType": "WEEK",
                        "instructions": "Take after meal"
                    }],
                    "notes": "Please follow the prescription judiciously"
                }
                """.formatted(consultation.getConsultationId(),
                "patientId That does not exist");

        var results = mockMvc.perform(
                        post("/api/v1/prescription")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                                .with(user(consultant.getUserPrincipal()))
                ).andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        
    }

    @Nested
    class ValidationTest {
        @Test
        void shouldNotCreatePrescriptionBecauseNoName() throws Exception {

            var request = """
                {
                    "consultationId": "%s",
                    "prescribedTo": "%s",
                    "diagnosis": "A diagnosis",
                    "medications": [{
                        "name": "",
                        "dosage": "A Dosage",
                        "medicationFrequency": "2 times daily",
                        "durationValue": 1,
                        "durationType": "WEEK",
                        "instructions": "No instructions"
                    }],
                    "notes": "Please follow the prescription judiciously"
                }
                """.formatted(consultation.getConsultationId(),
                    patient.getUserId());

            var results = mockMvc.perform(
                            post("/api/v1/prescription")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(request)
                                    .with(user(consultant.getUserPrincipal()))
                    ).andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            
        }

        @Test
        void shouldNotCreatePrescriptionBecauseNoDosage() throws Exception {

            var request = """
                {
                    "consultationId": "%s",
                    "prescribedTo": "%s",
                    "diagnosis": "A diagnosis",
                    "medications": [{
                        "name": "A name",
                        "dosage": "",
                        "medicationFrequency": "2 times daily",
                        "durationValue": 1,
                        "durationType": "WEEK",
                        "instructions": "No instructions"
                    }],
                    "notes": "Please follow the prescription judiciously"
                }
                """.formatted(consultation.getConsultationId(),
                    patient.getUserId());

            var results = mockMvc.perform(
                            post("/api/v1/prescription")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(request)
                                    .with(user(consultant.getUserPrincipal()))
                    ).andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.properties.data[0].error", IsEqual.equalTo("Dosage is required")))
                    .andReturn().getResponse().getContentAsString();

            
        }

        @Test
        void shouldNotCreatePrescriptionBecauseNoDiagnosis() throws Exception {

            var request = """
                {
                    "consultationId": "%s",
                    "prescribedTo": "%s",
                    "diagnosis": "",
                    "medications": [{
                        "name": "A name",
                        "dosage": "A dosage",
                        "medicationFrequency": "2 times daily",
                        "durationValue": 1,
                        "durationType": "WEEK",
                        "instructions": "No instructions"
                    }],
                    "notes": "Please follow the prescription judiciously"
                }
                """.formatted(consultation.getConsultationId(),
                    patient.getUserId());

            var results = mockMvc.perform(
                            post("/api/v1/prescription")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(request)
                                    .with(user(consultant.getUserPrincipal()))
                    ).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.properties.data[0].error", IsEqual.equalTo("Diagnosis is required")))
                    .andReturn().getResponse().getContentAsString();

            
        }
        @Test
        void shouldNotCreatePrescriptionBecauseNoPrescribedTo() throws Exception {

            var request = """
                {
                    "consultationId": "%s",
                    "diagnosis": "A diagnosis",
                    "medications": [{
                        "name": "A name",
                        "dosage": "A dosage",
                        "medicationFrequency": "2 times daily",
                        "durationValue": 1,
                        "durationType": "WEEK",
                        "instructions": "No instructions"
                    }],
                    "notes": "Please follow the prescription judiciously"
                }
                """.formatted(consultation.getConsultationId(),
                    patient.getUserId());

            var results = mockMvc.perform(
                            post("/api/v1/prescription")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(request)
                                    .with(user(consultant.getUserPrincipal()))
                    ).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.properties.data[0].error", IsEqual.equalTo("prescribedTo is required")))
                    .andReturn().getResponse().getContentAsString();

            
        }
        @Test
        void shouldNotCreatePrescriptionBecauseNoMedicalFrequency() throws Exception {

            var request = """
                {
                    "consultationId": "%s",
                    "prescribedTo": "%s",
                    "diagnosis": "A diagnosis",
                    "medications": [{
                        "name": "A name",
                        "dosage": "A dosage",
                        "medicationFrequency": "",
                        "durationValue": 1,
                        "durationType": "WEEK",
                        "instructions": "No instructions"
                    }],
                    "notes": "Please follow the prescription judiciously"
                }
                """.formatted(consultation.getConsultationId(),
                    patient.getUserId());

            var results = mockMvc.perform(
                            post("/api/v1/prescription")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(request)
                                    .with(user(consultant.getUserPrincipal()))
                    ).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.properties.data[0].error", IsEqual.equalTo("frequency is required")))
                    .andReturn().getResponse().getContentAsString();

            
        }
        @Test
        void shouldNotCreatePrescriptionBecauseNoDurationValue() throws Exception {

            var request = """
                {
                    "consultationId": "%s",
                    "prescribedTo": "%s",
                    "diagnosis": "A diagnosis",
                    "medications": [{
                        "name": "A name",
                        "dosage": "A dosage",
                        "medicationFrequency": "2 times daily",
                        "durationType": "WEEK",
                        "instructions": "No instructions"
                    }],
                    "notes": "Please follow the prescription judiciously"
                }
                """.formatted(consultation.getConsultationId(),
                    patient.getUserId());

            var results = mockMvc.perform(
                            post("/api/v1/prescription")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(request)
                                    .with(user(consultant.getUserPrincipal()))
                    ).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.properties.data[0].error", IsEqual.equalTo("durationValue is required")))
                    .andReturn().getResponse().getContentAsString();

            
        }
        @Test
        void shouldNotCreatePrescriptionBecauseNoDurationType() throws Exception {

            var request = """
                {
                    "consultationId": "%s",
                    "prescribedTo": "%s",
                    "diagnosis": "Head ache",
                    "medications": [{
                        "name": "paracetamol",
                        "dosage": "two tablets",
                        "medicationFrequency": "Three times daily",
                        "durationValue": 1,
                        "instructions": "Take after meal"
                    }],
                    "notes": "Please follow the prescription judiciously"
                }
                """.formatted(consultation.getConsultationId(),
                    patient.getUserId());

            var results = mockMvc.perform(
                            post("/api/v1/prescription")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(request)
                                    .with(user(consultant.getUserPrincipal()))
                    ).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.properties.data[0].error", IsEqual.equalTo("durationType is required")))
                    .andReturn().getResponse().getContentAsString();

            
        }

        @Test
        void shouldNotCreatePrescriptionBecauseInvalidDurationType() throws Exception {

            var request = """
                {
                    "consultationId": "%s",
                    "prescribedTo": "%s",
                    "diagnosis": "Head ache",
                    "medications": [{
                        "name": "paracetamol",
                        "dosage": "two tablets",
                        "medicationFrequency": "Three times daily",
                        "durationValue": 1,
                        "durationType": "WeEEK",
                        "instructions": "Take after meal"
                    }],
                    "notes": "Please follow the prescription judiciously"
                }
                """.formatted(consultation.getConsultationId(),
                    patient.getUserId());

            var results = mockMvc.perform(
                            post("/api/v1/prescription")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(request)
                                    .with(user(consultant.getUserPrincipal()))
                    ).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.properties.data[0].error", IsEqual.equalTo("durationType is invalid")))
                    .andReturn().getResponse().getContentAsString();

            
        }
        @Test
        void shouldNotCreatePrescriptionBecauseInvalidDurationValue() throws Exception {

            var request = """
                {
                    "consultationId": "%s",
                    "prescribedTo": "%s",
                    "diagnosis": "Head ache",
                    "medications": [{
                        "name": "paracetamol",
                        "dosage": "two tablets",
                        "medicationFrequency": "Three times daily",
                        "durationValue": 0,
                        "durationType": "WEEK",
                        "instructions": "Take after meal"
                    }],
                    "notes": "Please follow the prescription judiciously"
                }
                """.formatted(consultation.getConsultationId(),
                    patient.getUserId());

            var results = mockMvc.perform(
                            post("/api/v1/prescription")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(request)
                                    .with(user(consultant.getUserPrincipal()))
                    ).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.properties.data[0].error", IsEqual.equalTo("Duration should not be less than 1")))
                    .andReturn().getResponse().getContentAsString();

            
        }
    }



}
