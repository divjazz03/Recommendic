package com.divjazz.recommendic.notification;

import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.notification.app.dto.NotificationDTO;
import com.divjazz.recommendic.notification.app.enums.NotificationCategory;
import com.divjazz.recommendic.notification.app.model.AppNotification;
import com.divjazz.recommendic.notification.app.repository.NotificationRepository;
import com.divjazz.recommendic.notification.app.service.AppNotificationService;
import com.divjazz.recommendic.security.utils.AuthUtils;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AppAppNotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private AuthUtils authUtils;

    private static final Faker faker = new Faker();
    @InjectMocks
    private AppNotificationService appNotificationService;

    @Test
    void shouldCreateNotification() {
        var notificationDTO = new NotificationDTO(
                faker.text().text(12),
                faker.text().text(45),
                UUID.randomUUID().toString(),
                "0L",
                false,
                NotificationCategory.CONSULTATION,
                null
        );
        var result = appNotificationService.createNotification(notificationDTO);
        assertThat(result.header()).isEqualTo(notificationDTO.header());

        then(notificationRepository).should(times(1))
                .save(any(AppNotification.class));
    }

    @Test
    void shouldUpdateNotificationToSeen() {
        var notification = AppNotification.builder()
                .subjectId("subject id")
                .summary("Summary")
                .seen(false)
                .header("Header")
                .forUserId("User Id")
                .category(NotificationCategory.APPOINTMENT)
                .build();
        given(notificationRepository.findById(anyLong())).willReturn(Optional.of(notification));

        var result = appNotificationService.setNotificationToSeen(1L);
        assertThat(result.seen()).isTrue();

    }

    @Test
    void shouldThrowEntityNotFoundExceptionIfNotFound() {
        given(notificationRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> appNotificationService.setNotificationToSeen(1L));
    }
}
