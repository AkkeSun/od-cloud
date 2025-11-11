package com.odcloud.application.service.register_group;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_SAVED_GROUP;

import com.odcloud.application.port.in.RegisterGroupUseCase;
import com.odcloud.application.port.in.command.RegisterGroupCommand;
import com.odcloud.application.port.out.FileUploadPort;
import com.odcloud.application.port.out.FolderStoragePort;
import com.odcloud.application.port.out.GroupStoragePort;
import com.odcloud.domain.model.Folder;
import com.odcloud.domain.model.Group;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class RegisterGroupService implements RegisterGroupUseCase {

    private final FileUploadPort fileUploadPort;
    private final GroupStoragePort groupStoragePort;
    private final FolderStoragePort folderStoragePort;

    @Override
    @Transactional
    public RegisterGroupServiceResponse register(RegisterGroupCommand command) {
        if (groupStoragePort.existsById(command.id())) {
            throw new CustomBusinessException(Business_SAVED_GROUP);
        }

        groupStoragePort.save(Group.of(command));

        Folder folder = Folder.ofRootFolder(command);
        folderStoragePort.save(folder);
        fileUploadPort.createFolder(folder.getPath());

        return RegisterGroupServiceResponse.ofSuccess();
    }
}
