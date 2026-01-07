package com.odcloud.application.auth.service.reissue_token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Group;
import com.odcloud.fakeClass.FakeAccountStoragePort;
import com.odcloud.fakeClass.FakeJwtUtil;
import com.odcloud.fakeClass.FakeProfileConstant;
import com.odcloud.fakeClass.FakeRedisStoragePort;
import com.odcloud.fakeClass.FakeUserAgentUtil;
import com.odcloud.infrastructure.constant.ProfileConstant;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ReissueTokenServiceTest {

    private FakeJwtUtil fakeJwtUtil;
    private FakeUserAgentUtil fakeUserAgentUtil;
    private ProfileConstant profileConstant;
    private FakeRedisStoragePort fakeRedisStoragePort;
    private FakeAccountStoragePort fakeAccountStoragePort;
    private ReissueTokenService reissueTokenService;

    @BeforeEach
    void setUp() {
        fakeJwtUtil = new FakeJwtUtil();
        fakeUserAgentUtil = new FakeUserAgentUtil();
        profileConstant = FakeProfileConstant.create();
        fakeRedisStoragePort = new FakeRedisStoragePort();
        fakeAccountStoragePort = new FakeAccountStoragePort();
        reissueTokenService = new ReissueTokenService(
            fakeJwtUtil,
            fakeUserAgentUtil,
            profileConstant,
            fakeRedisStoragePort,
            fakeAccountStoragePort
        );
    }

    @Nested
    @DisplayName("[reissueToken] 토큰 재발급")
    class Describe_reissueToken {

        @Test
        @DisplayName("[success] 정상적으로 토큰을 재발급한다")
        void success() {
            // given
            String email = "user@example.com";
            String refreshToken = "fake-refresh-token-" + email;

            Group group = Group.builder().id(1L).build();
            Account account = Account.builder()
                .id(1L)
                .email(email)
                .nickname("사용자")
                .groups(List.of(group))
                .build();
            fakeAccountStoragePort.database.add(account);

            String redisKey = String.format(
                profileConstant.redisKey().token(),
                email,
                fakeUserAgentUtil.getUserAgent()
            );
            fakeRedisStoragePort.database.put(redisKey, refreshToken);

            // when
            ReissueTokenServiceResponse response = reissueTokenService.reissueToken(refreshToken);

            // then
            assertThat(response).isNotNull();
            assertThat(response.accessToken()).isEqualTo("fake-access-token-" + email);
            assertThat(response.refreshToken()).isEqualTo("fake-refresh-token-" + email);
            assertThat(fakeRedisStoragePort.database.get(redisKey)).isEqualTo(
                response.refreshToken());
        }

        @Test
        @DisplayName("[failure] 유효하지 않은 Refresh Token으로 재발급 시도하면 예외가 발생한다")
        void failure_invalidToken() {
            // given
            fakeJwtUtil.shouldReturnInvalidToken = true;
            String invalidRefreshToken = "invalid-token";

            // when & then
            assertThatThrownBy(() -> reissueTokenService.reissueToken(invalidRefreshToken))
                .isInstanceOf(CustomAuthenticationException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_REFRESH_TOKEN);
        }

        @Test
        @DisplayName("[failure] Redis에 저장된 Refresh Token이 없으면 예외가 발생한다")
        void failure_noTokenInRedis() {
            // given
            String email = "user@example.com";
            String refreshToken = "fake-refresh-token-" + email;

            // when & then
            assertThatThrownBy(() -> reissueTokenService.reissueToken(refreshToken))
                .isInstanceOf(CustomAuthenticationException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_REFRESH_TOKEN);
        }

        @Test
        @DisplayName("[failure] Redis의 Refresh Token과 요청한 Token이 다르면 예외가 발생한다")
        void failure_tokenMismatch() {
            // given
            String email = "user@example.com";
            String refreshToken = "fake-refresh-token-" + email;
            String differentToken = "different-token";

            String redisKey = String.format(profileConstant.redisKey().token(),
                email, fakeUserAgentUtil.getUserAgent());
            fakeRedisStoragePort.database.put(redisKey, differentToken);

            // when & then
            assertThatThrownBy(() -> reissueTokenService.reissueToken(refreshToken))
                .isInstanceOf(CustomAuthenticationException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_REFRESH_TOKEN);
        }

        @Test
        @DisplayName("[success] 새로운 Refresh Token이 Redis에 업데이트된다")
        void success_newTokenUpdatedInRedis() {
            // given
            String email = "user@example.com";
            String oldRefreshToken = "fake-refresh-token-" + email;

            Group group = Group.builder().id(1L).build();
            Account account = Account.builder()
                .id(1L)
                .email(email)
                .nickname("사용자")
                .groups(List.of(group))
                .build();
            fakeAccountStoragePort.database.add(account);

            String redisKey = String.format(
                profileConstant.redisKey().token(),
                email,
                fakeUserAgentUtil.getUserAgent()
            );
            fakeRedisStoragePort.database.put(redisKey, oldRefreshToken);

            // when
            ReissueTokenServiceResponse response = reissueTokenService.reissueToken(
                oldRefreshToken);

            // then
            assertThat(fakeRedisStoragePort.database.get(redisKey)).isEqualTo(
                response.refreshToken());
        }

        @Test
        @DisplayName("[success] 빈 문자열이 아닌 유효한 토큰으로 재발급한다")
        void success_validNonEmptyToken() {
            // given
            String email = "user@example.com";
            String refreshToken = "fake-refresh-token-" + email;

            Group group = Group.builder().id(1L).build();
            Account account = Account.builder()
                .id(1L)
                .email(email)
                .nickname("사용자")
                .groups(List.of(group))
                .build();
            fakeAccountStoragePort.database.add(account);

            String redisKey = String.format(
                profileConstant.redisKey().token(),
                email,
                fakeUserAgentUtil.getUserAgent()
            );
            fakeRedisStoragePort.database.put(redisKey, refreshToken);

            // when
            ReissueTokenServiceResponse response = reissueTokenService.reissueToken(refreshToken);

            // then
            assertThat(response).isNotNull();
            assertThat(response.accessToken()).isNotEmpty();
            assertThat(response.refreshToken()).isNotEmpty();
        }

        @Test
        @DisplayName("[success] 다른 User Agent에서는 다른 Redis Key를 사용한다")
        void success_differentUserAgentDifferentKey() {
            // given
            String email = "user@example.com";
            String refreshToken1 = "fake-refresh-token-" + email;

            Group group = Group.builder().id(1L).build();
            Account account = Account.builder()
                .id(1L)
                .email(email)
                .nickname("사용자")
                .groups(List.of(group))
                .build();
            fakeAccountStoragePort.database.add(account);

            // First user agent
            String redisKey1 = String.format(
                profileConstant.redisKey().token(),
                email,
                fakeUserAgentUtil.getUserAgent()
            );
            fakeRedisStoragePort.database.put(redisKey1, refreshToken1);

            ReissueTokenServiceResponse response1 = reissueTokenService.reissueToken(refreshToken1);

            // Change user agent
            fakeUserAgentUtil.mockUserAgent = "Chrome/120.0";
            String refreshToken2 = "fake-refresh-token-" + email;
            String redisKey2 = String.format(
                profileConstant.redisKey().token(),
                email,
                fakeUserAgentUtil.getUserAgent()
            );
            fakeRedisStoragePort.database.put(redisKey2, refreshToken2);

            ReissueTokenServiceResponse response2 = reissueTokenService.reissueToken(refreshToken2);

            // then
            assertThat(response1).isNotNull();
            assertThat(response2).isNotNull();
            assertThat(fakeRedisStoragePort.database).hasSize(2);
        }

        @Test
        @DisplayName("[failure] 빈 문자열 토큰으로 재발급 시도하면 예외가 발생한다")
        void failure_emptyToken() {
            // given
            String emptyToken = "";

            // when & then
            assertThatThrownBy(() -> reissueTokenService.reissueToken(emptyToken))
                .isInstanceOf(CustomAuthenticationException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }
}
