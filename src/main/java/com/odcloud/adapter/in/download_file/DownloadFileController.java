package com.odcloud.adapter.in.download_file;

import com.odcloud.application.port.in.DownloadFileUseCase;
import com.odcloud.application.port.in.command.DownloadFileCommand;
import com.odcloud.application.service.download_file.DownloadFileServiceResponse;
import jakarta.validation.constraints.NotEmpty;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
class DownloadFileController {

    private final DownloadFileUseCase downloadFileUseCase;

    @GetMapping("/files/download")
    ResponseEntity<byte[]> downloadFile(
        @RequestParam @NotEmpty(message = "파일 ID는 필수값 입니다") List<Long> fileIds) {

        DownloadFileCommand command = DownloadFileCommand.builder()
            .fileIds(fileIds)
            .build();

        DownloadFileServiceResponse response = downloadFileUseCase.downloadFile(command);

        try {
            String encodedFileName = URLEncoder.encode(response.fileName(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(response.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + encodedFileName + "\"")
                .body(response.fileData());
        } catch (Exception e) {
            throw new RuntimeException("파일 다운로드 중 오류가 발생했습니다", e);
        }
    }
}
