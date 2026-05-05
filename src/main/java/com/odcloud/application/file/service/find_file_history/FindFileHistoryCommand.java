package com.odcloud.application.file.service.find_file_history;

import com.odcloud.domain.model.Account;
import lombok.Builder;

@Builder
public record FindFileHistoryCommand(
    Long fileId,
    Account account
) {

}
