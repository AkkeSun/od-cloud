package com.odcloud.application.group.service.find_group;

import com.odcloud.application.group.port.in.FindGroupUseCase;
import com.odcloud.application.group.port.out.GroupStoragePort;
import com.odcloud.application.group.port.out.NoticeStoragePort;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.Notice;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class FindGroupService implements FindGroupUseCase {

    private final GroupStoragePort groupStoragePort;
    private final NoticeStoragePort noticeStoragePort;

    @Override
    @Transactional(readOnly = true)
    public FindGroupServiceResponse findById(String groupId) {
        Group group = groupStoragePort.findById(groupId);
        List<Notice> notices = noticeStoragePort.findByGroupId(groupId, 5);
        return FindGroupServiceResponse.of(group, notices);
    }
}
