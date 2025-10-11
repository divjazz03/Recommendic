package com.divjazz.recommendic.article;

import com.divjazz.recommendic.article.dto.ArticleSearchDTO;
import com.divjazz.recommendic.article.dto.ArticleUpload;
import com.divjazz.recommendic.article.repository.ArticleRepository;
import com.divjazz.recommendic.article.service.ArticleService;
import com.divjazz.recommendic.global.general.PageResponse;
import com.divjazz.recommendic.recommendation.service.RecommendationService;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.ConsultantProfile;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private RecommendationService recommendationService;
    @Mock
    private AuthUtils authUtils;

    private Consultant consultantUser;
    private ConsultantProfile consultantProfile;
    @InjectMocks
    private ArticleService articleService;

    private static final Faker faker = new Faker(Locale.ENGLISH);

    private final Set<ArticleSearchDTO> searchDTO =  Set.of(
            new ArticleSearchDTO(
                    1L,
                    faker.text().text(10),
                    faker.text().text(20),
                    faker.name().firstName(),
                    faker.name().lastName(),
                    LocalDate.of(2024,12,1).toString(),
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
                    LocalDate.of(2023,2,28).toString(),
                    new String[]{"tag", "tag"},
                    1.6F,
                    "",
                    1000,
                    102,
                    100323,
                    1003232
            )
    );

    @BeforeEach
    void setup() {
        consultantUser = new Consultant(
                "test_user2@test.com",
                Gender.MALE,
                new UserCredential("test_user2_password"),
                new Role(1L,"ROLE_TEST", "")
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
        ArticleUpload articleUpload = new ArticleUpload(
                "article title",
                "Subtitle for the article",
                new String[]{"child health"},"lots of stuff"
        );
        given(authUtils.getCurrentUser()).willReturn(consultantUser);

        var article = articleService.uploadArticle(articleUpload);
        assertThat(article.tags()).contains(articleUpload.tags());
        assertThat(article.title()).isEqualTo(articleUpload.title());
        assertThat(article.subtitle()).isEqualTo(articleUpload.subtitle());
        assertThat(article.content()).isEqualTo(articleUpload.content());
    }

    @Test
    void givenAQueryShouldReturnSearchResponseIfAnyArticleExistMatchingQuery() {
        given(articleRepository.queryArticle(anyString(),anyInt(),anyInt())).willReturn(searchDTO);

        var result = articleService.searchArticle("some query", Pageable.ofSize(10));

        assertThat(result.toList()).isNotEmpty();
    }
    @Test
    void givenEmptyOrBlankQueryShouldReturnTopArticles() {
        given(articleRepository.queryTopArticle(anyInt(),anyInt())).willReturn(searchDTO);

        var result = articleService.searchArticle("", Pageable.ofSize(10));

        assertThat(result.toList()).isNotEmpty();

    }

    @Test
    void givenAQueryShouldReturnAPageResponseIfArticlesExistMatchingQuery() {
        given(articleRepository.queryArticle(anyString(),anyInt(),anyInt())).willReturn(searchDTO);
        var result = articleService.searchPageArticle("some query", Pageable.ofSize(10));
        assertThat(result.getClass()).isAssignableTo(PageResponse.class);
    }
}
