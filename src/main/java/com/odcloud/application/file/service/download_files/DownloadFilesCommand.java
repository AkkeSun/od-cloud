package com.odcloud.application.file.service.download_files;

import com.odcloud.domain.model.Account;
import java.util.List;
import lombok.Builder;

@Builder
public record DownloadFilesCommand(
    Account account,
    List<Long> fileIds
) {

}
