package com.odcloud.adapter.in.controller.auth.callback_google_oauth2;

import com.odcloud.infrastructure.util.StringUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class CallbackGoogleOAuth2Request {

    @NotBlank(message = "구글 인증코드는 필수값 입니다")
    private String code;

    @Override
    public String toString() {
        return StringUtil.toString(this);
    }
}
