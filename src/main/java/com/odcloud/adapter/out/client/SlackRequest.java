package com.odcloud.adapter.out.client;

import com.odcloud.domain.model.Account;
import lombok.Builder;

@Builder
public record SlackRequest(
    String text,
    String channel
) {

    public static SlackRequest ofCreateAccount(Account account) {
        return SlackRequest.builder()
            .channel("#monitoring")
            .text(String.format("""
                [신규 사용자 등록 안내]
                
                - 계정: %s
                - 이름: %s
                - 이메일: %s
                
                관리자 승인 처리가 필요합니다.
                """, account.getUsername(), account.getName(), account.getEmail()))
            .build();
    }
}