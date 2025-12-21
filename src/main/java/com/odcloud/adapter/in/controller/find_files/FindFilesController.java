package com.odcloud.adapter.in.controller.find_files;

import com.odcloud.application.port.in.FindFilesUseCase;
import com.odcloud.application.service.find_files.FindFilesServiceResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.response.ApiResponse;
import com.odcloud.resolver.LoginAccount;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
class FindFilesController {

    private final FindFilesUseCase useCase;

    @GetMapping("/files")
    ApiResponse<FindFilesResponse> findAll(
        @Valid @ModelAttribute FindFilesRequest request,
        @LoginAccount Account account
    ) {
        FindFilesServiceResponse serviceResponse = useCase.findAll(request.toCommand(account));
        return ApiResponse.ok(FindFilesResponse.of(serviceResponse));
    }
}
