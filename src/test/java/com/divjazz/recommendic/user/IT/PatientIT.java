package com.divjazz.recommendic.user.IT;

import com.divjazz.recommendic.user.dto.PatientDTO;
import com.divjazz.recommendic.user.dto.PatientInfoResponse;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class PatientIT {

    private static final Faker faker = new Faker();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JacksonTester<PatientInfoResponse> patientDTOJacksonTester;

    private Patient patient;

    @BeforeEach
    void setup() {
        patient = new Patient(
                new UserName("test_user1_firstname", "test_user1_firstname"),
                "test_user1@test.com",
                "+234905593953",
                Gender.FEMALE,
                new Address("test_user1_city", "test_user1_state", "test_user1_country"),
                new UserCredential("test_user1_password")
        );
    }

    @Test
    void shouldCreateUserWithValidRequestParameterAndReturn201Created() throws Exception {
        var jsonRequest = """
                {
                      "city": "Ibadan",
                      "country": "Nigeria",
                      "email": "divjazz9@gmail.com",
                      "firstName": "Divine",
                      "gender": "Male",
                      "lastName": "Maduka",
                      "password": "june12003dsd",
                      "phoneNumber": "+2347046641978",
                      "state": "Oyo"
                }
                """;

        var response = mockMvc.perform(
                post("/api/v1/patients")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated())
                .andReturn().getResponse();
        var patientInfoResponseResponse = patientDTOJacksonTester.parseObject(response.getContentAsString());
    }

}
