package com.yap.young.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {

    @NotBlank(message = "User id cannot be null or blank")
    private String userId;

    @NotBlank(message = "Password cannot be null or blank")
    private String password;
}
