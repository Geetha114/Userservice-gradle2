package com.yap.young.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessTokenResponseDTO {

    private String accessToken;

    private Long expiresIn;

    private String refreshToken;

    private Long refreshTokenExpiredIn;

    private String tokenType;

    private String userId;
}
