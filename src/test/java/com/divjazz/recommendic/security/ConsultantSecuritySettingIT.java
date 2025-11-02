package com.divjazz.recommendic.security;

import com.divjazz.recommendic.BaseIntegrationTest;
import com.divjazz.recommendic.global.Response;
import com.divjazz.recommendic.global.exception.GlobalControllerExceptionAdvice;
import com.divjazz.recommendic.global.general.PageResponse;
import com.divjazz.recommendic.security.service.SecurityService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Slf4j
public class ConsultantSecuritySettingIT extends BaseIntegrationTest {

    private static final Faker FAKER = new Faker();
    public static final String SECURITY_BASE_URL = "/api/v1/security";
    @Autowired
    public MockMvc mockMvc;
    @Autowired
    public ConsultantRepository consultantRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private MedicalCategoryService medicalCategoryService;

    public Consultant consultant;
    public Role consultantRole;
    public MedicalCategoryEntity medicalCategory;
    @Autowired
    private ConsultantEducationRepository consultantEducationRepository;
    @Autowired
    private SecurityService securityService;

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
        ConsultantEducation consultantEducation = new ConsultantEducation(
                consultant,
                FAKER.university().degree(),
                FAKER.university().name(),
                2004);

        consultantEducationRepository.save(consultantEducation);
       securityService.createUserSetting(consultant);
    }

    @Test
    void shouldGetUserSecuritySetting() throws Exception {
        var responseString = mockMvc.perform(
                        get(SECURITY_BASE_URL)
                                .with(user(consultant.getUserPrincipal()))
                                .with(baseAuthenticatedSession(consultant.getUserPrincipal()))
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        log.info(responseString);
    }

    @Test
    void shouldUpdateSecuritySetting() throws Exception {
        var request = """
                {
                    "sessionTimeoutMin": 60
                }
                """;

        var responseString = mockMvc.perform(
                patch(SECURITY_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                        .with(user(consultant.getUserPrincipal()))
                        .with(baseAuthenticatedSession(consultant.getUserPrincipal()))
        ).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        log.info(responseString);
    }

}
