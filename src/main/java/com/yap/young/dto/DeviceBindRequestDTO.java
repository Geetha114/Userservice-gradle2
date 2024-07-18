package com.yap.young.dto;

import com.yap.young.exception.annotation.ValidOSVersion;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeviceBindRequestDTO {

    @NotBlank(message = "Device id cannot be null or blank")
    private String deviceId;

    @NotBlank(message = "OS version cannot be null or blank")
    @ValidOSVersion
    private String osVersion;

    @NotBlank(message = "location cannot be null or blank")
    private String location;
}
