package com.odcloud.adapter.in.controller.file.download_files;

import com.odcloud.application.file.service.download_files.DownloadFilesCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.util.StringUtil;
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
public class DownloadFilesRequest {

    @NotEmpty(message = "파일 아이디 목록은 필수값 입니다")
    List<Long> fileIds;

    DownloadFilesCommand toCommand(Account account) {
        return DownloadFilesCommand.builder()
            .account(account)
            .fileIds(fileIds)
            .build();
    }

    @Override
    public String toString() {
        return StringUtil.toString(this);
    }
}
