package com.yap.young.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UqudoIdFrontResponseDTO {

    private String fullName;

    private LocalDate dateOfBirthFormatted;

    private String identityNumber;

    private String nationality;

    private char gender;
}
