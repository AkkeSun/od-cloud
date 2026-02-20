package com.odcloud.infrastructure.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Group;
import com.odcloud.infrastructure.constant.ProfileConstant;
import com.odcloud.infrastructure.constant.ProfileConstant.Jwt;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import com.odcloud.infrastructure.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JwtUtilImplTest {

    @Mock
    private HttpServletRequest request;
    private JwtUtilImpl jwtUtil;
    private ProfileConstant profileConstant;

    @BeforeEach
    void setUp() {
        Jwt jwt = Jwt.builder()
            .tempTokenTtl(300000L)
            .accessTokenTtl(1800000L)
            .refreshTokenTtl(604800000L)
            .secretKey("test-secret-key-for-jwt-token")
            .build();

        profileConstant = ProfileConstant.builder()
            .jwt(jwt)
            .aesSecretKey("test-aes-key")
            .build();

        jwtUtil = new JwtUtilImpl(profileConstant);
    }

    @Nested
    @DisplayName("[createAccessToken] Access Token 생성")
    class Describe_createAccessToken {

        @Test
        @DisplayName("[success] 정상적으로 Access Token을 생성한다")
        void success() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("test@example.com")
                .groups(List.of(
                    Group.builder()
                        .id(1L)
                        .name("그룹1")
                        .build(),
                    Group.builder()
                        .id(2L)
                        .name("그룹2")
                        .build()))
                .build();

            // when
            String accessToken = jwtUtil.createAccessToken(account);

            // then
            assertThat(accessToken).isNotNull();
            assertThat(accessToken).startsWith("Bearer ");

            String tokenWithoutBearer = accessToken.replace("Bearer ", "");
            Claims claims = Jwts.parser()
                .setSigningKey(profileConstant.getJwtSecretKey())
                .parseClaimsJws(tokenWithoutBearer)
                .getBody();

            assertThat(claims.getSubject()).isEqualTo("test@example.com");
            assertThat(claims.get("id", Long.class)).isEqualTo(1L);
            List<java.util.Map<String, Object>> groups = claims.get("groups", List.class);
            assertThat(groups).hasSize(2);
            assertThat(((Number) groups.get(0).get("id")).longValue()).isEqualTo(1L);
            assertThat(groups.get(0).get("name")).isEqualTo("그룹1");
            assertThat(((Number) groups.get(1).get("id")).longValue()).isEqualTo(2L);
            assertThat(groups.get(1).get("name")).isEqualTo("그룹2");
        }

        @Test
        @DisplayName("[success] 그룹이 없는 계정으로 Access Token을 생성한다")
        void success_noGroups() {
            // given
            Account account = Account.builder()
                .id(2L)
                .email("nogroup@example.com")
                .groups(List.of())
                .build();

            // when
            String accessToken = jwtUtil.createAccessToken(account);

            // then
            assertThat(accessToken).isNotNull();
            assertThat(accessToken).startsWith("Bearer ");

            String tokenWithoutBearer = accessToken.replace("Bearer ", "");
            Claims claims = Jwts.parser()
                .setSigningKey(profileConstant.getJwtSecretKey())
                .parseClaimsJws(tokenWithoutBearer)
                .getBody();

            assertThat(claims.getSubject()).isEqualTo("nogroup@example.com");
            assertThat(claims.get("groups", List.class)).isEmpty();
        }
    }

    @Nested
    @DisplayName("[createRefreshToken] Refresh Token 생성")
    class Describe_createRefreshToken {

        @Test
        @DisplayName("[success] 정상적으로 Refresh Token을 생성한다")
        void success() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("test@example.com")
                .build();
            String deviceId = "device-abc123";

            // when
            String refreshToken = jwtUtil.createRefreshToken(account, deviceId);

            // then
            assertThat(refreshToken).isNotNull();
            assertThat(refreshToken).startsWith("Bearer ");

            String tokenWithoutBearer = refreshToken.replace("Bearer ", "");
            Claims claims = Jwts.parser()
                .setSigningKey(profileConstant.getJwtSecretKey())
                .parseClaimsJws(tokenWithoutBearer)
                .getBody();

            assertThat(claims.getSubject()).isEqualTo("test@example.com");
            assertThat(claims.get("deviceId")).isEqualTo(deviceId);
            assertThat(claims.get("id")).isNull();
            assertThat(claims.get("groups")).isNull();
        }
    }

    @Nested
    @DisplayName("[getDeviceId] 토큰에서 deviceId 추출")
    class Describe_getDeviceId {

        @Test
        @DisplayName("[success] 정상적으로 deviceId를 추출한다")
        void success() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("test@example.com")
                .build();
            String deviceId = "device-abc123";
            String refreshToken = jwtUtil.createRefreshToken(account, deviceId);

            // when
            String extractedDeviceId = jwtUtil.getDeviceId(refreshToken);

            // then
            assertThat(extractedDeviceId).isEqualTo(deviceId);
        }

        @Test
        @DisplayName("[failure] 유효하지 않은 토큰으로 deviceId 추출 시도하면 예외가 발생한다")
        void failure_invalidToken() {
            // given
            String invalidToken = "Bearer invalid.token.here";

            // when & then
            assertThatThrownBy(() -> jwtUtil.getDeviceId(invalidToken))
                .isInstanceOf(CustomAuthenticationException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_ACCESS_TOKEN);
        }
    }

    @Nested
    @DisplayName("[getEmail] 토큰에서 이메일 추출")
    class Describe_getEmail {

        @Test
        @DisplayName("[success] 정상적으로 이메일을 추출한다")
        void success() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("test@example.com")
                .groups(List.of(Group.builder()
                    .id(1L)
                    .name("그룹1")
                    .build()))
                .build();
            String token = jwtUtil.createAccessToken(account);

            // when
            String email = jwtUtil.getEmail(token);

            // then
            assertThat(email).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("[failure] 유효하지 않은 토큰으로 이메일 추출 시도하면 예외가 발생한다")
        void failure_invalidToken() {
            // given
            String invalidToken = "Bearer invalid.token.here";

            // when & then
            assertThatThrownBy(() -> jwtUtil.getEmail(invalidToken))
                .isInstanceOf(CustomAuthenticationException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_ACCESS_TOKEN);
        }
    }

    @Nested
    @DisplayName("[validateTokenExceptExpiration] 토큰 유효성 검증")
    class Describe_validateTokenExceptExpiration {

        @Test
        @DisplayName("[success] 유효한 토큰은 true를 반환한다")
        void success_validToken() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("test@example.com")
                .groups(List.of(Group.builder()
                    .id(1L)
                    .name("그룹1")
                    .build()))
                .build();
            String token = jwtUtil.createAccessToken(account);

            // when
            boolean isValid = jwtUtil.validateTokenExceptExpiration(token);

            // then
            assertThat(isValid).isTrue();
        }

        @Test
        @DisplayName("[failure] 유효하지 않은 토큰은 false를 반환한다")
        void failure_invalidToken() {
            // given
            String invalidToken = "Bearer invalid.token.here";

            // when
            boolean isValid = jwtUtil.validateTokenExceptExpiration(invalidToken);

            // then
            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("[failure] null 토큰은 false를 반환한다")
        void failure_nullToken() {
            // when
            boolean isValid = jwtUtil.validateTokenExceptExpiration(null);

            // then
            assertThat(isValid).isFalse();
        }
    }

    @Nested
    @DisplayName("[getClaims] 토큰에서 Claims 추출")
    class Describe_getClaims {

        @Test
        @DisplayName("[success] Bearer 접두어가 있는 토큰에서 Claims를 추출한다")
        void success_withBearer() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("test@example.com")
                .groups(List.of(Group.builder()
                    .id(1L)
                    .name("그룹1")
                    .build()))
                .build();
            String token = jwtUtil.createAccessToken(account);

            // when
            Claims claims = jwtUtil.getClaims(token);

            // then
            assertThat(claims.getSubject()).isEqualTo("test@example.com");
            assertThat(claims.get("id", Long.class)).isEqualTo(1L);
            List<java.util.Map<String, Object>> groups = claims.get("groups", List.class);
            assertThat(groups).hasSize(1);
            assertThat(((Number) groups.get(0).get("id")).longValue()).isEqualTo(1L);
            assertThat(groups.get(0).get("name")).isEqualTo("그룹1");
        }

        @Test
        @DisplayName("[success] Bearer 접두어가 없는 토큰에서도 Claims를 추출한다")
        void success_withoutBearer() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("test@example.com")
                .groups(List.of(Group.builder()
                    .id(1L)
                    .name("그룹1")
                    .build()))
                .build();
            String tokenWithBearer = jwtUtil.createAccessToken(account);
            String tokenWithoutBearer = tokenWithBearer.replace("Bearer ", "");

            // when
            Claims claims = jwtUtil.getClaims(tokenWithoutBearer);

            // then
            assertThat(claims.getSubject()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("[failure] 유효하지 않은 토큰으로 Claims 추출 시도하면 예외가 발생한다")
        void failure_invalidToken() {
            // given
            String invalidToken = "Bearer invalid.token.here";

            // when & then
            assertThatThrownBy(() -> jwtUtil.getClaims(invalidToken))
                .isInstanceOf(CustomAuthenticationException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_ACCESS_TOKEN);
        }
    }

    @Nested
    @DisplayName("[getAccountInfo] 요청에서 계정 정보 추출")
    class Describe_getAccountInfo {

        @Test
        @DisplayName("[success] Authorization 헤더에서 계정 정보를 추출한다")
        void success() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("test@example.com")
                .groups(List.of(Group.builder()
                    .id(1L)
                    .name("그룹1")
                    .build()))
                .build();
            String token = jwtUtil.createAccessToken(account);

            given(request.getHeader("Authorization")).willReturn(token);

            // when
            var accountInfo = jwtUtil.getAccountInfo(request);

            // then
            assertThat(accountInfo.has("email")).isTrue();
            assertThat(accountInfo.get("email").asText()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("[success] Authorization 헤더가 없으면 빈 ObjectNode를 반환한다")
        void success_noAuthorizationHeader() {
            // given
            given(request.getHeader("Authorization")).willReturn(null);

            // when
            var accountInfo = jwtUtil.getAccountInfo(request);

            // then
            assertThat(accountInfo.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("[success] 유효하지 않은 토큰이면 빈 ObjectNode를 반환한다")
        void success_invalidToken() {
            // given
            given(request.getHeader("Authorization")).willReturn("Bearer invalid.token");

            // when
            var accountInfo = jwtUtil.getAccountInfo(request);

            // then
            assertThat(accountInfo.isEmpty()).isTrue();
        }
    }
}
