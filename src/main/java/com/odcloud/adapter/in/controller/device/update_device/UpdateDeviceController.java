package com.odcloud.adapter.in.controller.device.update_device;

import com.odcloud.application.port.in.UpdateDeviceUseCase;
import com.odcloud.application.service.update_device.UpdateDeviceServiceResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.response.ApiResponse;
import com.odcloud.resolver.LoginAccount;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class UpdateDeviceController {

    private final UpdateDeviceUseCase useCase;

    @PatchMapping("/devices")
    ApiResponse<UpdateDeviceResponse> updateDevice(
        @LoginAccount Account account,
        @Valid @RequestBody UpdateDeviceRequest request
    ) {
        UpdateDeviceServiceResponse serviceResponse = useCase.update(
            request.toCommand(account.getId())
        );
        return ApiResponse.ok(UpdateDeviceResponse.of(serviceResponse));
    }
}
