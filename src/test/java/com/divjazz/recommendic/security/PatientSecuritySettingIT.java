package com.divjazz.recommendic.security;

import com.divjazz.recommendic.BaseIntegrationTest;
import com.divjazz.recommendic.security.service.SecurityService;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.model.Admin;
import com.divjazz.recommendic.user.model.MedicalCategoryEntity;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.*;
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
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import java.util.Set;


@AutoConfigureMockMvc
@Slf4j
public class PatientSecuritySettingIT extends BaseIntegrationTest {
    private static final Faker FAKER = new Faker();
    public static final String SECURITY_BASE_URL = "/api/v1/security";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private MedicalCategoryRepository medicalCategoryRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private SecurityService securityService;
    private Patient patient;
    private Role patientRole;
    private Set<MedicalCategoryEntity> medicalCategory;

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
        securityService.createUserSetting(patient);

    }

    @Test
    void shouldGetUserSecuritySetting() throws Exception {
        var responseString = mockMvc.perform(
                get(SECURITY_BASE_URL)
                        .with(user(patient.getUserPrincipal()))
                        .with(baseAuthenticatedSession(patient.getUserPrincipal()))
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        log.info(responseString);
    }
}
