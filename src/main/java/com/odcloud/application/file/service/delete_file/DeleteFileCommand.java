package com.odcloud.application.file.service.delete_file;

import com.odcloud.domain.model.Account;
import java.util.List;
import lombok.Builder;

@Builder
public record DeleteFileCommand(
    Account account,
    List<Long> fileIds
) {

}
