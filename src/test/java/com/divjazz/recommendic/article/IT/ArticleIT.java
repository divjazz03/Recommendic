package com.divjazz.recommendic.article.IT;

import com.divjazz.recommendic.BaseIntegration;
import com.divjazz.recommendic.article.enums.ArticleStatus;
import com.divjazz.recommendic.article.domain.ArticleTag;
import com.divjazz.recommendic.article.dto.ArticleDTO;
import com.divjazz.recommendic.article.model.Article;
import com.divjazz.recommendic.article.repository.ArticleRepository;
import com.divjazz.recommendic.global.Response;
import com.divjazz.recommendic.security.config.WebSecurityConfig;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.MedicalCategoryEnum;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.ConsultantRepository;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@Import({WebSecurityConfig.class})
public class ArticleIT extends BaseIntegration {

    private static final Faker faker = new Faker();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JacksonTester<Response<ArticleDTO>> articleJacksonTester;
    @Autowired
    private ConsultantRepository consultantRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private ArticleRepository articleRepository;

    private Consultant consultant;
    private Patient patient;

    static Stream<Arguments> validArticle() {
        String validArticle1 = """
                {
                    "title": "%s",
                    "subtitle": "%s",
                    "tags": %s,
                    "content": "%s"
                }
                """.formatted(faker.text().text(20),
                faker.text().text(30),
                Arrays.toString(new String[]{"\"child health\"", "\"cancer\"", "\"skin care\""}),
                faker.text().text(400)
        );
        String validArticle2 = """
                {
                    "title": "%s",
                    "subtitle": "%s",
                    "tags": %s,
                    "content": "%s"
                }
                """.formatted(faker.text().text(20),
                faker.text().text(30),
                Arrays.toString(new String[]{"\"child health\"", "\"cancer\"", "\"skin care\""}),
                faker.text().text(400)
        );

        return Stream.of(Arguments.of(validArticle1),
                Arguments.of(validArticle2));
    }

    static Stream<Arguments> inValidArticle() {
        String inValidArticle1 = """
                {
                    "title": "%s",
                    "subtitle": "%s",
                    "content": "%s"
                }
                """.formatted(faker.text().text(200),
                faker.text().text(30),
                faker.text().text(400)
        );
        String inValidArticle2 = """
                {
                    "subtitle": "%s",
                    "tags": %s,
                    "content": "%s"
                }
                """.formatted(
                faker.text().text(5),
                Arrays.toString(new String[]{"\"child health\"", "\"cancer\"", "\"skin care\""}),
                faker.text().text(400)
        );

        return Stream.of(Arguments.of(inValidArticle1),
                Arguments.of(inValidArticle2));
    }

    @BeforeEach
    void setupForEachTest() {
        consultant = new Consultant(
                new UserName(faker.name().firstName(), faker.name().lastName()),
                faker.internet().emailAddress(),
                faker.phoneNumber().phoneNumber(),
                Gender.MALE,
                new Address(faker.address().city(), faker.address().state(), faker.address().country()),
                new UserCredential(faker.text().text(20))
        );
        consultant.setEnabled(true);
        consultant.setMedicalCategory(MedicalCategoryEnum.CARDIOLOGY);
        consultant.setUserStage(UserStage.ACTIVE_USER);
        consultant.setLocationOfInstitution(faker.location().work());
        consultant.setTitle(faker.job().title());
        consultant = consultantRepository.save(consultant);

        patient = new Patient(
                new UserName(faker.name().firstName(), faker.name().lastName()),
                faker.internet().emailAddress(),
                faker.phoneNumber().phoneNumber(),
                Gender.MALE,
                new Address(faker.address().city(), faker.address().state(), faker.address().country()),
                new UserCredential(faker.text().text(20))
        );
        patient.setEnabled(true);
        patient.setMedicalCategories(new String[]{});
        patient.setUserStage(UserStage.ACTIVE_USER);

        patient = patientRepository.save(patient);

    }

