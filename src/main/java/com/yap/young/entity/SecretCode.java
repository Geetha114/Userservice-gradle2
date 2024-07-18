package com.yap.young.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "secret_code")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecretCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "secret_code_id")
    private Long secretCodeId;

    @Column(name = "code", nullable = false)
    private Integer code;

    @Column(name = "is_expired", nullable = false)
    private Boolean isExpired = false;

    @Column(name = "is_used", nullable = false)
    private Boolean isUsed = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "parent_id", nullable = false)
    private String parentId;

    @Column(name = "child_id", nullable = false)
    private String childId;
}