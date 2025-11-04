package com.divjazz.recommendic.user.IT;

import com.divjazz.recommendic.BaseIntegrationTest;
import com.divjazz.recommendic.global.Response;
import com.divjazz.recommendic.global.exception.GlobalControllerExceptionAdvice;
import com.divjazz.recommendic.global.general.PageResponse;
import com.divjazz.recommendic.user.controller.consultant.payload.ConsultantInfoResponse;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.model.Admin;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.MedicalCategoryEntity;
import com.divjazz.recommendic.user.model.certification.ConsultantEducation;
import com.divjazz.recommendic.user.model.userAttributes.*;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.AdminRepository;
import com.divjazz.recommendic.user.repository.ConsultantRepository;
import com.divjazz.recommendic.user.repository.certificationRepo.ConsultantEducationRepository;
import com.divjazz.recommendic.user.service.AdminService;
import com.divjazz.recommendic.user.service.ConsultantService;
import com.divjazz.recommendic.user.service.MedicalCategoryService;
import com.divjazz.recommendic.user.service.RoleService;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.*;

@Slf4j
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class ConsultantIT extends BaseIntegrationTest {
    private static final Faker FAKER = new Faker();
    public static final String CONSULTANT_BASE_ENDPOINT = "/api/v1/consultants";
    @Autowired
    public MockMvc mockMvc;
    @Autowired
    public ConsultantRepository consultantRepository;
    @Autowired
    public AdminRepository adminRepository;
    @Autowired
    public JacksonTester<Response<ConsultantInfoResponse>> jacksonTester;
    @Autowired
    private JacksonTester<Response<GlobalControllerExceptionAdvice.ValidationErrorResponse>> validationErrorresponseJacksonTester;
    @Autowired
    private JacksonTester<Response<PageResponse<ConsultantInfoResponse>>> consultantPageResponseJacksonTester;
    @Autowired
    private RoleService roleService;
    @Autowired
    private MedicalCategoryService medicalCategoryService;

    public Consultant consultant;
    public Admin admin;
    public Role adminRole;
    public Role consultantRole;
    public MedicalCategoryEntity medicalCategory;
    @Autowired
    private ConsultantEducationRepository consultantEducationRepository;

    @BeforeEach
    void setUp() {
        consultantRole = roleService.getRoleByName(ConsultantService.CONSULTANT_ROLE_NAME);
        medicalCategory = medicalCategoryService.getMedicalCategoryByName("cardiology");
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
//        ConsultantEducation consultantEducation = new ConsultantEducation(
//                consultant,
//                FAKER.university().degree(),
//                FAKER.university().name(),
//                2004);
//
//        consultantEducationRepository.save(consultantEducation);
        adminRole = roleService.getRoleByName(AdminService.ADMIN_ROLE_NAME);
        Admin unSavedAdmin = new Admin(
                FAKER.internet().emailAddress(),
                Gender.MALE,
                new UserCredential("adminPassword"),
                adminRole
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
    void shouldCreateConsultantWithValidRequestParameterAndReturn201Created() throws Exception {
        var jsonRequest = """
                {
                      "city": "Ibadan",
                      "country": "Nigeria",
                      "email": "divjazz0@gmail.com",
                      "firstName": "Divine",
                      "gender": "Male",
                      "lastName": "Maduka",
                      "password": "june12003dsd",
                      "dateOfBirth": "2003-03-17",
                      "state": "Oyo"
                }
                """;

        var response = mockMvc.perform(
                        post(CONSULTANT_BASE_ENDPOINT)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        var consultantResponse = jacksonTester.parseObject(response);
        assertThat(consultantResponse.data()).isNotNull();

    }

    private static Stream<Arguments> invalidCreateConsultantArguments() {
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
    @MethodSource(value = "invalidCreateConsultantArguments")
    void shouldNotCreateUserGivenInvalidRequestBodyAndReturnA400(String jsonRequest) throws Exception {

        var responseString = mockMvc.perform(
                        post(CONSULTANT_BASE_ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest)
                ).andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        var validationError = validationErrorresponseJacksonTester.parse(responseString).getObject();

        assertThat(validationError.data().errors()).isNotEmpty();

    }

    @Test
    void shouldNotGetConsultantIFNotAuthorizedAndReturn403() throws Exception {
        mockMvc.perform(
                        get(CONSULTANT_BASE_ENDPOINT)
                ).andExpect(status().isForbidden());
    }
    @Test
    void shouldGetConsultantsWhenAuthorizedAndIFExists() throws Exception {
        populateConsultants();
        var responseString = mockMvc.perform(
                        get(CONSULTANT_BASE_ENDPOINT)
                                .with(user(consultant.getUserPrincipal()))
                ).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var consultantResponse = consultantPageResponseJacksonTester.parse(responseString).getObject();
        assertThat(consultantResponse.data().empty()).isFalse();
    }
    @Test
    void shouldDeleteConsultantIFExists() throws Exception {
        Consultant unsavedConsultant = new Consultant(
                FAKER.internet().emailAddress(),
                Gender.FEMALE,
                new UserCredential(FAKER.text().text(20)),
                consultantRole
        );
        unsavedConsultant.getUserPrincipal().setEnabled(true);
        unsavedConsultant.setSpecialization(medicalCategory);
        unsavedConsultant.setUserStage(UserStage.ACTIVE_USER);

        ConsultantProfile consultantProfile = ConsultantProfile.builder()
                .address(new Address(FAKER.address().city(), FAKER.address().state(), FAKER.address().country()))
                .dateOfBirth(FAKER.timeAndDate().birthday())
                .userName(new UserName(FAKER.name().firstName(), FAKER.name().lastName()))
                .consultant(unsavedConsultant)
                .build();
        unsavedConsultant.setProfile(consultantProfile);
        var savedConsultant = consultantRepository.save(unsavedConsultant);
        var consultantUserId = savedConsultant.getUserId();

        mockMvc.perform(
                delete("%s/%s".formatted(CONSULTANT_BASE_ENDPOINT, consultantUserId))
                        .with(user(admin.getUserPrincipal()))
        ).andExpect(status().isNoContent());
        mockMvc.perform(
                    get("%s/%s".formatted(CONSULTANT_BASE_ENDPOINT, consultantUserId))
                        .with(user(admin.getUserPrincipal()))
        ).andExpect(status().isNotFound());
    }
    @Test
    void shouldDeleteIfConsultantIsThisUser() throws Exception {
        mockMvc.perform(
                delete("%s/%s".formatted(CONSULTANT_BASE_ENDPOINT,consultant.getUserId()))
                        .with(user(consultant.getUserPrincipal()))
        ).andExpect(status().isNoContent());
        mockMvc.perform(
                get("%s/%s".formatted(CONSULTANT_BASE_ENDPOINT,consultant.getUserId()))
                        .with(user(admin.getUserPrincipal()))
        ).andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404IfUserTobeDeletedDoesNotExist() throws Exception {
        mockMvc.perform(
                delete("%s/%s".formatted(CONSULTANT_BASE_ENDPOINT, UUID.randomUUID()))
                        .with(user(admin.getUserPrincipal()))
        ).andExpect(status().isNotFound());
    }

    @Test
    void shouldHandleOnboardingRequestAndReturnOk() throws Exception {

        var consultantInOnboardingStage = new Consultant(FAKER.internet().emailAddress(),
                Gender.MALE,
                new UserCredential("password"), consultantRole);
        consultantInOnboardingStage.setUserStage(UserStage.ONBOARDING);
        consultantInOnboardingStage.getUserPrincipal().setEnabled(true);
        ConsultantProfile consultantProfile = ConsultantProfile.builder()
                .address(new Address(FAKER.address().city(), FAKER.address().state(), FAKER.address().country()))
                .dateOfBirth(FAKER.timeAndDate().birthday())
                .userName(new UserName(FAKER.name().firstName(), FAKER.name().lastName()))
                .consultant(consultantInOnboardingStage)
                .build();
        consultantInOnboardingStage.setProfile(consultantProfile);
        consultantRepository.save(consultantInOnboardingStage);

        var onboardingData = """
                {
                    "medicalSpecialization": "pediatrician"
                }
                """;

        mockMvc.perform(
                post("%s/%s/onboard".formatted(CONSULTANT_BASE_ENDPOINT, consultantInOnboardingStage.getUserId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(onboardingData)
                        .with(user(consultantInOnboardingStage.getUserPrincipal()))

        ).andExpect(status().isOk());
    }

    @Test
    void shouldGetConsultantProfileDetails() throws Exception {
        String response = mockMvc.perform(
                get("/api/v1/consultants/profiles/details")
                        .with(user(consultant.getUserPrincipal()))
        ).andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        log.info(response);
    }

    @Test
    void shouldModifyConsultantProfileDetails() throws Exception {
        String request = """
                {
                    "education": {
                        "year": "2003",
                        "degree": "Masters",
                        "institution": "Nnamdi Azikwe University"
                    },
                    "profile": {
                        "phoneNumber": "09046641978"
                    }
                }
                """;
        String response = mockMvc.perform(
                        patch("/api/v1/consultants/profiles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                                .with(user(consultant.getUserPrincipal()))
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        log.info(response);

    }

    private void populateConsultants() {
        Set<Consultant> consultants = new HashSet<>();
        IntStream.range(0,10)
                .forEach(i -> {
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
                            .locationOfInstitution(FAKER.location().work())
                            .title(FAKER.job().title())
                            .dateOfBirth(FAKER.timeAndDate().birthday())
                            .yearsOfExperience(3)
                            .userName(new UserName(FAKER.name().firstName(), FAKER.name().lastName()))
                            .address(new Address(FAKER.address().city(), FAKER.address().state(), FAKER.address().country()))
                            .build();
                    unSavedConsultant.setProfile(consultantProfile);
                    consultants.add(unSavedConsultant);
                });

        consultantRepository.saveAll(consultants);
    }


}
