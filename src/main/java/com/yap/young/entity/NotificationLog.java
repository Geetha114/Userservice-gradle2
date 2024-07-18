package com.yap.young.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_log")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_log_id")
    private Long notificationLogId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "recipient", nullable = false)
    private String recipient;

    @Column(name = "body", length = 5000, nullable = false)
    private String body;

    @Column(name = "code", nullable = false)
    private Integer code;

    @Column(name = "created_at", nullable = false)
    private final LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "notification_type_id")
    private NotificationType notificationType;
}
