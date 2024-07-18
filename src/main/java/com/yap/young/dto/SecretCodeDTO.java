package com.yap.young.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SecretCodeDTO {

    @NotBlank(message = "Parent Id cannot be null or blank")
    private String parentId;

    @NotBlank(message = "Child Id cannot be null or blank")
    private String childId;
}