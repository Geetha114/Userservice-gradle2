package com.yap.young.dto;

import lombok.Data;
import java.util.List;

@Data
public class SMSMessageDTO {

    private List<DestinationDTO> destinations;
    private String from;
    private String text;
}
