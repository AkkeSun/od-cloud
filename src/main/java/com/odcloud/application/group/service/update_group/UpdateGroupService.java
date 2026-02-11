package com.odcloud.application.group.service.update_group;

import static com.odcloud.infrastructure.constant.CommonConstant.DEFAULT_STORAGE_TOTAL;
import static com.odcloud.infrastructure.constant.CommonConstant.GROUP_LOCK;
import static com.odcloud.infrastructure.exception.ErrorCode.Business_GROUP_LIMIT_EXCEEDED;
import static com.odcloud.infrastructure.exception.ErrorCode.Business_INVALID_GROUP_OWNER;
import static com.odcloud.infrastructure.exception.ErrorCode.Business_SAVED_GROUP;

import com.odcloud.application.account.port.out.AccountStoragePort;
import com.odcloud.application.auth.port.out.RedisStoragePort;
import com.odcloud.application.file.port.out.FolderInfoStoragePort;
import com.odcloud.application.group.port.in.UpdateGroupUseCase;
import com.odcloud.application.group.port.in.command.UpdateGroupCommand;
import com.odcloud.application.group.port.out.GroupStoragePort;
import com.odcloud.application.voucher.port.out.VoucherStoragePort;
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
class UpdateGroupService implements UpdateGroupUseCase {

    private final GroupStoragePort groupStoragePort;
    private final AccountStoragePort accountStoragePort;
    private final FolderInfoStoragePort folderInfoStoragePort;
    private final VoucherStoragePort voucherStoragePort;
    private final RedisStoragePort redisStoragePort;

    @Override
    @Transactional
    public UpdateGroupServiceResponse update(UpdateGroupCommand command) {
        redisStoragePort.executeWithLock(GROUP_LOCK + command.groupId(), () -> {
            Group group = groupStoragePort.findById(command.groupId());
            if (!group.getOwnerEmail().equals(command.currentOwnerEmail())) {
                throw new CustomBusinessException(Business_INVALID_GROUP_OWNER);
            }

            if (group.needsOwnerEmailUpdate(command.newOwnerEmail())) {
                Account newOwner = accountStoragePort.findByEmail(command.newOwnerEmail());
                if (groupStoragePort.countByOwnerEmail(command.newOwnerEmail()) >= 3) {
                    throw new CustomBusinessException(Business_GROUP_LIMIT_EXCEEDED);
                }

                groupStoragePort.findGroupAccountsByAccountId(newOwner.getId())
                    .stream()
                    .filter(ga -> ga.getGroupId().equals(group.getId()))
                    .findAny()
                    .orElseGet(() -> {
                        groupStoragePort.save(GroupAccount.ofGroupOwner(group, newOwner));
                        return null;
                    });

                group.updateOwnerEmail(command.newOwnerEmail());
                voucherStoragePort.findActiveByAccountId(newOwner.getId())
                    .stream()
                    .filter(voucher -> voucher.getVoucherType().isStorageVoucher())
                    .findFirst()
                    .ifPresentOrElse(
                        voucher -> group.updateStorageTotal(
                            voucher.getVoucherType().getStorageIncreaseSize()),
                        () -> group.updateStorageTotal(DEFAULT_STORAGE_TOTAL)
                    );
            }

            if (group.needsNameUpdate(command.name())) {
                if (groupStoragePort.existsByName(command.name())) {
                    throw new CustomBusinessException(Business_SAVED_GROUP);
                }
                group.updateName(command.name());

                FolderInfo rootFolder = folderInfoStoragePort.findRootFolderByGroupId(
                    group.getId());
                rootFolder.update(command.name());
                folderInfoStoragePort.save(rootFolder);
            }

            groupStoragePort.save(group);
            return null;
        });

        return UpdateGroupServiceResponse.ofSuccess();
    }
}
