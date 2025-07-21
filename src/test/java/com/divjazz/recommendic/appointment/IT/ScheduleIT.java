package com.divjazz.recommendic.appointment.IT;

import com.divjazz.recommendic.BaseIntegration;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.MedicalCategoryEnum;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.ConsultantProfile;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.ConsultantRepository;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Slf4j
public class ScheduleIT extends BaseIntegration {

    public static final String BASE_URL = "/api/v1/schedules";
    private static final Faker faker = new Faker();
    @Autowired
    private ConsultantRepository consultantRepository;
    @Autowired
    private MockMvc mockMvc;
    private Consultant consultant;

    private static Stream<Arguments> invalidCreateScheduleRequests() {
        return Stream.of(Arguments.of("""
                {
                    "name":"My schedule",
                    "startTime": "11:30",
                    "zoneOffset": "+01:00",
                    "channels": ["voice","in_person"],
                    "recurrenceRule": null,
                    "isRecurring": false,
                    "isActive": true
                }
                """), Arguments.of("""
                {
                    "name":"My schedule",
                    "startTime": "1130",
                    "endTime": "14:400",
                    "zoneOffset": "+01:00p",
                    "channels": ["voicee","in_pearson"],
                    "recurrenceRule": {},
                    "isRecurring": true,
                    "isActive": true
                }
                """), Arguments.of("""
                {
                    "name":"My schedule",
                    "startTime": "11:30",
                    "endTime": "14:00",
                    "zoneOffset": "+01:00",
                    "channels": ["voice","in_person"],
                    "recurrenceRule": {
                        "recurrenceFrequency": "one-of",
                        "weekDays": ["moday", "wednsday"],
                        "interval": 2,
                        "endDate": "2023-0s1-23"
                    },
                    "isRecurring": true,
                    "isActive": true
                }
                """), Arguments.of("""
                {
                    "name":"My schedule",
                    "startTime": "11:30",
                    "endTime": "14:00",
                    "zoneOffset": "+01:00",
                    "channels": ["voice","in_person"],
                    "recurrenceRule": {
                        "recurrenceFrequency": "weekly",
                        "weekDays": [],
                        "interval": 2,
                        "endDate": "2023-0s1-23"
                    },
                    "isRecurring": true,
                    "isActive": true
                }
                """));
    }

    private static Stream<Arguments> createScheduleRequests() {
        return Stream.of(
                Arguments.of("""
                        {
                            "name":"My schedule",
                            "startTime": "11:30",
                            "endTime": "14:00",
                            "zoneOffset": "+01:00",
                            "channels": ["voice","in_person"],
                            "recurrenceRule": null,
                            "isActive": true
                        }
                        """),
                Arguments.of("""
                        {
                            "name":"My schedule",
                            "startTime": "11:30",
                            "endTime": "14:00",
                            "zoneOffset": "+01:00",
                            "channels": ["voice","in_person"],
                            "recurrenceRule": {
                                "recurrenceFrequency": "one-off",
                                "weekDays": ["monday", "wednesday"],
                                "interval": 2,
                                "endDate": "2023-01-23"
                            },
                            "isRecurring": true,
                            "isActive": true
                        }
                        """)
        );
    }

    @BeforeEach
    void setup() {
        var unSavedconsultant = new Consultant(
                faker.internet().emailAddress(),
                Gender.MALE,
                new UserCredential("sjfskjvnksjfns"));
        unSavedconsultant.setCertified(true);
        unSavedconsultant.setUserStage(UserStage.ACTIVE_USER);
        unSavedconsultant.setMedicalCategory(MedicalCategoryEnum.CARDIOLOGY);

        var consultantProfile = ConsultantProfile.builder()
                .address(new Address(faker.address().city(), faker.address().state(), faker.address().country()))
                .phoneNumber(faker.phoneNumber().phoneNumber())
                .userName(new UserName(faker.name().firstName(), faker.name().lastName()))
                .locationOfInstitution(faker.location().work())
                .title(faker.job().title())
                .consultant(unSavedconsultant)
                .build();
        unSavedconsultant.setProfile(consultantProfile);
        this.consultant = consultantRepository.save(unSavedconsultant);
    }

    @ParameterizedTest
    @MethodSource("createScheduleRequests")
    void shouldCreateScheduleForCurrentUser(String createScheduleRequest) throws Exception {
        var result = mockMvc.perform(
                post(BASE_URL)
                        .with(user(consultant))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createScheduleRequest)
        ).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        log.info("Response {}", result);
    }

    @ParameterizedTest
    @MethodSource("invalidCreateScheduleRequests")
    void shouldNotCreateScheduleButReturn400(String invalidCreateScheduleRequest) throws Exception {
        var result = mockMvc.perform(
                post(BASE_URL)
                        .with(user(consultant))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidCreateScheduleRequest)
        ).andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
        log.info("Response {}", result);
    }
}
