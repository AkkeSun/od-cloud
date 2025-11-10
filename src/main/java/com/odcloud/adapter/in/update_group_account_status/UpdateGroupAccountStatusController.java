package com.odcloud.adapter.in.update_group_account_status;

import com.odcloud.application.port.in.UpdateGroupAccountStatusUseCase;
import com.odcloud.application.service.update_group_account_status.UpdateGroupAccountStatusServiceResponse;
import com.odcloud.infrastructure.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class UpdateGroupAccountStatusController {

    private final UpdateGroupAccountStatusUseCase useCase;

    @PatchMapping("/groups/{groupId}/accounts/{accountId}/status")
    ApiResponse<UpdateGroupAccountStatusResponse> updateStatus(
        @PathVariable String groupId,
        @PathVariable Long accountId,
        @RequestBody @Valid UpdateGroupAccountStatusRequest request
    ) {
        UpdateGroupAccountStatusServiceResponse serviceResponse = useCase.updateStatus(groupId,
            accountId, request.status());
        return ApiResponse.ok(UpdateGroupAccountStatusResponse.of(serviceResponse));
    }
}
