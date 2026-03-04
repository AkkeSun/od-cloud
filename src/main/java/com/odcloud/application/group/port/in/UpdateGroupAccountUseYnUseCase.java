package com.odcloud.application.group.port.in;

import com.odcloud.application.group.service.update_group_account_use_yn.UpdateGroupAccountUseYnCommand;
import com.odcloud.application.group.service.update_group_account_use_yn.UpdateGroupAccountUseYnResponse;

public interface UpdateGroupAccountUseYnUseCase {

    UpdateGroupAccountUseYnResponse updateShowYn(UpdateGroupAccountUseYnCommand command);
}
