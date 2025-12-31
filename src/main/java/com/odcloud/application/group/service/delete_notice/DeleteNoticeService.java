package com.odcloud.application.group.service.delete_notice;

import com.odcloud.application.group.port.in.DeleteNoticeUseCase;
import com.odcloud.application.group.port.out.GroupStoragePort;
import com.odcloud.application.group.port.out.NoticeStoragePort;
import com.odcloud.application.port.in.command.DeleteNoticeCommand;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.Notice;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class DeleteNoticeService implements DeleteNoticeUseCase {

    private final GroupStoragePort groupStoragePort;
    private final NoticeStoragePort noticeStoragePort;

    @Override
    @Transactional
    public DeleteNoticeServiceResponse delete(DeleteNoticeCommand command) {
        Group group = groupStoragePort.findById(command.groupId());
        if (!group.getOwnerEmail().equals(command.account().getEmail())) {
            throw new CustomBusinessException(ErrorCode.Business_INVALID_GROUP_OWNER);
        }

        Notice notice = noticeStoragePort.findById(command.noticeId());
        if (!notice.getGroupId().equals(command.groupId())) {
            throw new CustomBusinessException(ErrorCode.Business_NOT_FOUND_NOTICE);
        }

        noticeStoragePort.delete(notice);
        return DeleteNoticeServiceResponse.ofSuccess();
    }
}
