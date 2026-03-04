package com.odcloud.application.file.service.update_file;

import com.odcloud.domain.model.Account;
import lombok.Builder;
import org.springframework.util.StringUtils;

@Builder
public record UpdateFileCommand(

    Long fileId,

    Account account,

    String fileName,

    Long folderId
) {

    public boolean isFileNameUpdate(String fileName) {
        return StringUtils.hasText(this.fileName) && !this.fileName.equals(fileName);
    }

    public boolean isFileLocUpdate(Long folderId) {
        return this.folderId != null && !this.folderId.equals(folderId);
    }

}
