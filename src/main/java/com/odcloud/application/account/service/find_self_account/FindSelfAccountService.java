package com.odcloud.application.account.service.find_self_account;

import com.odcloud.application.account.port.in.FindSelfAccountUseCase;
import com.odcloud.application.account.service.find_self_account.FindSelfAccountResponse.GroupInfo;
import com.odcloud.domain.model.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class FindSelfAccountService implements FindSelfAccountUseCase {

    @Override
    public FindSelfAccountResponse findSelf(Account account) {
        return FindSelfAccountResponse.builder()
            .id(account.getId())
            .email(account.getEmail())
            .nickname(account.getNickname())
            .picture(account.getPicture())
            .groups(account.getGroups().stream()
                .map(group -> new GroupInfo(group.getId(), group.getName()))
                .toList())
            .vouchers(account.getVouchersInfo())
            .build();
    }
}
