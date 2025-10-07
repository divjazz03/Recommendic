package com.divjazz.recommendic.user.IT;

import com.divjazz.recommendic.BaseIntegrationTest;
import com.divjazz.recommendic.global.Response;
import com.divjazz.recommendic.global.exception.GlobalControllerExceptionAdvice;
import com.divjazz.recommendic.global.general.PageResponse;
import com.divjazz.recommendic.user.dto.PatientInfoResponse;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.model.Admin;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.AdminProfile;
import com.divjazz.recommendic.user.model.userAttributes.PatientProfile;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.AdminRepository;
import com.divjazz.recommendic.user.repository.PatientRepository;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class PatientIT extends BaseIntegrationTest {

    private static final Faker FAKER = new Faker();
    private static final String PATIENT_BASE_ENDPOINT = "/api/v1/patients";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JacksonTester<Response<PatientInfoResponse>> patientInfoJacksonTester;
    @Autowired
    private JacksonTester<Response<GlobalControllerExceptionAdvice.ValidationErrorResponse>> validationErrorresponseJacksonTester;
    @Autowired
    private JacksonTester<Response<PageResponse<PatientInfoResponse>>> patientsPageResponseJacksonTester;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private AdminRepository adminRepository;

    private Patient patient;
    private Admin admin;


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
                .dateOfBirth(FAKER.timeAndDate().birthday())
                .userName(new UserName(FAKER.name().firstName(), FAKER.name().lastName()))
                .patient(unsavedPatient)
                .build();
        unsavedPatient.setPatientProfile(patientProfile);
        patient = patientRepository.save(unsavedPatient);
        Admin unSavedAdmin = new Admin(
                FAKER.internet().emailAddress(),
                Gender.MALE,
                new UserCredential("adminPassword")
        );
        unSavedAdmin.getUserPrincipal().setEnabled(true);
        unSavedAdmin.setUserStage(UserStage.ACTIVE_USER);

        AdminProfile adminProfile = AdminProfile.builder()
                .userName(new UserName(FAKER.name().firstName(), FAKER.name().lastName()))
                .address(new Address(FAKER.address().city(), FAKER.address().state(), FAKER.address().country()))
                .phoneNumber(FAKER.phoneNumber().phoneNumber())
                .admin(unSavedAdmin)
                .build();
        unSavedAdmin.setAdminProfile(adminProfile);

        admin = adminRepository.save(unSavedAdmin);

    }

    @Test
    void shouldCreatePatientWithValidRequestParameterAndReturn201Created() throws Exception {
        var jsonRequest = """
                {
                      "city": "Ibadan",
                      "country": "Nigeria",
                      "email": "divjazz1@gmail.com",
                      "firstName": "Divine",
                      "gender": "Male",
                      "lastName": "Maduka",
                      "password": "june12003dsd",
                      "dateOfBirth": "2001-03-21",
                      "state": "Oyo"
                }
                """;

        var response = mockMvc.perform(
                post(PATIENT_BASE_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated())
                .andReturn().getResponse();
        var patientInfoResponseResponse = patientInfoJacksonTester.parseObject(response.getContentAsString());
    }

    private static Stream<Arguments> invalidCreateUserArguments() {
        return Stream.of(Arguments.of("""
                {
                      "city": "",
                      "country": "Nigeria",
                      "email": "divjazz9gmail.com",
                      "firstName": "Divine",
                      "gender": "Mal",
                      "lastName": "Maduka",
                      "password": "june12003dsd",
                      "phoneNumber": "+2347046641978",
                      "state": "Oyo"
                }
                """),
                Arguments.of("""
                {
                      "city": "Ibadan",
                      "country": "Nigeria",
                      "email": "divjazz9@gmail.com",
                      "firstName": "Divine",
                      "gender": "Ma",
                      "lastName": "Maduka",
                      "password": "june120",
                      "phoneNumber": "+2347046641978",
                      "state": "Oyo"
                }
                """));
    }

    @ParameterizedTest
    @MethodSource(value = "invalidCreateUserArguments")
    void shouldNotCreateUserGivenInvalidRequestBodyAndReturnA400(String jsonRequest) throws Exception {

        var responseString = mockMvc.perform(
                post(PATIENT_BASE_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
        ).andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        var validationError = validationErrorresponseJacksonTester.parse(responseString).getObject();

        assertThat(validationError.data().errors()).isNotEmpty();

    }
    @Test
    void shouldNotGetPatientsIFNotAuthorizedAndReturn403() throws Exception {
        mockMvc.perform(
                        get(PATIENT_BASE_ENDPOINT)
                ).andExpect(status().isForbidden())
                .andReturn().getResponse().getContentAsString();
    }
    @Test
    void shouldGetPatientsWhenAuthorizedAndIFExists() throws Exception{
        populatePatients();
        var responseString = mockMvc.perform(
                        get(PATIENT_BASE_ENDPOINT)
                                .with(user(patient.getUserPrincipal()))
                ).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var patientResponse = patientsPageResponseJacksonTester.parse(responseString).getObject();
        assertThat(patientResponse.data().empty()).isFalse();
    }

    @Test
    void shouldDeletePatientIfExists() throws Exception {
        Patient unsavedPatient = new Patient(
                FAKER.internet().emailAddress(),
                Gender.FEMALE,
                new UserCredential(FAKER.text().text(20))
        );
        unsavedPatient.getUserPrincipal().setEnabled(true);
        unsavedPatient.setMedicalCategories(new String[]{});
        unsavedPatient.setUserStage(UserStage.ACTIVE_USER);

        PatientProfile patientProfile = PatientProfile.builder()
                .address(new Address(FAKER.address().city(), FAKER.address().state(), FAKER.address().country()))
                .dateOfBirth(FAKER.timeAndDate().birthday())
                .userName(new UserName(FAKER.name().firstName(), FAKER.name().lastName()))
                .patient(unsavedPatient)
                .build();
        unsavedPatient.setPatientProfile(patientProfile);
        var savedPatient = patientRepository.save(unsavedPatient);
        var patientId = savedPatient.getUserId();
        var patient = patientRepository.findByUserId(patientId).orElseThrow();
        log.info("patient {}", patient.getUserId());

        mockMvc.perform(
                delete("%s/%s".formatted(PATIENT_BASE_ENDPOINT,patientId))
                        .with(user(admin.getUserPrincipal()))
        ).andExpect(status().isNoContent());
        mockMvc.perform(
                get("%s/%s".formatted(PATIENT_BASE_ENDPOINT,patientId))
                        .with(user(admin.getUserPrincipal()))
        ).andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404IfUserTobeDeletedDoesNotExist() throws Exception {
        mockMvc.perform(
                delete("%s/%s".formatted(PATIENT_BASE_ENDPOINT, UUID.randomUUID()))
                        .with(user(admin.getUserPrincipal()))
        ).andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteIfConsultantIsThisUser() throws Exception {
        mockMvc.perform(
                delete("%s/%s".formatted(PATIENT_BASE_ENDPOINT,patient.getUserId()))
                        .with(user(admin.getUserPrincipal()))
        ).andExpect(status().isNoContent());
        mockMvc.perform(
                get("%s/%s".formatted(PATIENT_BASE_ENDPOINT,patient.getUserId()))
                        .with(user(admin.getUserPrincipal()))
        ).andExpect(status().isNotFound());
    }

    @Test
    void shouldHandleOnboardingRequestAndReturnOk() throws Exception {

        var patientInOnboardingStage = new Patient(FAKER.internet().emailAddress(), Gender.MALE, new UserCredential("password"));
        patientInOnboardingStage.setUserStage(UserStage.ONBOARDING);
        patientInOnboardingStage.getUserPrincipal().setEnabled(true);
        patientInOnboardingStage.setMedicalCategories(new String[]{});
        PatientProfile patientProfile = PatientProfile.builder()
                .address(new Address(FAKER.address().city(), FAKER.address().state(), FAKER.address().country()))
                .dateOfBirth(FAKER.timeAndDate().birthday())
                .userName(new UserName(FAKER.name().firstName(), FAKER.name().lastName()))
                .patient(patientInOnboardingStage)
                .build();
        patientInOnboardingStage.setPatientProfile(patientProfile);
        patientRepository.save(patientInOnboardingStage);

        var onboardingData = """
                {
                    "medicalCategories": ["pediatrician","cardiology"]
                }
                """;

        mockMvc.perform(
                post("%s/%s/onboard".formatted(PATIENT_BASE_ENDPOINT,patientInOnboardingStage.getUserId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(onboardingData)
                        .with(user(patientInOnboardingStage.getUserPrincipal()))

        ).andExpect(status().isOk());
    }

    private void populatePatients() {
        Set<Patient> patients = new HashSet<>(10);
        IntStream.range(0,10)
                .forEach(i -> {
                    Patient unsavedPatient = new Patient(
                            FAKER.internet().emailAddress(),
                            Gender.FEMALE,
                            new UserCredential(FAKER.text().text(20))
                    );
                    unsavedPatient.getUserPrincipal().setEnabled(true);
                    unsavedPatient.setMedicalCategories(new String[]{});
                    unsavedPatient.setUserStage(UserStage.ACTIVE_USER);

                    PatientProfile patientProfile = PatientProfile.builder()
                            .address(new Address(FAKER.address().city(), FAKER.address().state(), FAKER.address().country()))
                            .dateOfBirth(FAKER.timeAndDate().birthday())
                            .userName(new UserName(FAKER.name().firstName(), FAKER.name().lastName()))
                            .patient(unsavedPatient)
                            .build();
                    unsavedPatient.setPatientProfile(patientProfile);
                    patients.add(unsavedPatient);
                });

        patientRepository.saveAll(patients);
    }




}
