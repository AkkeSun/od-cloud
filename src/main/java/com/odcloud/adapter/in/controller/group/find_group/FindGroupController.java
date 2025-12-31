package com.odcloud.adapter.in.controller.group.find_group;

import com.odcloud.application.group.port.in.FindGroupUseCase;
import com.odcloud.application.group.service.find_group.FindGroupServiceResponse;
import com.odcloud.infrastructure.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class FindGroupController {

    private final FindGroupUseCase useCase;

    @GetMapping("/groups/{groupId}")
    ApiResponse<FindGroupResponse> findById(@PathVariable String groupId) {
        FindGroupServiceResponse serviceResponse = useCase.findById(groupId);
        return ApiResponse.ok(FindGroupResponse.of(serviceResponse));
    }
}
