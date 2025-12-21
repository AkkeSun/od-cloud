package com.odcloud.adapter.in.controller.delete_device;

import com.odcloud.application.port.in.DeleteDeviceUseCase;
import com.odcloud.application.port.in.command.DeleteDeviceCommand;
import com.odcloud.application.service.delete_device.DeleteDeviceServiceResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.response.ApiResponse;
import com.odcloud.resolver.LoginAccount;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class DeleteDeviceController {

    private final DeleteDeviceUseCase useCase;

    // patch fcm token reset
    @DeleteMapping("/devices")
    ApiResponse<DeleteDeviceResponse> deleteDevice(
        @LoginAccount Account account,
        @Valid @RequestBody DeleteDeviceRequest request
    ) {
        DeleteDeviceCommand command = DeleteDeviceCommand.builder()
            .accountId(account.getId())
            .osType(request.osType())
            .deviceId(request.deviceId())
            .build();

        DeleteDeviceServiceResponse serviceResponse = useCase.delete(command);
        return ApiResponse.ok(DeleteDeviceResponse.of(serviceResponse));
    }
}