    @ParameterizedTest
    @MethodSource("validArticle")
    void shouldUploadArticleWithValidRequestAndReturn201IfUserIsConsultant(String validArticle) throws Exception {
        var response = mockMvc.perform(
                        post("/api/v1/articles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(validArticle)
                                .with(user(consultant))
                )
                .andExpect(status().isCreated())
                .andReturn().getResponse();
        var article = articleJacksonTester.parseObject(response.getContentAsString());
        assertThat(article.data()).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("validArticle")
    void shouldNotUploadArticleWithValidRequestAndReturn403IfNotConsultant(String validArticle) throws Exception {

        mockMvc.perform(
                        post("/api/v1/articles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(validArticle)
                                .with(user(patient))
                )
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @MethodSource("inValidArticle")
    void shouldNotUploadArticleWithInvalidRequestAndReturn400(String invalidArticle) throws Exception {
        var response = mockMvc.perform(
                        post("/api/v1/articles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidArticle)
                                .with(user(consultant))
                )
                .andExpect(status().isBadRequest())
                .andReturn().getResponse();

        log.info("Test complete with response {}", response.getContentAsString());
    }

    @Test
    void shouldGetPagedArticleThatWasWrittenByConsultantAndReturn200() throws Exception {
        populateArticles();

        var response = mockMvc.perform(
                        get("/api/v1/articles")
                                .with(user(consultant))
                )
                .andExpect(status().isOk())
                .andReturn().getResponse();
        log.info("Test complete with response {}", response.getContentAsString());

    }
    @Test
    void shouldGetArticlesForPatientUserAndReturn200() throws Exception {
        populateArticles();

        var response = mockMvc.perform(
                get("/api/v1/articles")
                        .with(user(patient))
        )
                .andExpect(status().isOk())
                .andReturn().getResponse();
        log.info("Test complete with response {}", response.getContentAsString());
    }

    @Test
    void shouldGetArticleByIDIfExistsAndReturn200() throws Exception {
        var articlePopulated = articleRepository.save(
                Article.builder()
                        .title(faker.text().text(20))
                        .subtitle(faker.text().text(30))
                        .status(ArticleStatus.PUBLISHED)
                        .published_at(LocalDateTime.now())
                        .consultant(consultant)
                        .content(faker.text().text(200))
                        .numberOfReads(5004038L)
                        .tags(new String[]{ArticleTag.ORAL.getValue()})
                        .build()
        );

        var response = mockMvc.perform(
                get("/api/v1/articles/%s".formatted(articlePopulated.getId()))
                        .with(user(patient))

        )
                .andExpect(status().isOk())
                .andReturn().getResponse();
        var articleResponse = articleJacksonTester.parseObject(response.getContentAsString());
        assertThat(articleResponse.data().content()).isEqualTo(articlePopulated.getContent());
    }

    private void populateArticles() {
        articleRepository.saveAll(Set.of(
                Article.builder()
                        .title(faker.text().text(20))
                        .subtitle(faker.text().text(30))
                        .status(ArticleStatus.PUBLISHED)
                        .published_at(LocalDateTime.now())
                        .consultant(consultant)
                        .content(faker.text().text(200))
                        .numberOfReads(500L)
                        .tags(new String[]{ArticleTag.ORAL.getValue()})
                        .build(),
                Article.builder()
                        .title(faker.text().text(20))
                        .subtitle(faker.text().text(30))
                        .status(ArticleStatus.DRAFT)
                        .published_at(LocalDateTime.now())
                        .consultant(consultant)
                        .content(faker.text().text(200))
                        .numberOfReads(1200L)
                        .tags(new String[]{ArticleTag.MENTAL.getValue()})
                        .build(),
                Article.builder()
                        .title(faker.text().text(20))
                        .subtitle(faker.text().text(30))
                        .status(ArticleStatus.PUBLISHED)
                        .published_at(LocalDateTime.now())
                        .consultant(consultant)
                        .content(faker.text().text(200))
                        .numberOfReads(560046L)
                        .tags(new String[]{ArticleTag.ORAL.getValue()})
                        .build()
        ));
    }
}
