package com.yap.young.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VerificationRequestDTO {

    @NotBlank(message = "User Id cannot be null or blank")
    private String userId;

    @NotNull(message = "Verification code cannot be null")
    private Integer verificationCode;

    private Boolean isRegistered;
}
