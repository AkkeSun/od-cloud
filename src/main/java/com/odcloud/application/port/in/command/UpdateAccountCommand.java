package com.odcloud.application.port.in.command;

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
