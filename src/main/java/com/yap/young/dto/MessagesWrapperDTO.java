package com.yap.young.dto;

import lombok.Data;

import java.util.List;

@Data
public class MessagesWrapperDTO {

    private List<SMSMessageDTO> messages;
}
