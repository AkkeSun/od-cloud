package com.odcloud.adapter.in.register_group;

import com.odcloud.application.port.in.RegisterGroupUseCase;
import com.odcloud.application.service.register_group.RegisterGroupServiceResponse;
import com.odcloud.infrastructure.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class RegisterGroupController {

    private final RegisterGroupUseCase useCase;

    @PostMapping("/accounts/groups")
    ApiResponse<RegisterGroupResponse> registerGroup(
        @RequestBody @Valid RegisterGroupRequest request
    ) {
        RegisterGroupServiceResponse serviceResponse = useCase.register(request.toCommand());

        return ApiResponse.ok(RegisterGroupResponse.of(serviceResponse));
    }
}
