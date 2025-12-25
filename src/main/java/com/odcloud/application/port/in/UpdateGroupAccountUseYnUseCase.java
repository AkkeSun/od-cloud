package com.odcloud.application.port.in;

import com.odcloud.application.port.in.command.UpdateGroupAccountUseYnCommand;
import com.odcloud.application.service.update_group_account_use_yn.UpdateGroupAccountUseYnServiceResponse;

public interface UpdateGroupAccountUseYnUseCase {

    UpdateGroupAccountUseYnServiceResponse updateShowYn(UpdateGroupAccountUseYnCommand command);
}
