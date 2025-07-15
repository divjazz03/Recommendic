package com.divjazz.recommendic.search;

import com.divjazz.recommendic.appointment.service.AppointmentService;
import com.divjazz.recommendic.article.service.ArticleService;
import com.divjazz.recommendic.consultation.service.ConsultationService;
import com.divjazz.recommendic.search.repository.SearchRepository;
import com.divjazz.recommendic.search.service.SearchService;
import com.divjazz.recommendic.security.exception.AuthenticationException;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.service.AdminService;
import com.divjazz.recommendic.user.service.ConsultantService;
import com.divjazz.recommendic.user.service.GeneralUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SearchServiceTest {

    @Mock
    private  SearchRepository searchRepository;
    @Mock
    private  ConsultantService consultantService;
    @Mock
    private  GeneralUserService userService;
    @Mock
    private  ConsultationService consultationService;
    @Mock
    private  AppointmentService appointmentService;
    @Mock
    private  ArticleService articleService;
    @Mock
    private  AdminService adminService;
    @Mock
    private  AuthUtils authUtils;
    @InjectMocks
    private SearchService searchService;

    @Test
    void shouldThrowAuthenticationExceptionIfNoUserInAuthUtils() {
        given(authUtils.getCurrentUser()).willReturn(null);
        assertThatExceptionOfType(AuthenticationException.class)
                .isThrownBy(() -> searchService.executeQueryForAuthorizedUsers("some query", "category"));
    }

    @Test
    void givenUserExistsButInvalidCategoryShouldThrowIllegalArgumentException() {
        var user = User.builder()
                .email("testemail@test.com")
                .userStage(UserStage.ONBOARDING)
                .userType(UserType.CONSULTANT)
                .gender(Gender.FEMALE)
                .role(Role.CONSULTANT)
                .enabled(false)
                .build();
        var invalidCategoryString = "apppoinntmemt";
        var query = "some query";
        given(authUtils.getCurrentUser()).willReturn(user);
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> searchService.executeQueryForAuthorizedUsers(query, invalidCategoryString));

    }
}
