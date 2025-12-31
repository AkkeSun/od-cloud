package com.odcloud.application.account.service.update_account;

import com.odcloud.application.account.port.in.UpdateAccountUseCase;
import com.odcloud.application.account.port.in.command.UpdateAccountCommand;
import com.odcloud.application.account.port.out.AccountStoragePort;
import com.odcloud.application.file.port.out.FilePort;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.FileInfo;
import com.odcloud.infrastructure.constant.ProfileConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
class UpdateAccountService implements UpdateAccountUseCase {

    private final FilePort filePort;
    private final ProfileConstant constant;
    private final AccountStoragePort accountStoragePort;

    @Override
    @Transactional
    public UpdateAccountServiceResponse update(UpdateAccountCommand command) {
        Account account = accountStoragePort.findByEmail(command.account().getEmail());
        if (command.pictureFile() != null) {
            FileInfo file = FileInfo.ofProfilePicture(command.pictureFile());
            filePort.uploadFile(file);

            if (account.getPicture().startsWith(constant.webServerHost())) {
                filePort.deleteFile(account.getPicture().replace(constant.webServerHost(), ""));
            }
            account.updatePicture(constant.webServerHost() + file.getFileLoc());
        }

        if (StringUtils.hasText(command.nickname())) {
            account.updateNickname(command.nickname());
        }

        account.updateModDt();
        accountStoragePort.save(account);
        return UpdateAccountServiceResponse.ofSuccess();
    }
}
