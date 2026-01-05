package com.odcloud.application.group.service.delete_group;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_INVALID_GROUP_OWNER;

import com.odcloud.application.file.port.out.FileInfoStoragePort;
import com.odcloud.application.file.port.out.FilePort;
import com.odcloud.application.file.port.out.FolderInfoStoragePort;
import com.odcloud.application.group.port.in.DeleteGroupUseCase;
import com.odcloud.application.group.port.in.command.DeleteGroupCommand;
import com.odcloud.application.group.port.out.GroupStoragePort;
import com.odcloud.application.group.port.out.NoticeStoragePort;
import com.odcloud.application.schedule.port.out.ScheduleStoragePort;
import com.odcloud.domain.model.FileInfo;
import com.odcloud.domain.model.FolderInfo;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.Notice;
import com.odcloud.domain.model.Schedule;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class DeleteGroupService implements DeleteGroupUseCase {

    private final GroupStoragePort groupStoragePort;
    private final FolderInfoStoragePort folderInfoStoragePort;
    private final FileInfoStoragePort fileInfoStoragePort;
    private final FilePort filePort;
    private final ScheduleStoragePort scheduleStoragePort;
    private final NoticeStoragePort noticeStoragePort;

    @Override
    @Transactional
    public DeleteGroupServiceResponse delete(DeleteGroupCommand command) {
        Group group = groupStoragePort.findById(command.groupId());
        if (!group.getOwnerEmail().equals(command.currentOwnerEmail())) {
            throw new CustomBusinessException(Business_INVALID_GROUP_OWNER);
        }

        List<FolderInfo> folders = folderInfoStoragePort.findByGroupId(command.groupId());

        for (FolderInfo folder : folders) {
            List<FileInfo> files = fileInfoStoragePort.findByFolderId(folder.getId());
            for (FileInfo file : files) {
                filePort.deleteFile(file.getFileLoc());
                fileInfoStoragePort.delete(file);
            }

            filePort.deleteFolder(folder.getPath());
            folderInfoStoragePort.delete(folder);
        }

        List<Schedule> schedules = scheduleStoragePort.findByGroupId(command.groupId());
        for (Schedule schedule : schedules) {
            scheduleStoragePort.delete(schedule);
        }

        List<Notice> notices = noticeStoragePort.findByGroupId(command.groupId(), Integer.MAX_VALUE);
        for (Notice notice : notices) {
            noticeStoragePort.delete(notice);
        }

        groupStoragePort.deleteGroupAccountsByGroupId(command.groupId());
        groupStoragePort.delete(group);
        return DeleteGroupServiceResponse.ofSuccess();
    }
}
