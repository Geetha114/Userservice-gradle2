package com.yap.young.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UqudoIdResponseDTO {

    private UqudoIdFrontResponseDTO front;

    private UqudoIdBackResponseDTO back;
}
