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
import com.divjazz.recommendic.user.model.userAttributes.*;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.AdminRepository;
import com.divjazz.recommendic.user.repository.ConsultantRepository;
import com.divjazz.recommendic.user.repository.RoleRepository;
import com.divjazz.recommendic.user.service.AdminService;
import com.divjazz.recommendic.user.service.ConsultantService;
import com.divjazz.recommendic.user.service.MedicalCategoryService;
import com.divjazz.recommendic.user.service.RoleService;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class ConsultantUserOnboardingIT extends BaseIntegrationTest {

    private static final Faker FAKER = new Faker();
    public static final String CONSULTANT_BASE_ENDPOINT = "/api/v1/consultants";
    @Autowired
    public MockMvc mockMvc;
    @Autowired
    public ConsultantRepository consultantRepository;
    public Role consultantRole;
    @Autowired
    public RoleService roleService;

    @BeforeEach
    void setup() {
        consultantRole = roleService.getRoleByName(ConsultantService.CONSULTANT_ROLE_NAME);
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
                    "availableDays":["Thursday"],
                    "bio":"ewewww",
                    "certifications":"ewew",
                    "consultationDuration":60,
                    "consultationFee":15000,
                    "currentWorkplace":"fdeeff ffdfdfd",
                    "graduationYear":2012,
                    "languages":["Yoruba"],
                    "licenseNumber":"dsdsdsdsds",
                    "medicalDegree":"hthrthth",
                    "preferredTimeSlots":["Afternoon (12PM - 4PM)"],
                    "specialization":"dermatology",
                    "university":"fwefwefwf",
                    "yearsOfExperience":9,
                    "resume":{
                        "fileUrl":"https://res.cloudinary.com/dmg3bwlhh/image/upload/v1764935050/users/CST-019ad953-58d5-7d0d-9ced-be524b53a636/users/CST-019ad953-58d5-7d0d-9ced-be524b53a636/profile_a620e257-6f6c-4b3e-b94a-a47085e6b370.png",
                        "name":"unizik.png",
                        "type":"resume"},
                    "credentials":[
                        {"fileUrl":"https://res.cloudinary.com/dmg3bwlhh/image/upload/v1764935051/users/CST-019ad953-58d5-7d0d-9ced-be524b53a636/users/CST-019ad953-58d5-7d0d-9ced-be524b53a636/profile_cc98f360-8847-41b5-a736-eccd718290c0.pdf",
                            "name":"MyStatement.pdf",
                            "type":"certificate"
                         }
                    ],
                    "profilePictureUrl":"https://res.cloudinary.com/dmg3bwlhh/image/upload/v1764935050/users/CST-019ad953-58d5-7d0d-9ced-be524b53a636/users/CST-019ad953-58d5-7d0d-9ced-be524b53a636/profile_f84d5769-97ab-4f21-b1c3-43acc4417c61.png"
                }
               
                """;

        mockMvc.perform(
                post("%s/%s/onboard".formatted(CONSULTANT_BASE_ENDPOINT, consultantInOnboardingStage.getUserId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(onboardingData)
                        .with(user(consultantInOnboardingStage.getUserPrincipal()))

        ).andExpect(status().isOk());
    }

}
