package com.odcloud.adapter.in.controller.group.find_group_account_list;

import com.odcloud.application.group.port.in.FindGroupAccountListUseCase;
import com.odcloud.application.group.service.find_group_account_list.FindGroupAccountListServiceResponse;
import com.odcloud.infrastructure.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class FindGroupAccountListController {

    private final FindGroupAccountListUseCase useCase;

    @GetMapping("/groups/{groupId}/accounts")
    ApiResponse<FindGroupAccountListResponse> findGroupAccountList(
        @PathVariable String groupId
    ) {
        FindGroupAccountListServiceResponse serviceResponse = useCase.findGroupAccountList(groupId);
        return ApiResponse.ok(FindGroupAccountListResponse.of(serviceResponse));
    }
}
