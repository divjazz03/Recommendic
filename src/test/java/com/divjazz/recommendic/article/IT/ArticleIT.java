package com.divjazz.recommendic.article.IT;

import com.divjazz.recommendic.BaseIntegration;
import com.divjazz.recommendic.DataSeeder;
import com.divjazz.recommendic.article.enums.ArticleStatus;
import com.divjazz.recommendic.article.domain.ArticleTag;
import com.divjazz.recommendic.article.dto.ArticleDTO;
import com.divjazz.recommendic.article.model.Article;
import com.divjazz.recommendic.article.repository.ArticleRepository;
import com.divjazz.recommendic.global.Response;
import com.divjazz.recommendic.global.general.BeanConfig;
import com.divjazz.recommendic.security.config.WebSecurityConfig;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.MedicalCategoryEnum;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.*;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.ConsultantProfileRepository;
import com.divjazz.recommendic.user.repository.ConsultantRepository;
import com.divjazz.recommendic.user.repository.PatientProfileRepository;
import com.divjazz.recommendic.user.repository.PatientRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Slf4j
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@Import({WebSecurityConfig.class, BeanConfig.class})
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
    void setup() {
        Patient unSavedPatient = new Patient(
                faker.internet().emailAddress(),
                Gender.MALE,
                new UserCredential(faker.text().text(20))
        );
        unSavedPatient.getUserPrincipal().setEnabled(true);
        unSavedPatient.setMedicalCategories(new String[]{});
        unSavedPatient.setUserStage(UserStage.ACTIVE_USER);
        PatientProfile patientProfile = PatientProfile.builder()
                .address(new Address(faker.address().city(), faker.address().state(), faker.address().country()))
                .phoneNumber(faker.phoneNumber().phoneNumber())
                .userName(new UserName(faker.name().firstName(), faker.name().lastName()))
                .patient(unSavedPatient)
                .build();
        unSavedPatient.setPatientProfile(patientProfile);
        patient = patientRepository.save(unSavedPatient);

        Consultant unSavedconsultant = new Consultant(
                faker.internet().emailAddress(),
                Gender.MALE,
                new UserCredential(faker.text().text(20))
        );
        unSavedconsultant.getUserPrincipal().setEnabled(true);
        unSavedconsultant.setMedicalCategory(MedicalCategoryEnum.CARDIOLOGY);
        unSavedconsultant.setUserStage(UserStage.ACTIVE_USER);
        var unSavedconsultantProfile = ConsultantProfile.builder()
                .address(new Address(faker.address().city(), faker.address().state(), faker.address().country()))
                .phoneNumber(faker.phoneNumber().phoneNumber())
                .userName(new UserName(faker.name().firstName(), faker.name().lastName()))
                .locationOfInstitution(faker.location().work())
                .title(faker.job().title())
                .consultant(unSavedconsultant)
                .build();
        unSavedconsultant.setProfile(unSavedconsultantProfile);
        consultant = consultantRepository.save(unSavedconsultant);
    }

    @ParameterizedTest
    @MethodSource("validArticle")
    void shouldUploadArticleWithValidRequestAndReturn201IfUserIsConsultant(String validArticle) throws Exception {
        var response = mockMvc.perform(
                        post("/api/v1/articles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(validArticle)
                                .with(user(this.consultant.getUserPrincipal()))
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
                                .with(user(patient.getUserPrincipal()))
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
                                .with(user(this.consultant.getUserPrincipal()))
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
                                .with(user(this.consultant.getUserPrincipal()))
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
                        .with(user(this.patient.getUserPrincipal()))
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
                        .with(user(this.patient.getUserPrincipal()))

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
