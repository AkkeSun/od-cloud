package com.odcloud.adapter.in.controller.file.register_file;

import com.odcloud.application.file.port.in.RegisterFileUseCase;
import com.odcloud.application.file.service.register_file.RegisterFileServiceResponse;
import com.odcloud.infrastructure.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
class RegisterFileController {

    private final RegisterFileUseCase useCase;

    @PostMapping("/folders/{folderId}/files")
    ApiResponse<RegisterFileResponse> register(
        @PathVariable Long folderId,
        @Valid RegisterFileRequest request
    ) {
        RegisterFileServiceResponse response = useCase.register(request.toCommand(folderId));
        return ApiResponse.ok(RegisterFileResponse.of(response));
    }
}
