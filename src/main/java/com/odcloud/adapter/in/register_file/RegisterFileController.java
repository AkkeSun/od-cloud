package com.odcloud.adapter.in.register_file;

import com.odcloud.application.port.in.RegisterFileUseCase;
import com.odcloud.application.service.register_file.RegisterFileServiceResponse;
import com.odcloud.infrastructure.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
class RegisterFileController {

    private final RegisterFileUseCase createFileUseCase;

    @PostMapping("/files")
    ApiResponse<RegisterFileResponse> createFile(@Valid RegisterFileRequest request) {
        RegisterFileServiceResponse response = createFileUseCase.createFile(request.toCommand());
        return ApiResponse.ok(RegisterFileResponse.of(response));
    }
}
