package com.odcloud.application.service.update_group_account_status;

import static com.odcloud.adapter.out.mail.MailRequest.ofGroupAccountStatusApproved;

import com.odcloud.application.port.in.UpdateGroupAccountStatusUseCase;
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
    public UpdateGroupAccountStatusServiceResponse updateStatus(String groupId, Long accountId,
        String status) {
        GroupAccount groupAccount = groupStoragePort.findGroupAccountByGroupIdAndAccountId(groupId,
            accountId);

        groupAccount.updateStatus(status);
        groupStoragePort.save(groupAccount);

        mailPort.send(ofGroupAccountStatusApproved(groupId, groupAccount.getName(),
            groupAccount.getEmail(), status));

        return UpdateGroupAccountStatusServiceResponse.ofSuccess();
    }
}
