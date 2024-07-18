package com.yap.young.service.notification;

import com.yap.young.dto.enums.NotificationStatus;
import com.yap.young.entity.NotificationLog;
import com.yap.young.entity.NotificationType;
import com.yap.young.exception.ResourceNotFoundException;
import com.yap.young.repository.NotificationLogRepository;
import com.yap.young.repository.NotificationTypeRepository;
import com.yap.young.util.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class NotificationLogService {

    private final NotificationLogRepository notificationLogRepository;

    private final NotificationTypeRepository notificationTypeRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationLogService.class);

    public NotificationLogService(NotificationLogRepository notificationLogRepository, NotificationTypeRepository notificationTypeRepository) {
        this.notificationLogRepository = notificationLogRepository;
        this.notificationTypeRepository = notificationTypeRepository;
    }

    public NotificationLog createLogForNotification(String userId, String recipient, String body, String notificationType, NotificationStatus status, int code) {
        LOGGER.info("create Log For Notification for user id: {}", userId);
        Integer notificationTypeId = notificationType.equals(AppConstants.EMAIL) ? 1 : 2;
        NotificationLog notificationLog = NotificationLog.builder()
                .userId(userId)
                .recipient(recipient)
                .body(body)
                .code(code)
                .status(status.name())
                .notificationType(getNotificationTypeById(notificationTypeId))
                .build();
        LOGGER.info("Notification log created for the user id: {}", userId);
        return notificationLogRepository.save(notificationLog);
    }

    private NotificationType getNotificationTypeById(Integer notificationTypeId) {
        return notificationTypeRepository.findById(notificationTypeId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.ERROR_NOTIFICATION_TYPE_NOT_FOUND));
    }

    public void updateLogForNotification(NotificationLog notificationLog, NotificationStatus status, String exception) {
        LOGGER.info("update Log For Notification for user id: {}", notificationLog.getUserId());
        notificationLog.setStatus(status.name());
        notificationLog.setUpdatedAt(LocalDateTime.now());
        Optional.ofNullable(exception).ifPresent(error -> notificationLog.setErrorMessage(exception));
        LOGGER.info("Notification log updated for the user id: {}", notificationLog.getUserId());
        notificationLogRepository.save(notificationLog);
    }
}
