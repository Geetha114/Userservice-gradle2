package com.yap.young.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_profile")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "card_color", nullable = false)
    private String cardColor;

    @Column(name = "card_display_name", nullable = false)
    private String cardDisplayName;

    @Column(name = "yap_tag", nullable = false)
    private String yapTag;

    @Column(name = "child_id", nullable = false)
    private String childId;
}
