package com.divjazz.recommendic.notification;

import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.notification.dto.NotificationDTO;
import com.divjazz.recommendic.notification.enums.NotificationCategory;
import com.divjazz.recommendic.notification.model.Notification;
import com.divjazz.recommendic.notification.repository.NotificationRepository;
import com.divjazz.recommendic.notification.service.NotificationService;
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
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private AuthUtils authUtils;

    private static final Faker faker = new Faker();
    @InjectMocks
    private NotificationService notificationService;

    @Test
    void shouldCreateNotification() {
        var notificationDTO = new NotificationDTO(
                faker.text().text(12),
                faker.text().text(45),
                UUID.randomUUID().toString(),
                false,
                NotificationCategory.CONSULTATION_STARTED
        );
        var result = notificationService.createNotification(notificationDTO);
        assertThat(result.header()).isEqualTo(notificationDTO.header());

        then(notificationRepository).should(times(1))
                .save(any(Notification.class));
    }

    @Test
    void shouldUpdateNotificationToSeen() {
        var notification = new Notification(
                "header",
                "body",
                UUID.randomUUID().toString(),
                false,
                NotificationCategory.CONSULTATION_STARTED
        );
        given(notificationRepository.findById(anyLong())).willReturn(Optional.of(notification));

        var result = notificationService.setNotificationToSeen(1L);
        assertThat(result.seen()).isTrue();

    }

    @Test
    void shouldThrowEntityNotFoundExceptionIfNotFound() {
        given(notificationRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> notificationService.setNotificationToSeen(1L));
    }
}
