package com.odcloud.application.file.port.in.command;

import com.odcloud.domain.model.Account;
import lombok.Builder;
import org.springframework.util.StringUtils;

@Builder
public record FindFilesCommand(
    Account account,
    Long folderId,
    String sortType,
    Long groupId,
    String keyword
) {

    public boolean isLikeSearch() {
        return StringUtils.hasText(keyword) &&
            (keyword.contains(".") || keyword.contains("_") || keyword.contains("-"));
    }

    public boolean isFulltextSearch() {
        return StringUtils.hasText(keyword) &&
            (!keyword.contains(".") && !keyword.contains("_") && !keyword.contains("-"));
    }

    public boolean isRootSearch() {
        return folderId == null;
    }
}
