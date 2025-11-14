package com.odcloud.adapter.in.download_file;

import com.odcloud.application.port.in.command.DownloadFilesCommand;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class DownloadFilesRequest {

    @NotEmpty(message = "파일 ID 목록은 필수값 입니다")
    private List<Long> fileIds;

    DownloadFilesCommand toCommand() {
        return DownloadFilesCommand.builder()
            .fileIds(fileIds)
            .build();
    }
}
