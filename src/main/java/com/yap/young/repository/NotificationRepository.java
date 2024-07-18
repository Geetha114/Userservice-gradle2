package com.yap.young.repository;

import com.yap.young.entity.VerificationCode;
import com.yap.young.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<VerificationCode, Long> {

    List<VerificationCode> findByChildIdAndIsUsedAndIsExpiredAndNotificationType(String userId, boolean isUsed, boolean isExpired, NotificationType notificationTypeById);

    Optional<VerificationCode> findByChildIdAndCodeAndNotificationType(String userId, Integer verificationCode, NotificationType notificationType);
}
