package com.odcloud.adapter.in.controller.file.delete_file;

import com.odcloud.application.file.port.in.command.DeleteFileCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.util.StringUtil;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Builder;

@Builder
record DeleteFileRequest(
    @NotEmpty(message = "파일 ID 목록은 필수입니다")
    List<Long> fileIds
) {

    DeleteFileCommand toCommand(Account account) {
        return DeleteFileCommand.builder()
            .account(account)
            .fileIds(fileIds)
            .build();
    }

    @Override
    public String toString() {
        return StringUtil.toString(this);
    }
}
