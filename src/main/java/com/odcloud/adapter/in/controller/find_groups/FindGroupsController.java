package com.odcloud.adapter.in.controller.find_groups;

import com.odcloud.application.port.in.FindGroupsUseCase;
import com.odcloud.application.service.find_groups.FindGroupsServiceResponse;
import com.odcloud.infrastructure.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
class FindGroupsController {

    private final FindGroupsUseCase useCase;

    @GetMapping("/groups")
    ApiResponse<FindGroupsResponse> findAll(
        @Valid @ModelAttribute FindGroupsRequest request
    ) {
        FindGroupsServiceResponse serviceResponse = useCase.findAll(request.toCommand());
        return ApiResponse.ok(FindGroupsResponse.of(serviceResponse));
    }
}
