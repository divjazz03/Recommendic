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
    private ConsultantProfileRepository consultantProfileRepository;
    @Autowired
    private PatientProfileRepository patientProfileRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .findAndRegisterModules()
                .configure(SerializationFeature.CLOSE_CLOSEABLE, true)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

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
    private long savePatientAndProfile(Patient patient, PatientProfile patientProfile) throws com.fasterxml.jackson.core.JsonProcessingException {
        String savePatientSQL = """
                INSERT INTO patient (user_id, email, user_type, user_stage, gender, role,created_by, updated_by, medical_categories,user_credential)
                VALUES (?,?,?,?,?,?,?,?,?,?) RETURNING id;
                """;
        String savePatientProfile = """
                INSERT INTO patient_profiles (id, address, phone_number, username, updated_at, created_at, created_by, updated_by)
                VALUES (?,?,?,?,?,?,?,?) RETURNING id;
                """;
        long id =  jdbcTemplate.query(savePatientSQL,
                (rs,rsnex) -> rs.getLong("id"),
                UUID.randomUUID().toString(),
                patient.getEmail(),
                patient.getUserType().name(),
                patient.getUserStage().name(),
                patient.getGender().toString(),
                patient.getRole().getName(),
                "SYSTEM",
                "SYSTEM",
                new String[]{MedicalCategoryEnum.CARDIOLOGY.getValue()},
                DataSeeder.asJsonb(objectMapper.writeValueAsString(new UserCredential("random password")))
                ).get(0);
        return jdbcTemplate.query(savePatientProfile,
                (rs, rowNum) -> rs.getLong("id"),
                id,
                DataSeeder.asJsonb(objectMapper.writeValueAsString(patientProfile.getAddress())),
                patientProfile.getPhoneNumber(),
                DataSeeder.asJsonb(objectMapper.writeValueAsString(patientProfile.getUserName())),
                patientProfile.getUpdatedAt(),
                LocalDateTime.now(),
                "SYSTEM",
                "SYSTEM"
        ).get(0);
    }
    private long saveConsultantAndProfile(Consultant consultant, ConsultantProfile consultantProfile) throws com.fasterxml.jackson.core.JsonProcessingException {
        String saveConsultantSQL = """
                INSERT INTO consultant (user_id, email, user_type, user_stage, gender, role,created_by, updated_by, specialization,user_credential)
                VALUES (?,?,?,?,?,?,?,?,?,?) RETURNING id;
                """;
        String saveConsultantProfile = """
                INSERT INTO consultant_profiles (id, address, phone_number, username, updated_at, created_at, created_by, updated_by)
                VALUES (?,?,?,?,?,?,?,?) RETURNING id;
                """;
        long id =  jdbcTemplate.query(saveConsultantSQL,
                (rs, rsN) -> rs.getLong("id"),
                UUID.randomUUID().toString(),
                consultant.getEmail(),
                consultant.getUserType().name(),
                consultant.getUserStage().name(),
                consultant.getGender().toString(),
                consultant.getRole().getName(),
                "SYSTEM",
                "SYSTEM",
                MedicalCategoryEnum.CARDIOLOGY.getValue(),
                DataSeeder.asJsonb(objectMapper.writeValueAsString(new UserCredential("random password")))
        ).get(0);
        return jdbcTemplate.query(saveConsultantProfile,
                (rs, rsN) -> rs.getLong("id"),
                id,
                DataSeeder.asJsonb(objectMapper.writeValueAsString(consultantProfile.getAddress())),
                consultantProfile.getPhoneNumber(),
                DataSeeder.asJsonb(objectMapper.writeValueAsString(consultantProfile.getUserName())),
                consultant.getUpdatedAt(),
                LocalDateTime.now(),
                "SYSTEM",
                "SYSTEM"
        ).get(0);
    }

    @BeforeEach
    void setup() {
        Patient unSavedPatient = new Patient(
                faker.internet().emailAddress(),
                Gender.MALE,
                new UserCredential(faker.text().text(20))
        );
        unSavedPatient.setEnabled(true);
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
        unSavedconsultant.setEnabled(true);
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
                                .with(user(this.consultant))
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
                                .with(user(this.consultant))
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
                                .with(user(this.consultant))
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
                        .with(user(this.patient))
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
                        .with(user(this.patient))

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
