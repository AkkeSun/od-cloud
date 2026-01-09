package com.odcloud.adapter.in.controller.voucher.create_voucher;

import com.odcloud.application.voucher.port.in.CreateVoucherUseCase;
import com.odcloud.application.voucher.service.create_voucher.CreateVoucherServiceResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.resolver.LoginAccount;
import com.odcloud.infrastructure.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class CreateVoucherController {

    private final CreateVoucherUseCase useCase;

    @PostMapping("/vouchers")
    ApiResponse<CreateVoucherResponse> createVoucher(
        @RequestBody @Valid CreateVoucherRequest request,
        @LoginAccount Account account
    ) {
        CreateVoucherServiceResponse serviceResponse = useCase.create(request.toCommand(account));
        return ApiResponse.ok(CreateVoucherResponse.of(serviceResponse));
    }
}
