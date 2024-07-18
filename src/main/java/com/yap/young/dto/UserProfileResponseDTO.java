package com.yap.young.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileResponseDTO {

    private String userId;

    private String yapTag;

    private String profilePictureUrl;

    private String mobile;

    private String email;
}
