package com.odcloud.application.service.update_group_account_status;

import static com.odcloud.adapter.out.mail.MailRequest.ofGroupAccountStatusApproved;

import com.odcloud.application.port.in.UpdateGroupAccountStatusUseCase;
import com.odcloud.application.port.in.command.UpdateGroupAccountStatusCommand;
import com.odcloud.application.port.out.GroupStoragePort;
import com.odcloud.application.port.out.MailPort;
import com.odcloud.domain.model.GroupAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class UpdateGroupAccountStatusService implements UpdateGroupAccountStatusUseCase {

    private final GroupStoragePort groupStoragePort;
    private final MailPort mailPort;

    @Override
    @Transactional
    public UpdateGroupAccountStatusServiceResponse updateStatus(
        UpdateGroupAccountStatusCommand command
    ) {
        GroupAccount groupAccount = groupStoragePort.findGroupAccountByGroupIdAndAccountId(command);
        groupAccount.updateStatus(command.status());
        groupStoragePort.save(groupAccount);

        if (groupAccount.isApproved()) {
            mailPort.send(ofGroupAccountStatusApproved(groupAccount));
        }
        return UpdateGroupAccountStatusServiceResponse.ofSuccess();
    }
}
