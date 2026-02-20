package com.odcloud.application.auth.service.issue_token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.adapter.out.client.google.GoogleUserInfoResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Group;
import com.odcloud.fakeClass.FakeAccountStoragePort;
import com.odcloud.fakeClass.FakeGoogleOAuth2Port;
import com.odcloud.fakeClass.FakeJwtUtil;
import com.odcloud.fakeClass.FakeProfileConstant;
import com.odcloud.fakeClass.FakeRedisStoragePort;
import com.odcloud.fakeClass.FakeVoucherStoragePort;
import com.odcloud.infrastructure.constant.ProfileConstant;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class IssueTokenServiceTest {

    private FakeJwtUtil fakeJwtUtil;
    private ProfileConstant profileConstant;
    private FakeGoogleOAuth2Port fakeGoogleOAuth2Port;
    private FakeRedisStoragePort fakeRedisStoragePort;
    private FakeAccountStoragePort fakeAccountStoragePort;
    private FakeVoucherStoragePort fakeVoucherStoragePort;
    private IssueTokenService issueTokenService;

    @BeforeEach
    void setUp() {
        fakeJwtUtil = new FakeJwtUtil();
        profileConstant = FakeProfileConstant.create();
        fakeGoogleOAuth2Port = new FakeGoogleOAuth2Port();
        fakeRedisStoragePort = new FakeRedisStoragePort();
        fakeAccountStoragePort = new FakeAccountStoragePort();
        fakeVoucherStoragePort = new FakeVoucherStoragePort();
        issueTokenService = new IssueTokenService(
            fakeJwtUtil,
            profileConstant,
            fakeGoogleOAuth2Port,
            fakeRedisStoragePort,
            fakeAccountStoragePort,
            fakeVoucherStoragePort
        );
    }

    @Nested
    @DisplayName("[issue] 토큰 발급")
    class Describe_issue {

        @Test
        @DisplayName("[success] 정상적으로 토큰을 발급한다")
        void success() {
            // given
            GoogleUserInfoResponse userInfo = GoogleUserInfoResponse.builder()
                .email("user@example.com")
                .name("사용자")
                .picture("https://example.com/photo.jpg")
                .build();
            fakeGoogleOAuth2Port.mockUserInfoResponse = userInfo;

            Group group = Group.builder()
                .id(1L)
                .build();

            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .nickname("사용자")
                .groups(List.of(group))
                .build();
            fakeAccountStoragePort.database.add(account);

            String googleAuthorization = "Bearer test-google-token";
            String deviceId = "device-abc123";

            // when
            IssueTokenServiceResponse response = issueTokenService.issue(googleAuthorization, deviceId);

            // then
            assertThat(response).isNotNull();
            assertThat(response.accessToken()).isEqualTo("fake-access-token-user@example.com");
            assertThat(response.refreshToken()).isEqualTo("fake-refresh-token-user@example.com");
            assertThat(fakeRedisStoragePort.database).hasSize(1);
        }

        @Test
        @DisplayName("[failure] 존재하지 않는 계정은 토큰 발급에 실패한다")
        void failure_nonExistentAccount() {
            // given
            GoogleUserInfoResponse userInfo = GoogleUserInfoResponse.builder()
                .email("nonexistent@example.com")
                .name("없는 사용자")
                .build();
            fakeGoogleOAuth2Port.mockUserInfoResponse = userInfo;

            String googleAuthorization = "Bearer test-google-token";
            String deviceId = "device-abc123";

            // when & then
            assertThatThrownBy(() -> issueTokenService.issue(googleAuthorization, deviceId))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_NOT_FOUND_ACCOUNT);

            assertThat(fakeRedisStoragePort.database).isEmpty();
        }

        @Test
        @DisplayName("[failure] Google OAuth2에서 사용자 정보 조회 실패 시 예외가 발생한다")
        void failure_googleOAuth2Error() {
            // given
            fakeGoogleOAuth2Port.shouldThrowExceptionOnGetUserInfo = true;
            String googleAuthorization = "Bearer invalid-token";
            String deviceId = "device-abc123";

            // when & then
            assertThatThrownBy(() -> issueTokenService.issue(googleAuthorization, deviceId))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode",
                    ErrorCode.Business_GOOGLE_USER_INFO_ERROR);

            assertThat(fakeRedisStoragePort.database).isEmpty();
        }

        @Test
        @DisplayName("[success] Refresh Token이 Redis에 deviceId 키로 정상적으로 저장된다")
        void success_refreshTokenStoredInRedis() {
            // given
            GoogleUserInfoResponse userInfo = GoogleUserInfoResponse.builder()
                .email("user@example.com")
                .name("사용자")
                .build();
            fakeGoogleOAuth2Port.mockUserInfoResponse = userInfo;

            Group group = Group.builder()
                .id(1L)
                .build();

            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .nickname("사용자")
                .groups(List.of(group))
                .build();
            fakeAccountStoragePort.database.add(account);

            String googleAuthorization = "Bearer test-google-token";
            String deviceId = "device-abc123";

            // when
            IssueTokenServiceResponse response = issueTokenService.issue(googleAuthorization, deviceId);

            // then
            String expectedRedisKey = String.format(
                profileConstant.redisKey().token(),
                "user@example.com",
                deviceId
            );
            assertThat(fakeRedisStoragePort.database).containsKey(expectedRedisKey);
            assertThat(fakeRedisStoragePort.database.get(expectedRedisKey))
                .isEqualTo(response.refreshToken());
        }

        @Test
        @DisplayName("[success] 여러 그룹에 속한 계정도 토큰을 발급받을 수 있다")
        void success_multipleGroups() {
            // given
            GoogleUserInfoResponse userInfo = GoogleUserInfoResponse.builder()
                .email("user@example.com")
                .name("사용자")
                .build();
            fakeGoogleOAuth2Port.mockUserInfoResponse = userInfo;

            Group group1 = Group.builder().id(1L).build();
            Group group2 = Group.builder().id(2L).build();
            Group group3 = Group.builder().id(3L).build();

            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .nickname("사용자")
                .groups(List.of(group1, group2, group3))
                .build();
            fakeAccountStoragePort.database.add(account);

            String googleAuthorization = "Bearer test-google-token";
            String deviceId = "device-abc123";

            // when
            IssueTokenServiceResponse response = issueTokenService.issue(googleAuthorization, deviceId);

            // then
            assertThat(response).isNotNull();
            assertThat(response.accessToken()).isNotNull();
            assertThat(response.refreshToken()).isNotNull();
        }
    }
}
