package com.divjazz.recommendic.user.IT;

import com.divjazz.recommendic.BaseIntegrationTest;
import com.divjazz.recommendic.security.UserPrincipal;
import com.divjazz.recommendic.user.controller.UserController;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class UserIT extends BaseIntegrationTest {
    private static final Faker faker = new Faker();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JacksonTester<UserController.CurrentUser> currentUserJacksonTester;

    private User user;

    @BeforeEach
    void setup() {
        user = User.builder()
                .userPrincipal(UserPrincipal.builder()
                        .role(Role.CONSULTANT)
                        .enabled(true)
                        .userCredential(new UserCredential(faker.text().text(23)))
                        .build())
                .userId(UUID.randomUUID().toString())
                .userType(UserType.CONSULTANT)
                .gender(Gender.MALE)
                .build();
    }


    @Test
    void shouldReturnCurrentAuthenticatedUser() throws Exception {
        var response = mockMvc.perform(
                get("/api/v1/users")
                        .with(user(user.getUserPrincipal()))
        ).andExpect(status().isOk())
                .andReturn().getResponse();
        var currentUser = currentUserJacksonTester.parseObject(response.getContentAsString());
        assertThat(currentUser).isNotNull();
    }

    @Test
    void shouldNotReturnCurrentUserButReturn401UnAuthorizedWithErrorResponse() throws Exception {
        var response = mockMvc.perform(
                get("/api/v1/users")
        ).andExpect(status().isUnauthorized())
                .andReturn().getResponse();
        log.info("Response is {}", response.getContentAsString());
    }
}
