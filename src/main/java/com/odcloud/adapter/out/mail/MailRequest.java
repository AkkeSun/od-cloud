package com.odcloud.adapter.out.mail;

import com.odcloud.domain.model.Account;
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

    public static MailRequest ofApprove(Account account) {
        return MailRequest.builder()
            .from("오디 클라우드 지원팀 <akkessun@gmail.com>")
            .subject("[OD Cloud] 신규 사용자 관리자 승인 안내")
            .contents(String.format("<!DOCTYPE html>\n"
                + "<html lang=\"ko\">\n"
                + "  <head>\n"
                + "    <meta charset=\"UTF-8\" />\n"
                + "    <title>od-cloud 관리자 승인 안내</title>\n"
                + "  </head>\n"
                + "  <body style=\"font-family: 'Apple SD Gothic Neo', sans-serif; background-color: #f8f9fa; margin: 0; padding: 0;\">\n"
                + "    <div style=\"max-width: 600px; margin: 40px auto; background: white; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); padding: 30px;\">\n"
                + "      <h2 style=\"color: #2c3e50; text-align: center;\">od-cloud 신규 사용자 승인 완료 안내 ☁\uFE0F</h2>\n"
                + "\n"
                + "      <p style=\"font-size: 15px; color: #444; line-height: 1.6;\">\n"
                + "        안녕하세요, %s님!<br />\n"
                + "        <b>od-cloud</b> 관리자 승인이 완료되었습니다.<br />\n"
                + "        이제 서비스에 로그인하여 다양한 기능을 이용하실 수 있습니다.\n"
                + "      </p>\n"
                + "\n"
                + "      <hr style=\"margin: 25px 0; border: none; border-top: 1px solid #e0e0e0;\" />\n"
                + "\n"
                + "      <p style=\"font-size: 15px; color: #444; line-height: 1.6;\">\n"
                + "        로그인 방법:<br />\n"
                + "        👉 <a href=\"https://cloud.odlab.kr/login\" target=\"_blank\" style=\"color:#1a73e8; text-decoration:none;\">OD Cloud 로그인 페이지</a>\n"
                + "      </p>\n"
                + "\n"
                + "      <p style=\"font-size: 15px; color: #444; line-height: 1.6;\">\n"
                + "        로그인 후에는 계정 정보 및 서비스 이용이 가능합니다.<br />\n"
                + "        문제가 발생하거나 문의사항이 있으면 고객센터로 연락해 주세요.\n"
                + "      </p>\n"
                + "\n"
                + "<br><br>"
                + "      <p style=\"text-align:center; font-size:14px; color:#666; margin-top:30px;\">\n"
                + "        감사합니다.<br />\n"
                + "        <b>OD Cloud 팀 드림</b><br /><br />\n"
                + "        <span style=\"font-size:12px; color:#aaa;\">본 메일은 발신 전용입니다. 문의는 고객센터를 이용해주세요.</span>\n"
                + "      </p>\n"
                + "    </div>\n"
                + "  </body>\n"
                + "</html>\n", account.getName()))
            .toList(List.of(account.getEmail()))
            .fileList(List.of())
            .build();
    }
}
