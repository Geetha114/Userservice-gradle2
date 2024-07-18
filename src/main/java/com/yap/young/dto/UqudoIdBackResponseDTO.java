package com.yap.young.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UqudoIdBackResponseDTO {

    private String documentNumber;

    private String nationality;

    private String sponsorName;
}
