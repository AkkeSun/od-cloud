package com.odcloud.adapter.out.mail;

import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.GroupAccount;
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
    
    public static MailRequest ofGroupJoinRequest(Account requester, Group group) {
        return MailRequest.builder()
            .from("오디 클라우드 지원팀 <akkessun@gmail.com>")
            .subject("[OD Cloud] 그룹 가입 요청 확인 안내")
            .contents(String.format("<!DOCTYPE html>\n"
                    + "<html lang=\"ko\">\n"
                    + "  <head>\n"
                    + "    <meta charset=\"UTF-8\" />\n"
                    + "    <title>od-cloud 그룹 가입 요청 안내</title>\n"
                    + "  </head>\n"
                    + "  <body style=\"font-family: 'Apple SD Gothic Neo', sans-serif; background-color: #f8f9fa; margin: 0; padding: 0;\">\n"
                    + "    <div style=\"max-width: 600px; margin: 40px auto; background: white; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); padding: 30px;\">\n"
                    + "      <h2 style=\"color: #2c3e50; text-align: center;\">od-cloud 그룹 가입 요청 안내 ☁\uFE0F</h2>\n"
                    + "\n"
                    + "      <p style=\"font-size: 15px; color: #444; line-height: 1.6;\">\n"
                    + "        안녕하세요!<br />\n"
                    + "        <b>%s</b> 그룹에 대한 가입 요청이 도착했습니다.<br />\n"
                    + "      </p>\n"
                    + "\n"
                    + "      <hr style=\"margin: 25px 0; border: none; border-top: 1px solid #e0e0e0;\" />\n"
                    + "\n"
                    + "      <p style=\"font-size: 15px; color: #444; line-height: 1.6;\">\n"
                    + "        요청자 정보:<br />\n"
                    + "        이름: %s<br />\n"
                    + "        이메일: %s\n"
                    + "      </p>\n"
                    + "\n"
                    + "      <p style=\"font-size: 15px; color: #444; line-height: 1.6;\">\n"
                    + "        확인 후 그룹 가입 승인 여부를 결정해 주세요.\n"
                    + "      </p>\n"
                    + "\n"
                    + "      <p style=\"text-align:center; font-size:14px; color:#666; margin-top:30px;\">\n"
                    + "        감사합니다.<br />\n"
                    + "        <b>OD Cloud 팀 드림</b><br /><br />\n"
                    + "        <span style=\"font-size:12px; color:#aaa;\">본 메일은 발신 전용입니다. 문의는 고객센터를 이용해주세요.</span>\n"
                    + "      </p>\n"
                    + "    </div>\n"
                    + "  </body>\n"
                    + "</html>\n",
                group.getName(), requester.getName(), requester.getEmail()))
            .toList(List.of(group.getOwnerEmail()))
            .fileList(List.of())
            .build();
    }

    public static MailRequest ofGroupAccountStatusActive(GroupAccount groupAccount) {
        return MailRequest.builder()
            .from("오디 클라우드 지원팀 <akkessun@gmail.com>")
            .subject("[OD Cloud] 그룹 가입 승인 완료 안내")
            .contents(String.format("<!DOCTYPE html>\n"
                + "<html lang=\"ko\">\n"
                + "  <head>\n"
                + "    <meta charset=\"UTF-8\" />\n"
                + "    <title>od-cloud 그룹 가입 승인 안내</title>\n"
                + "  </head>\n"
                + "  <body style=\"font-family: 'Apple SD Gothic Neo', sans-serif; background-color: #f8f9fa; margin: 0; padding: 0;\">\n"
                + "    <div style=\"max-width: 600px; margin: 40px auto; background: white; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); padding: 30px;\">\n"
                + "      <h2 style=\"color: #2c3e50; text-align: center;\">od-cloud 그룹 가입 승인 완료 안내 ☁\uFE0F</h2>\n"
                + "\n"
                + "      <p style=\"font-size: 15px; color: #444; line-height: 1.6;\">\n"
                + "        안녕하세요, %s님!<br />\n"
                + "        <b>%s</b> 그룹의 가입 승인이 완료 되었습니다.<br />\n"
                + "        이제 그룹의 서비스를 이용하실 수 있습니다.\n"
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
                + "</html>\n", groupAccount.getName(), groupAccount.getGroupName()))
            .toList(List.of(groupAccount.getEmail()))
            .fileList(List.of())
            .build();
    }

}
