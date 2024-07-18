package com.yap.young.dto;

import com.yap.young.exception.annotation.ValidAge;
import com.yap.young.exception.annotation.ValidGender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ChildDTO {

    @NotBlank(message = "Full name cannot be null or blank")
    private String fullName;

    @NotNull(message = "Gender cannot be null or blank")
    @ValidGender
    private String gender;

    @NotNull(message = "Date of Birth cannot be null")
    @ValidAge(tooYoungMessage = "Age must be at least 8 years old.", tooOldMessage = "Age must be 17 years old or younger.")
    private LocalDate dob;

    @NotNull(message = "Country Id cannot be null")
    private Integer countryId;

    @NotBlank(message = "Parent Id cannot be null or blank")
    private String parentId;

    @NotBlank(message = "Email cannot be null or blank")
    private String email;

    private String mobile;
}
