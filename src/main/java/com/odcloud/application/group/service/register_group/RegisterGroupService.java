package com.odcloud.application.group.service.register_group;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_GROUP_LIMIT_EXCEEDED;
import static com.odcloud.infrastructure.exception.ErrorCode.Business_SAVED_GROUP;

import com.odcloud.application.account.port.out.AccountStoragePort;
import com.odcloud.application.file.port.out.FilePort;
import com.odcloud.application.file.port.out.FolderInfoStoragePort;
import com.odcloud.application.group.port.in.RegisterGroupUseCase;
import com.odcloud.application.group.port.in.command.RegisterGroupCommand;
import com.odcloud.application.group.port.out.GroupStoragePort;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.FolderInfo;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.GroupAccount;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class RegisterGroupService implements RegisterGroupUseCase {

    private final FilePort fileUploadPort;
    private final GroupStoragePort groupStoragePort;
    private final FolderInfoStoragePort folderStoragePort;
    private final AccountStoragePort accountStoragePort;

    @Override
    @Transactional
    public RegisterGroupServiceResponse register(RegisterGroupCommand command) {
        if (groupStoragePort.existsByName(command.name())) {
            throw new CustomBusinessException(Business_SAVED_GROUP);
        }

        if (groupStoragePort.countByOwnerEmail(command.ownerEmail()) >= 3) {
            throw new CustomBusinessException(Business_GROUP_LIMIT_EXCEEDED);
        }

        Group group = groupStoragePort.save(Group.of(command));
        Account account = accountStoragePort.findByEmail(command.ownerEmail());
        groupStoragePort.save(GroupAccount.ofGroupOwner(group, account));

        FolderInfo folder = FolderInfo.ofRootFolder(group);
        folderStoragePort.save(folder);
        fileUploadPort.createFolder(folder.getPath());

        return RegisterGroupServiceResponse.ofSuccess();
    }
}
