package com.odcloud.adapter.out.client;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.domain.model.Account;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SlackRequestTest {

    @Nested
    @DisplayName("[SlackRequest.ofCreateAccount] Account 정보로 Slack 메시지를 생성하는 정적 팩토리 메서드")
    class Describe_ofCreateAccount {

        @Test
        @DisplayName("[success] Account 정보로 Slack 메시지 요청을 생성한다")
        void success() {
            // given
            Account account = Account.builder()
                .username("testuser")
                .name("홍길동")
                .email("test@example.com")
                .password("password123")
                .role("ROLE_USER")
                .twoFactorSecret("SECRET")
                .isAdminApproved(false)
                .regDt(LocalDateTime.now())
                .build();

            // when
            SlackRequest request = SlackRequest.ofCreateAccount(account);

            // then
            assertThat(request).isNotNull();
            assertThat(request.channel()).isEqualTo("#monitoring");
            assertThat(request.text()).contains("신규 사용자 등록 안내");
            assertThat(request.text()).contains("계정: testuser");
            assertThat(request.text()).contains("이름: 홍길동");
            assertThat(request.text()).contains("이메일: test@example.com");
            assertThat(request.text()).contains("관리자 승인 처리가 필요합니다");
        }

        @Test
        @DisplayName("[success] 관리자 계정의 Slack 메시지를 생성한다")
        void success_adminAccount() {
            // given
            Account account = Account.builder()
                .username("adminuser")
                .name("김관리")
                .email("admin@example.com")
                .password("adminpass123")
                .role("ROLE_ADMIN")
                .twoFactorSecret("SECRET")
                .isAdminApproved(false)
                .regDt(LocalDateTime.now())
                .build();

            // when
            SlackRequest request = SlackRequest.ofCreateAccount(account);

            // then
            assertThat(request.channel()).isEqualTo("#monitoring");
            assertThat(request.text()).contains("계정: adminuser");
            assertThat(request.text()).contains("이름: 김관리");
            assertThat(request.text()).contains("이메일: admin@example.com");
        }

        @Test
        @DisplayName("[success] 메시지 포맷이 올바르게 구성된다")
        void success_messageFormat() {
            // given
            Account account = Account.builder()
                .username("newuser")
                .name("김철수")
                .email("newuser@example.com")
                .password("password123")
                .role("ROLE_USER")
                .twoFactorSecret("SECRET")
                .isAdminApproved(false)
                .regDt(LocalDateTime.now())
                .build();

            // when
            SlackRequest request = SlackRequest.ofCreateAccount(account);

            // then
            assertThat(request.text()).contains("[신규 사용자 등록 안내]");
            assertThat(request.text()).contains("- 계정:");
            assertThat(request.text()).contains("- 이름:");
            assertThat(request.text()).contains("- 이메일:");
            assertThat(request.text()).contains("관리자 승인 처리가 필요합니다.");
        }
    }

    @Nested
    @DisplayName("[SlackRequest.builder] SlackRequest 빌더 패턴 테스트")
    class Describe_builder {

        @Test
        @DisplayName("[success] 모든 필드를 설정하여 SlackRequest를 생성한다")
        void success() {
            // when
            SlackRequest request = SlackRequest.builder()
                .channel("#test-channel")
                .text("Test message content")
                .build();

            // then
            assertThat(request.channel()).isEqualTo("#test-channel");
            assertThat(request.text()).isEqualTo("Test message content");
        }

        @Test
        @DisplayName("[success] 다양한 채널로 메시지를 생성한다")
        void success_differentChannels() {
            // when
            SlackRequest request1 = SlackRequest.builder()
                .channel("#monitoring")
                .text("Monitoring message")
                .build();

            SlackRequest request2 = SlackRequest.builder()
                .channel("#alerts")
                .text("Alert message")
                .build();

            // then
            assertThat(request1.channel()).isEqualTo("#monitoring");
            assertThat(request2.channel()).isEqualTo("#alerts");
        }
    }
}