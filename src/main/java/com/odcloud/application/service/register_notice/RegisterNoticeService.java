package com.odcloud.application.service.register_notice;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_INVALID_GROUP_OWNER;

import com.odcloud.application.port.in.PushFcmUseCase;
import com.odcloud.application.port.in.RegisterNoticeUseCase;
import com.odcloud.application.port.in.command.PushFcmCommand;
import com.odcloud.application.port.in.command.RegisterNoticeCommand;
import com.odcloud.application.port.out.AccountDeviceStoragePort;
import com.odcloud.application.port.out.GroupStoragePort;
import com.odcloud.application.port.out.NoticeStoragePort;
import com.odcloud.domain.model.AccountDevice;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.Notice;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class RegisterNoticeService implements RegisterNoticeUseCase {

    private final PushFcmUseCase pushFcmUseCase;
    private final GroupStoragePort groupStoragePort;
    private final NoticeStoragePort noticeStoragePort;
    private final AccountDeviceStoragePort accountDeviceStoragePort;

    @Override
    @Transactional
    public RegisterNoticeServiceResponse register(RegisterNoticeCommand command) {
        Group group = groupStoragePort.findById(command.groupId());
        if (!group.getOwnerEmail().equals(command.account().getEmail())) {
            throw new CustomBusinessException(Business_INVALID_GROUP_OWNER);
        }

        Notice savedNotice = noticeStoragePort.save(Notice.of(command));
        List<AccountDevice> devices = accountDeviceStoragePort
            .findByGroupIdForPush(command.groupId())
            .stream()
            .filter(device -> !device.getAccountId().equals(command.account().getId()))
            .toList();

        if (!devices.isEmpty()) {
            pushFcmUseCase.pushAsync(PushFcmCommand.ofNotice(devices, group, savedNotice));
        }

        return RegisterNoticeServiceResponse.ofSuccess();
    }
}
