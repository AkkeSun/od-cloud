package com.odcloud.application.account.service.update_account;

import com.odcloud.domain.model.Account;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record UpdateAccountCommand(
    Account account,
    String nickname,
    MultipartFile pictureFile
) {

}
