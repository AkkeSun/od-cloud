package com.odcloud.adapter.out.mail;

import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.util.GoogleOTPUtil;
import java.util.List;
import lombok.Builder;

@Builder
public record MailRequest(
    String from,
    String subject,
    String contents,
    List<String> toList,
    List<Attachment> fileList
) {

    public static MailRequest ofCreateUser(Account account) {
        return MailRequest.builder()
            .from("오디 클라우드 지원팀 <akkessun@gmail.com>")
            .subject("[od-cloud] 신규 사용자 등록을 위한 구글 OTP 연동 안내")
            .contents(String.format("<!DOCTYPE html>\n"
                + "<html lang=\"ko\">\n"
                + "  <head>\n"
                + "    <meta charset=\"UTF-8\" />\n"
                + "    <title>od-cloud OTP 연동 안내</title>\n"
                + "  </head>\n"
                + "  <body style=\"font-family: 'Apple SD Gothic Neo', sans-serif; background-color: #f8f9fa; margin: 0; padding: 0;\">\n"
                + "    <div style=\"max-width: 600px; margin: 40px auto; background: white; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); padding: 30px;\">\n"
                + "      <h2 style=\"color: #2c3e50; text-align: center;\">od-cloud 에 오신 것을 환영합니다 ☁\uFE0F</h2>\n"
                + "\n"
                + "      <p style=\"font-size: 15px; color: #444; line-height: 1.6;\">\n"
                + "        안녕하세요!<br />\n"
                + "        <b>od-cloud</b> 서비스를 이용해주셔서 감사합니다.<br />\n"
                + "        보안을 위해 <b>Google OTP</b> 연동이 필요합니다.<br />\n"
                + "        Google OTP는 로그인 시 사용하는 <b>1회용 비밀번호(One-Time Password)</b>를 의미합니다.\n"
                + "      </p>\n"
                + "\n"
                + "      <hr style=\"margin: 25px 0; border: none; border-top: 1px solid #e0e0e0;\" />\n"
                + "\n"
                + "      <h3 style=\"color: #2c3e50;\">\uD83D\uDD10 Google OTP 연동 가이드</h3>\n"
                + "      <ol style=\"font-size: 15px; color: #444; line-height: 1.8; padding-left: 20px;\">\n"
                + "        <li>\n"
                + "          아래 링크를 통해 Google Authenticator 앱을 다운로드합니다.<br />\n"
                + "          ▪\uFE0F <a href=\"https://play.google.com/store/apps/details?id=com.google.android.apps.authenticator2\" target=\"_blank\">Android용 Google OTP 앱</a><br />\n"
                + "          ▪\uFE0F <a href=\"https://apps.apple.com/kr/app/google-authenticator/id388497605\" target=\"_blank\">iPhone용 Google OTP 앱</a>\n"
                + "        </li>\n"
                + "        <li>\n"
                + "          앱을 실행한 후, 화면 하단의 <b>‘+’ 버튼</b>을 클릭하고<br />\n"
                + "          <b>‘QR 코드 스캔’</b>을 선택합니다.\n"
                + "        </li>\n"
                + "        <li>\n"
                + "          아래 QR 코드를 스캔합니다.<br /><br />\n"
                + "          <img src=\"%s\" alt=\"OTP QR 코드\" style=\"width:200px; border:1px solid #ddd; border-radius:8px;\" />\n"
                + "        </li>\n"
                + "      </ol>\n"
                + "\n"
                + "      <hr style=\"margin: 25px 0; border: none; border-top: 1px solid #e0e0e0;\" />\n"
                + "\n"
                + "      <p style=\"font-size: 15px; color: #444; line-height: 1.6;\">\n"
                + "        QR 코드 등록이 완료되면, <b>od-cloud</b>를 자유롭게 이용하실 수 있습니다. 🎉<br/>\n"
                + "        👉 <a href=\"https://od-cloud.example.com\" target=\"_blank\" style=\"color:#1a73e8; text-decoration:none;\">od-cloud 바로가기</a>\n"
                + "      </p>\n"
                + "\n"
                + "<br><br>"
                + "      <p style=\"text-align:center; font-size:14px; color:#666; margin-top:30px;\">\n"
                + "        감사합니다.<br />\n"
                + "        <b>od-cloud 팀 드림</b><br /><br />\n"
                + "        <span style=\"font-size:12px; color:#aaa;\">본 메일은 발신 전용입니다. 문의는 고객센터를 이용해주세요.</span>\n"
                + "      </p>\n"
                + "    </div>\n"
                + "  </body>\n"
                + "</html>\n", GoogleOTPUtil.getOtpAuthUrl(account)))
            .toList(List.of(account.getEmail()))
            .fileList(List.of())
            .build();
    }
}
