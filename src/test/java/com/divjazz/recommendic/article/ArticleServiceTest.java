package com.divjazz.recommendic.article;

import com.divjazz.recommendic.article.dto.ArticleSearchDTO;
import com.divjazz.recommendic.article.dto.ArticleUpload;
import com.divjazz.recommendic.article.repository.ArticleRepository;
import com.divjazz.recommendic.article.service.ArticleService;
import com.divjazz.recommendic.global.general.PageResponse;
import com.divjazz.recommendic.recommendation.service.RecommendationService;
import com.divjazz.recommendic.security.UserPrincipal;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.dto.UserDTO;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.ConsultantProfile;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.service.ConsultantService;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceTest {

    private static final Faker faker = new Faker(Locale.ENGLISH);
    private final Set<ArticleSearchDTO> searchDTO = Set.of(
            new ArticleSearchDTO(
                    1L,
                    faker.text().text(10),
                    faker.text().text(20),
                    faker.name().firstName(),
                    faker.name().lastName(),
                    LocalDate.of(2024, 12, 1).toString(),
                    new String[]{"tag", "tag"},
                    1.3F,
                    "",
                    100,
                    10,
                    1000,
                    10000
            ),
            new ArticleSearchDTO(
                    2L,
                    faker.text().text(10),
                    faker.text().text(20),
                    faker.name().firstName(),
                    faker.name().lastName(),
                    LocalDate.of(2023, 2, 28).toString(),
                    new String[]{"tag", "tag"},
                    1.6F,
                    "",
                    1000,
                    102,
                    100323,
                    1003232
            )
    );
    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private RecommendationService recommendationService;
    @Mock
    private AuthUtils authUtils;
    @Mock
    private ConsultantService consultantService;
    private Consultant consultantUser;
    private ConsultantProfile consultantProfile;
    @InjectMocks
    private ArticleService articleService;

    @BeforeEach
    void setup() {
        consultantUser = new Consultant(
                "Email",
                Gender.MALE,
                new UserCredential("password"),
                new Role("CONSULTANT", "")
        );
        consultantProfile = ConsultantProfile.builder()
                .consultant(consultantUser)
                .userName(new UserName("test_user2_firstname", "test_user2_firstname"))
                .phoneNumber("+234905593953")
                .address(new Address("test_user2_city", "test_user2_state", "test_user2_country"))
                .build();
    }

    @Test
    void givenValidArticleUploadRequestShouldUploadAndReturnArticle() {
        var consultant = new Consultant("email",
                Gender.MALE,
                new UserCredential("password"),
                new Role("ADMIN", "ROLE_ADMIN"));
        consultant.setProfile(ConsultantProfile.builder()
                .userName(new UserName("Test firstname", "Test lastname"))
                .build());

        given(consultantService.getReference(anyLong())).willReturn(consultant);

        ArticleUpload articleUpload = new ArticleUpload(
                "article title",
                "Subtitle for the article",
                new String[]{"child health"}, "lots of stuff"
        );
        var userDTO = new UserDTO(1,
                "",
                Gender.MALE,
                LocalDateTime.now(),
                UserType.CONSULTANT,
                UserStage.ONBOARDING,
                new UserPrincipal("",
                        new UserCredential("password"),
                        new Role("Admin", "")));
        given(authUtils.getCurrentUser()).willReturn(userDTO);

        var article = articleService.uploadArticle(articleUpload);
        assertThat(article.tags()).contains(articleUpload.tags());
        assertThat(article.title()).isEqualTo(articleUpload.title());
        assertThat(article.subtitle()).isEqualTo(articleUpload.subtitle());
        assertThat(article.content()).isEqualTo(articleUpload.content());
    }

    @Test
    void givenAQueryShouldReturnSearchResponseIfAnyArticleExistMatchingQuery() {
        given(articleRepository.queryArticle(anyString(), anyInt(), anyInt())).willReturn(searchDTO);

        var result = articleService.searchArticle("some query", Pageable.ofSize(10));

        assertThat(result.toList()).isNotEmpty();
    }

    @Test
    void givenEmptyOrBlankQueryShouldReturnTopArticles() {
        given(articleRepository.queryTopArticle(anyInt(), anyInt())).willReturn(searchDTO);

        var result = articleService.searchArticle("", Pageable.ofSize(10));

        assertThat(result.toList()).isNotEmpty();

    }

    @Test
    void givenAQueryShouldReturnAPageResponseIfArticlesExistMatchingQuery() {
        given(articleRepository.queryArticle(anyString(), anyInt(), anyInt())).willReturn(searchDTO);
        var result = articleService.searchPageArticle("some query", Pageable.ofSize(10));
        assertThat(result.getClass()).isAssignableTo(PageResponse.class);
    }
}
