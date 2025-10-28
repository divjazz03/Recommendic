package com.divjazz.recommendic.notification.setting.IT;

import com.divjazz.recommendic.BaseIntegrationTest;
import com.divjazz.recommendic.notification.app.model.ConsultantNotificationSetting;
import com.divjazz.recommendic.notification.app.repository.ConsultantNotificationSettingRepository;
import com.divjazz.recommendic.notification.app.repository.PatientNotificationSettingRepository;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.MedicalCategoryEntity;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.certification.ConsultantEducation;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.ConsultantProfile;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.ConsultantRepository;
import com.divjazz.recommendic.user.repository.MedicalCategoryRepository;
import com.divjazz.recommendic.user.repository.PatientRepository;
import com.divjazz.recommendic.user.repository.certificationRepo.ConsultantEducationRepository;
import com.divjazz.recommendic.user.service.ConsultantService;
import com.divjazz.recommendic.user.service.MedicalCategoryService;
import com.divjazz.recommendic.user.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Slf4j
public class ConsultantNotificationSettingIT extends BaseIntegrationTest {

    public static final Faker FAKER = new Faker();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ConsultantRepository consultantRepository;
    @Autowired
    private ConsultantEducationRepository consultantEducationRepository;
    @Autowired
    private MedicalCategoryService medicalCategoryService;
    @Autowired
    private RoleService roleService;
    private Consultant consultant;
    private Role consultantRole;
    private MedicalCategoryEntity medicalCategory;
    @Autowired
    private ConsultantNotificationSettingRepository notificationSettingRepository;
    @Autowired
    private ConsultantNotificationSettingRepository consultantNotificationSettingRepository;

    @BeforeEach
    void setup() {
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
        consultant = consultantRepository.saveAndFlush(unSavedConsultant);
        ConsultantEducation consultantEducation = new ConsultantEducation(
                consultant,
                FAKER.university().degree(),
                FAKER.university().name(),
                2004);

        consultantEducationRepository.save(consultantEducation);

        var consultantNotificationSetting = new ConsultantNotificationSetting();
        consultantNotificationSetting.setConsultant(consultant);
        consultantNotificationSettingRepository.save(consultantNotificationSetting);

    }

    @Test
    void shouldReturnNotificationSetting() throws Exception {
        var responseString =  mockMvc.perform(
                        get("/api/v1/notifications/settings")
                                .with(user(consultant.getUserPrincipal()))
                ).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        log.info(responseString);
    }

    @Test
    void shouldUpdateNotificationSetting() throws Exception {
        var requestString = """
                {
                    "emailNotificationEnabled": false,
                    "type": "CONSULTANT"
                }
                """;

        var responseString = mockMvc.perform(
                        patch("/api/v1/notifications/settings")
                                .with(user(consultant.getUserPrincipal()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestString)
                ).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        log.info(responseString);
    }


}
