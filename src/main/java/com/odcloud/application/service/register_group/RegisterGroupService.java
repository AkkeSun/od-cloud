package com.odcloud.application.service.register_group;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_SAVED_GROUP;
import static com.odcloud.infrastructure.util.JsonUtil.toJsonString;

import com.odcloud.application.port.in.RegisterGroupUseCase;
import com.odcloud.application.port.in.command.RegisterGroupCommand;
import com.odcloud.application.port.out.FileUploadPort;
import com.odcloud.application.port.out.FolderStoragePort;
import com.odcloud.application.port.out.GroupStoragePort;
import com.odcloud.application.port.out.RedisStoragePort;
import com.odcloud.domain.model.Folder;
import com.odcloud.domain.model.Group;
import com.odcloud.infrastructure.constant.ProfileConstant;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class RegisterGroupService implements RegisterGroupUseCase {

    private final ProfileConstant constant;
    private final GroupStoragePort groupStoragePort;
    private final RedisStoragePort redisStoragePort;
    private final FolderStoragePort folderStoragePort;
    private final FileUploadPort fileUploadPort;

    @Override
    @Transactional
    public RegisterGroupServiceResponse register(RegisterGroupCommand command) {
        if (groupStoragePort.existsById(command.id())) {
            throw new CustomBusinessException(Business_SAVED_GROUP);
        }

        groupStoragePort.save(Group.of(command));
        List<Group> savedGroups = groupStoragePort.findAll();

        redisStoragePort.register(constant.redisKey().group(), toJsonString(savedGroups));

        Folder folder = Folder.ofRootFolder(command.id(), command.description(),
            command.ownerEmail());
        folderStoragePort.save(folder);
        fileUploadPort.createFolder(folder.getPath());

        return RegisterGroupServiceResponse.ofSuccess();
    }
}
