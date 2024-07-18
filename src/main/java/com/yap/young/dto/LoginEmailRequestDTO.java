package com.yap.young.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginEmailRequestDTO {

    @NotBlank(message = "Email id cannot be null or blank")
    private String emailId;

    @NotBlank(message = "Password cannot be null or blank")
    private String password;
}
