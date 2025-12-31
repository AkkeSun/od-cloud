package com.odcloud.adapter.in.controller.group.find_group_self;

import com.odcloud.application.group.port.in.FindGroupSelfUseCase;
import com.odcloud.application.group.service.find_group_self.FindGroupSelfServiceResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.resolver.LoginAccount;
import com.odcloud.infrastructure.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class FindGroupSelfController {

    private final FindGroupSelfUseCase useCase;

    @GetMapping("/groups/self")
    ApiResponse<FindGroupSelfResponse> findSelf(@LoginAccount Account account) {
        FindGroupSelfServiceResponse serviceResponse = useCase.findSelf(account);
        return ApiResponse.ok(FindGroupSelfResponse.of(serviceResponse));
    }
}
