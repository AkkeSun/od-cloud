package com.odcloud.application.group.service.find_group_self;

import com.odcloud.application.file.port.out.FolderInfoStoragePort;
import com.odcloud.application.group.port.in.FindGroupSelfUseCase;
import com.odcloud.application.group.port.out.GroupStoragePort;
import com.odcloud.application.group.service.find_group_self.FindGroupSelfResponse.ActiveGroupInfo;
import com.odcloud.application.group.service.find_group_self.FindGroupSelfResponse.DeniedGroupInfo;
import com.odcloud.application.group.service.find_group_self.FindGroupSelfResponse.PendingGroupInfo;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.GroupAccount;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class FindGroupSelfService implements FindGroupSelfUseCase {

    private final GroupStoragePort groupStoragePort;
    private final FolderInfoStoragePort folderInfoStoragePort;

    @Override
    @Transactional(readOnly = true)
    public FindGroupSelfResponse findSelf(Account account) {
        List<GroupAccount> groupAccounts = groupStoragePort.findGroupAccountsByAccountId(
            account.getId());

        List<ActiveGroupInfo> activeGroups = groupAccounts.stream()
            .filter(ga -> "ACTIVE".equals(ga.getStatus()))
            .filter(ga -> "Y".equals(ga.getShowYn()))
            .map(ga -> {
                Group group = groupStoragePort.findById(ga.getGroupId());
                return ActiveGroupInfo.of(group,
                    folderInfoStoragePort.findRootFolderByGroupId(ga.getGroupId()));
            })
            .toList();

        List<PendingGroupInfo> pendingGroups = groupAccounts.stream()
            .filter(ga -> "PENDING".equals(ga.getStatus()))
            .filter(ga -> "Y".equals(ga.getShowYn()))
            .map(ga -> {
                Group group = groupStoragePort.findById(ga.getGroupId());
                return PendingGroupInfo.of(group);
            })
            .toList();

        List<DeniedGroupInfo> deniedGroups = groupAccounts.stream()
            .filter(ga -> "DENIED".equals(ga.getStatus()))
            .filter(ga -> "Y".equals(ga.getShowYn()))
            .map(ga -> {
                Group group = groupStoragePort.findById(ga.getGroupId());
                return DeniedGroupInfo.of(group, ga);
            })
            .toList();

        return FindGroupSelfResponse.of(activeGroups, pendingGroups, deniedGroups);
    }
}
