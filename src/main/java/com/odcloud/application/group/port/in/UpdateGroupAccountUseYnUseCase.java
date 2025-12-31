package com.odcloud.application.group.port.in;

import com.odcloud.application.group.service.update_group_account_use_yn.UpdateGroupAccountUseYnServiceResponse;
import com.odcloud.application.port.in.command.UpdateGroupAccountUseYnCommand;

public interface UpdateGroupAccountUseYnUseCase {

    UpdateGroupAccountUseYnServiceResponse updateShowYn(UpdateGroupAccountUseYnCommand command);
}
