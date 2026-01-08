package com.odcloud.application.account.service.delete_account;

import com.odcloud.application.account.port.in.DeleteAccountUseCase;
import com.odcloud.application.account.port.out.AccountStoragePort;
import com.odcloud.application.device.port.out.AccountDeviceStoragePort;
import com.odcloud.application.group.port.in.DeleteGroupUseCase;
import com.odcloud.application.group.port.in.command.DeleteGroupCommand;
import com.odcloud.application.group.port.out.GroupStoragePort;
import com.odcloud.application.schedule.port.out.ScheduleStoragePort;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.Schedule;
import jakarta.transaction.Transactional;
import java.util.List;
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
        List<Schedule> personalSchedules = scheduleStoragePort.findByWriterEmailAndGroupIdIsNull(
            account.getEmail());
        for (Schedule schedule : personalSchedules) {
            scheduleStoragePort.delete(schedule);
        }

        List<Group> ownedGroups = groupStoragePort.findByOwnerEmail(account.getEmail());
        for (Group group : ownedGroups) {
            deleteGroupUseCase.delete(DeleteGroupCommand.builder()
                .groupId(group.getId())
                .currentOwnerEmail(account.getEmail())
                .build());
        }

        accountStoragePort.delete(account);
        return DeleteAccountServiceResponse.ofSuccess();
    }
}
