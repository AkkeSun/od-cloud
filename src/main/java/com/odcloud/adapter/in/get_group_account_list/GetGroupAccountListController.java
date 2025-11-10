package com.odcloud.adapter.in.get_group_account_list;

import com.odcloud.application.port.in.GetGroupAccountListUseCase;
import com.odcloud.application.port.in.query.GetGroupAccountListQuery;
import com.odcloud.application.service.get_group_account_list.GetGroupAccountListServiceResponse;
import com.odcloud.infrastructure.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class GetGroupAccountListController {

    private final GetGroupAccountListUseCase useCase;

    @GetMapping("/groups/{groupId}/accounts")
    ApiResponse<GetGroupAccountListResponse> getGroupAccountList(
        @PathVariable String groupId
    ) {
        GetGroupAccountListQuery query = GetGroupAccountListQuery.builder()
            .groupId(groupId)
            .build();
        GetGroupAccountListServiceResponse serviceResponse = useCase.getGroupAccountList(query);
        return ApiResponse.ok(GetGroupAccountListResponse.of(serviceResponse));
    }
}
