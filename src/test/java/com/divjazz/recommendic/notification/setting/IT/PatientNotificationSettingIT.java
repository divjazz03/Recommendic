package com.divjazz.recommendic.notification.setting.IT;

import com.divjazz.recommendic.BaseIntegrationTest;
import com.divjazz.recommendic.notification.app.model.PatientNotificationSetting;
import com.divjazz.recommendic.notification.app.repository.PatientNotificationSettingRepository;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.model.Admin;
import com.divjazz.recommendic.user.model.MedicalCategoryEntity;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.PatientProfile;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.AdminRepository;
import com.divjazz.recommendic.user.repository.MedicalCategoryRepository;
import com.divjazz.recommendic.user.repository.PatientRepository;
import com.divjazz.recommendic.user.service.AdminService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;

@AutoConfigureMockMvc
@Slf4j
public class PatientNotificationSettingIT extends BaseIntegrationTest {

    public static final Faker FAKER = new Faker();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private MedicalCategoryRepository medicalCategoryRepository;
    @Autowired
    private RoleService roleService;
    private Patient patient;
    private Role patientRole;
    private Set<MedicalCategoryEntity> medicalCategory;
    @Autowired
    private PatientNotificationSettingRepository patientNotificationSettingRepository;

    @BeforeEach
    void setup() {
        patientRole = roleService.getRoleByName(PatientService.PATIENT_ROLE_NAME);
        medicalCategory = medicalCategoryRepository.findAllByNameIn(Set.of("cardiology","gynecology"));
        Patient unsavedPatient = new Patient(
                FAKER.internet().emailAddress(),
                Gender.MALE,
                new UserCredential(FAKER.text().text(20)),
                patientRole
        );
        unsavedPatient.getUserPrincipal().setEnabled(true);
        unsavedPatient.setUserStage(UserStage.ACTIVE_USER);

        PatientProfile patientProfile = PatientProfile.builder()
                .address(new Address(FAKER.address().city(), FAKER.address().state(), FAKER.address().country()))
                .dateOfBirth(FAKER.timeAndDate().birthday())
                .userName(new UserName(FAKER.name().firstName(), FAKER.name().lastName()))
                .patient(unsavedPatient)
                .build();

        unsavedPatient.setPatientProfile(patientProfile);
        patient = patientRepository.save(unsavedPatient);
        patient.addMedicalCategories(medicalCategory);
        patient = patientRepository.save(patient);

        PatientNotificationSetting patientNotificationSetting = new PatientNotificationSetting();
        patientNotificationSetting.setPatient(patient);
        patientNotificationSettingRepository.save(patientNotificationSetting);
    }

    @Test
    void shouldReturnNotificationSetting() throws Exception {

        var responseString =  mockMvc.perform(
                 get("/api/v1/notifications/settings")
                         .with(user(patient.getUserPrincipal()))
        ).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        log.info(responseString);
    }

    @Test
    void shouldUpdateNotificationSetting() throws Exception {
        var requestString = """
                {
                    "emailNotificationEnabled": false,
                    "type": "PATIENT"
                }
                """;

        var responseString = mockMvc.perform(
                patch("/api/v1/notifications/settings")
                        .with(user(patient.getUserPrincipal()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestString)
        ).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        log.info(responseString);
    }
}
