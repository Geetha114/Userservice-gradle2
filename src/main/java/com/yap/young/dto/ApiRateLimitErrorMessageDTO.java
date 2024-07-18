package com.yap.young.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApiRateLimitErrorMessageDTO {

    private final UUID id = UUID.randomUUID();

    private final int status;

    private final String error;

    private final String message;

    private final LocalDateTime timestamp = LocalDateTime.now();

    private final String path;

}
