package com.odcloud.adapter.in.controller.delete_device;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
record DeleteDeviceRequest(
    @NotBlank(message = "osType은 필수값 입니다")
    String osType,

    @NotBlank(message = "deviceId는 필수값 입니다")
    String deviceId
) {

}
