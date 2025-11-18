package com.odcloud.adapter.in.update_account;

import com.odcloud.application.port.in.command.UpdateAccountCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.validation.FileType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class UpdateAccountRequest {

    private String nickname;
    @FileType(allowed = {"PNG", "JPG", "JPGE"},
        message = "프로필 사진은 이미지만 등록 가능 가능합니다")
    private MultipartFile pictureFile;

    UpdateAccountCommand toCommand(Account account) {
        return UpdateAccountCommand.builder()
            .account(account)
            .nickname(nickname)
            .pictureFile(pictureFile)
            .build();
    }
}
