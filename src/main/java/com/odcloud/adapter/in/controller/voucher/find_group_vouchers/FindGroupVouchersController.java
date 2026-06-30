package com.odcloud.adapter.in.controller.voucher.find_group_vouchers;

import com.odcloud.application.voucher.port.in.FindGroupVouchersUseCase;
import com.odcloud.application.voucher.service.find_group_vouchers.FindGroupVouchersResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.resolver.LoginAccount;
import com.odcloud.infrastructure.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class FindGroupVouchersController {

    private final FindGroupVouchersUseCase useCase;

    @GetMapping("/vouchers/active")
    ApiResponse<FindGroupVouchersResponse> find(@LoginAccount Account account) {
        return ApiResponse.ok(useCase.find(account));
    }
}
