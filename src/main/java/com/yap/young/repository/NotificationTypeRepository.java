package com.yap.young.repository;

import com.yap.young.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationTypeRepository extends JpaRepository<NotificationType, Integer> {
}
