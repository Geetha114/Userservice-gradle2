package com.yap.young.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UqudoAuthResponse {

    private String access_token;

    private String scope;

    private String token_type;

    private Integer expires_in;

    private String jti;
}
