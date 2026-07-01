package com.odcloud.adapter.in.controller.subscription.find_group_subscriptions;

import com.odcloud.application.subscription.port.in.FindGroupSubscriptionsUseCase;
import com.odcloud.application.subscription.service.find_group_subscriptions.FindGroupSubscriptionsResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Group;
import com.odcloud.infrastructure.resolver.LoginAccount;
import com.odcloud.infrastructure.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class FindGroupSubscriptionsController {

    private final FindGroupSubscriptionsUseCase useCase;

    @GetMapping("/subscriptions/active")
    ApiResponse<List<FindGroupSubscriptionsResponse>> find(@LoginAccount Account account) {
        return ApiResponse.ok(useCase.find(account));
    }

    @GetMapping("/test/subscriptions/active")
    ApiResponse<List<FindGroupSubscriptionsResponse>> findForTest(List<Long> groupIds) {
        return ApiResponse.ok(useCase.find(Account.builder()
                .groups(groupIds.stream()
                .map(id -> Group.builder().id(id).build())
                .toList())
            .build()));
    }
}
