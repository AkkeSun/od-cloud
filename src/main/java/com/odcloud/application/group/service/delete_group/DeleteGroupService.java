package com.odcloud.application.group.service.delete_group;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_INVALID_GROUP_OWNER;

import com.odcloud.application.file.port.out.FileInfoStoragePort;
import com.odcloud.application.file.port.out.FilePort;
import com.odcloud.application.file.port.out.FolderInfoStoragePort;
import com.odcloud.application.group.port.in.DeleteGroupUseCase;
import com.odcloud.application.group.port.out.GroupStoragePort;
import com.odcloud.application.group.port.out.NoticeStoragePort;
import com.odcloud.application.schedule.port.out.ScheduleStoragePort;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.FileInfo;
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
    public DeleteGroupResponse delete(Long groupId, Account account) {
        Group group = groupStoragePort.findById(groupId);
        if (!group.getOwnerEmail().equals(account.getEmail())) {
            throw new CustomBusinessException(Business_INVALID_GROUP_OWNER);
        }

        List<FileInfo> files = fileInfoStoragePort.findByGroupId(groupId);
        for (FileInfo file : files) {
            filePort.deleteFile(file.getFileLoc());
        }
        fileInfoStoragePort.deleteByGroupId(groupId);
        folderInfoStoragePort.deleteByGroupId(groupId);

        List<Schedule> schedules = scheduleStoragePort.findByGroupId(groupId);
        for (Schedule schedule : schedules) {
            scheduleStoragePort.delete(schedule);
        }

        List<Notice> notices = noticeStoragePort.findByGroupId(groupId, Integer.MAX_VALUE);
        for (Notice notice : notices) {
            noticeStoragePort.delete(notice);
        }

        groupStoragePort.deleteGroupAccountsByGroupId(groupId);
        groupStoragePort.delete(group);
        return DeleteGroupResponse.ofSuccess();
    }
}
