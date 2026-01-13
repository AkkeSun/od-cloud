package com.odcloud.application.account.service.delete_account;

import com.odcloud.application.account.port.in.DeleteAccountUseCase;
import com.odcloud.application.account.port.out.AccountStoragePort;
import com.odcloud.application.device.port.out.AccountDeviceStoragePort;
import com.odcloud.application.group.port.in.DeleteGroupUseCase;
import com.odcloud.application.group.port.in.command.DeleteGroupCommand;
import com.odcloud.application.group.port.out.GroupStoragePort;
import com.odcloud.application.schedule.port.out.ScheduleStoragePort;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.GroupAccount;
import com.odcloud.domain.model.Schedule;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class DeleteAccountService implements DeleteAccountUseCase {

    private final AccountStoragePort accountStoragePort;
    private final AccountDeviceStoragePort accountDeviceStoragePort;
    private final ScheduleStoragePort scheduleStoragePort;
    private final GroupStoragePort groupStoragePort;
    private final DeleteGroupUseCase deleteGroupUseCase;

    @Override
    @Transactional
    public DeleteAccountServiceResponse delete(Account account) {
        accountDeviceStoragePort.deleteByAccountId(account.getId());

        for (Schedule schedule : scheduleStoragePort.findByPersonalSchedules(account.getEmail())) {
            scheduleStoragePort.delete(schedule);
        }

        for (GroupAccount groupAccount : groupStoragePort.findGroupAccountsByAccountId(
            account.getId())) {

            if (groupAccount.isOwner(account.getEmail())) {
                deleteGroupUseCase.delete(DeleteGroupCommand.builder()
                    .groupId(groupAccount.getGroupId())
                    .currentOwnerEmail(account.getEmail())
                    .build());
            } else {
                groupStoragePort.deleteGroupAccountById(groupAccount.getId());
            }
        }

        accountStoragePort.delete(account);
        return DeleteAccountServiceResponse.ofSuccess();
    }
}
