package com.odcloud.application.account.service.find_self_account;

import java.util.List;
import lombok.Builder;

@Builder
public record FindSelfAccountResponse(
    Long id,
    String email,
    String nickname,
    String picture,
    List<GroupInfo> groups,
    List<String> vouchers
) {

    public record GroupInfo(Long id, String name) {

    }
}
