package com.odcloud.infrastructure.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.odcloud.infrastructure.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("[doFilterInternal] JWT 인증 필터 처리")
    class Describe_doFilterInternal {

        @Test
        @DisplayName("[success] 유효한 토큰으로 인증을 설정한다")
        void success_validToken() throws ServletException, IOException {
            // given
            String token = "Bearer valid.jwt.token";
            String email = "test@example.com";
            List<String> groups = List.of("group1", "group2");

            Claims claims = Jwts.claims().setSubject(email);
            claims.put("groups", groups);

            given(request.getHeader("Authorization")).willReturn(token);
            given(jwtUtil.validateTokenExceptExpiration(token)).willReturn(true);
            given(jwtUtil.getClaims(token)).willReturn(claims);

            // when
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // then
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            assertThat(authentication).isNotNull();
            assertThat(authentication.getPrincipal()).isEqualTo(email);
            assertThat(authentication.getAuthorities()).hasSize(2);
            assertThat(authentication.getAuthorities()).contains(
                new SimpleGrantedAuthority("ROLE_group1"),
                new SimpleGrantedAuthority("ROLE_group2")
            );

            verify(filterChain, times(1)).doFilter(request, response);
        }

        @Test
        @DisplayName("[success] 그룹이 없는 유효한 토큰으로 인증을 설정한다")
        void success_validTokenWithoutGroups() throws ServletException, IOException {
            // given
            String token = "Bearer valid.jwt.token";
            String email = "test@example.com";
            List<String> groups = List.of();

            Claims claims = Jwts.claims().setSubject(email);
            claims.put("groups", groups);

            given(request.getHeader("Authorization")).willReturn(token);
            given(jwtUtil.validateTokenExceptExpiration(token)).willReturn(true);
            given(jwtUtil.getClaims(token)).willReturn(claims);

            // when
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // then
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            assertThat(authentication).isNotNull();
            assertThat(authentication.getPrincipal()).isEqualTo(email);
            assertThat(authentication.getAuthorities()).isEmpty();

            verify(filterChain, times(1)).doFilter(request, response);
        }

        @Test
        @DisplayName("[success] 유효하지 않은 토큰은 인증을 설정하지 않는다")
        void success_invalidToken() throws ServletException, IOException {
            // given
            String token = "Bearer invalid.jwt.token";

            given(request.getHeader("Authorization")).willReturn(token);
            given(jwtUtil.validateTokenExceptExpiration(token)).willReturn(false);

            // when
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // then
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            assertThat(authentication).isNull();

            verify(filterChain, times(1)).doFilter(request, response);
        }

        @Test
        @DisplayName("[success] Authorization 헤더가 없으면 인증을 설정하지 않는다")
        void success_noAuthorizationHeader() throws ServletException, IOException {
            // given
            given(request.getHeader("Authorization")).willReturn(null);

            // when
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // then
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            assertThat(authentication).isNull();

            verify(filterChain, times(1)).doFilter(request, response);
        }

        @Test
        @DisplayName("[success] 토큰 검증 중 예외가 발생해도 필터 체인은 계속된다")
        void success_exceptionDuringValidation() throws ServletException, IOException {
            // given
            String token = "Bearer malformed.token";

            given(request.getHeader("Authorization")).willReturn(token);
            given(jwtUtil.validateTokenExceptExpiration(token)).willThrow(new RuntimeException("Token validation failed"));

            // when
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // then
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            assertThat(authentication).isNull();

            verify(filterChain, times(1)).doFilter(request, response);
        }

        @Test
        @DisplayName("[success] Claims 추출 중 예외가 발생해도 필터 체인은 계속된다")
        void success_exceptionDuringClaimsExtraction() throws ServletException, IOException {
            // given
            String token = "Bearer valid.token.format";

            given(request.getHeader("Authorization")).willReturn(token);
            given(jwtUtil.validateTokenExceptExpiration(token)).willReturn(true);
            given(jwtUtil.getClaims(token)).willThrow(new RuntimeException("Claims extraction failed"));

            // when
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // then
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            assertThat(authentication).isNull();

            verify(filterChain, times(1)).doFilter(request, response);
        }

        @Test
        @DisplayName("[success] 여러 그룹을 가진 토큰으로 권한을 설정한다")
        void success_multipleGroups() throws ServletException, IOException {
            // given
            String token = "Bearer valid.jwt.token";
            String email = "admin@example.com";
            List<String> groups = List.of("admin", "user", "manager");

            Claims claims = Jwts.claims().setSubject(email);
            claims.put("groups", groups);

            given(request.getHeader("Authorization")).willReturn(token);
            given(jwtUtil.validateTokenExceptExpiration(token)).willReturn(true);
            given(jwtUtil.getClaims(token)).willReturn(claims);

            // when
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // then
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            assertThat(authentication).isNotNull();
            assertThat(authentication.getAuthorities()).hasSize(3);
            assertThat(authentication.getAuthorities()).contains(
                new SimpleGrantedAuthority("ROLE_admin"),
                new SimpleGrantedAuthority("ROLE_user"),
                new SimpleGrantedAuthority("ROLE_manager")
            );

            verify(filterChain, times(1)).doFilter(request, response);
        }

        @Test
        @DisplayName("[success] Bearer 접두어가 있는 토큰을 처리한다")
        void success_tokenWithBearerPrefix() throws ServletException, IOException {
            // given
            String token = "Bearer eyJhbGciOiJIUzI1NiJ9.token.signature";
            String email = "test@example.com";

            Claims claims = Jwts.claims().setSubject(email);
            claims.put("groups", List.of("group1"));

            given(request.getHeader("Authorization")).willReturn(token);
            given(jwtUtil.validateTokenExceptExpiration(token)).willReturn(true);
            given(jwtUtil.getClaims(token)).willReturn(claims);

            // when
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // then
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            assertThat(authentication).isNotNull();
            assertThat(authentication.getPrincipal()).isEqualTo(email);

            verify(filterChain, times(1)).doFilter(request, response);
        }
    }
}
