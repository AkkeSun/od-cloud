package com.odcloud.application.group.service.update_group_account_use_yn;

import com.odcloud.domain.model.Account;
import lombok.Builder;

@Builder
public record UpdateGroupAccountUseYnCommand(
    Long groupId,
    Account account,
    String showYn
) {

}
