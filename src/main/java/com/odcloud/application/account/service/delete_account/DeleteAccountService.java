package com.odcloud.application.account.service.delete_account;

import com.odcloud.application.account.port.in.DeleteAccountUseCase;
import com.odcloud.application.account.port.out.AccountStoragePort;
import com.odcloud.application.device.port.out.AccountDeviceStoragePort;
import com.odcloud.application.file.port.out.FileInfoStoragePort;
import com.odcloud.application.file.port.out.FilePort;
import com.odcloud.application.file.port.out.FolderInfoStoragePort;
import com.odcloud.application.group.port.out.GroupStoragePort;
import com.odcloud.application.schedule.port.out.ScheduleStoragePort;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.FileInfo;
import com.odcloud.domain.model.FolderInfo;
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
    private final FolderInfoStoragePort folderInfoStoragePort;
    private final FileInfoStoragePort fileInfoStoragePort;
    private final FilePort filePort;

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
            deleteGroupData(group);
        }

        accountStoragePort.delete(account);
        return DeleteAccountServiceResponse.ofSuccess();
    }

    private void deleteGroupData(Group group) {
        List<FolderInfo> folders = folderInfoStoragePort.findByGroupId(group.getId());

        for (FolderInfo folder : folders) {
            List<FileInfo> files = fileInfoStoragePort.findByFolderId(folder.getId());
            for (FileInfo file : files) {
                filePort.deleteFile(file.getFileLoc());
                fileInfoStoragePort.delete(file);
            }

            filePort.deleteFolder(folder.getPath());
            folderInfoStoragePort.delete(folder);
        }

        List<Schedule> groupSchedules = scheduleStoragePort.findByGroupId(group.getId());
        for (Schedule schedule : groupSchedules) {
            scheduleStoragePort.delete(schedule);
        }

        groupStoragePort.deleteGroupAccountsByGroupId(group.getId());
        groupStoragePort.delete(group);
    }
}
