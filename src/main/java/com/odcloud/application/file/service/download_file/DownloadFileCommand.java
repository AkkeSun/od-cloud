package com.odcloud.application.file.service.download_file;

import com.odcloud.domain.model.Account;
import lombok.Builder;

@Builder
public record DownloadFileCommand(
    Account account,
    Long fileId
) {

}