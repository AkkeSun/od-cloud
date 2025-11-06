package com.odcloud.application.service.register_group;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_SAVED_GROUP;

import com.odcloud.application.port.in.RegisterGroupUseCase;
import com.odcloud.application.port.in.command.RegisterGroupCommand;
import com.odcloud.application.port.out.GroupStoragePort;
import com.odcloud.domain.model.Group;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class RegisterGroupService implements RegisterGroupUseCase {

    private final GroupStoragePort groupStoragePort;

    @Override
    @Transactional
    public RegisterGroupServiceResponse register(RegisterGroupCommand command) {
        if (groupStoragePort.existsById(command.id())) {
            throw new CustomBusinessException(Business_SAVED_GROUP);
        }

        groupStoragePort.register(Group.of(command));
        return RegisterGroupServiceResponse.ofSuccess();
    }
}
