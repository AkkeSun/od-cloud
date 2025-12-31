package com.odcloud.application.group.service.update_group_account_use_yn;

import com.odcloud.application.group.port.in.UpdateGroupAccountUseYnUseCase;
import com.odcloud.application.group.port.out.GroupStoragePort;
import com.odcloud.application.port.in.command.UpdateGroupAccountUseYnCommand;
import com.odcloud.domain.model.GroupAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class UpdateGroupAccountUseYnService implements UpdateGroupAccountUseYnUseCase {

    private final GroupStoragePort groupStoragePort;

    @Override
    @Transactional
    public UpdateGroupAccountUseYnServiceResponse updateShowYn(
        UpdateGroupAccountUseYnCommand command) {
        GroupAccount groupAccount = groupStoragePort.findGroupAccountByGroupIdAndAccountId(
            command.groupId(), command.account().getId());
        groupAccount.updateShowYn(command.showYn());
        groupStoragePort.save(groupAccount);
        return UpdateGroupAccountUseYnServiceResponse.ofSuccess();
    }
}
