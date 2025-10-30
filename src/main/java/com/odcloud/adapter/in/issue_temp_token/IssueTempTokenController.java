package com.odcloud.adapter.in.issue_temp_token;

import com.odcloud.application.port.in.IssueTempTokenUseCase;
import com.odcloud.application.service.issue_temp_token.IssueTempTokenServiceResponse;
import com.odcloud.infrastructure.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class IssueTempTokenController {

    private final IssueTempTokenUseCase useCase;

    @PostMapping("/auth")
    ApiResponse<IssueTempTokenResponse> issue(
        @RequestBody @Valid IssueTempTokenRequest request
    ) {
        IssueTempTokenServiceResponse serviceResponse = useCase.issue(request.toCommand());

        return ApiResponse.ok(IssueTempTokenResponse.of(serviceResponse));
    }
}
