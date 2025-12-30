package com.odcloud.adapter.in.controller.device.register_device;

import com.odcloud.application.port.in.RegisterDeviceUseCase;
import com.odcloud.application.service.register_device.RegisterDeviceServiceResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.response.ApiResponse;
import com.odcloud.resolver.LoginAccount;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class RegisterDeviceController {

    private final RegisterDeviceUseCase useCase;

    @PostMapping("/devices")
    ApiResponse<RegisterDeviceResponse> registerDevice(
        @RequestBody @Valid RegisterDeviceRequest request,
        @LoginAccount Account account
    ) {
        RegisterDeviceServiceResponse serviceResponse = useCase.register(
            request.toCommand(account.getId())
        );
        return ApiResponse.ok(RegisterDeviceResponse.of(serviceResponse));
    }
}
