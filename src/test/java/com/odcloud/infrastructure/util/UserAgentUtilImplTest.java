package com.odcloud.infrastructure.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserAgentUtilImplTest {

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private UserAgentUtilImpl userAgentUtil;

    @Nested
    @DisplayName("[getUserAgent] User-Agent 헤더 추출")
    class Describe_getUserAgent {

        @Test
        @DisplayName("[success] 정상적으로 User-Agent 헤더를 추출한다")
        void success() {
            // given
            String expectedUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";
            given(request.getHeader("User-Agent")).willReturn(expectedUserAgent);

            // when
            String userAgent = userAgentUtil.getUserAgent();

            // then
            assertThat(userAgent).isEqualTo(expectedUserAgent);
        }

        @Test
        @DisplayName("[success] Chrome 브라우저의 User-Agent를 추출한다")
        void success_chrome() {
            // given
            String chromeUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
            given(request.getHeader("User-Agent")).willReturn(chromeUserAgent);

            // when
            String userAgent = userAgentUtil.getUserAgent();

            // then
            assertThat(userAgent).isEqualTo(chromeUserAgent);
            assertThat(userAgent).contains("Chrome");
        }

        @Test
        @DisplayName("[success] Firefox 브라우저의 User-Agent를 추출한다")
        void success_firefox() {
            // given
            String firefoxUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:89.0) Gecko/20100101 Firefox/89.0";
            given(request.getHeader("User-Agent")).willReturn(firefoxUserAgent);

            // when
            String userAgent = userAgentUtil.getUserAgent();

            // then
            assertThat(userAgent).isEqualTo(firefoxUserAgent);
            assertThat(userAgent).contains("Firefox");
        }

        @Test
        @DisplayName("[success] Safari 브라우저의 User-Agent를 추출한다")
        void success_safari() {
            // given
            String safariUserAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.1.1 Safari/605.1.15";
            given(request.getHeader("User-Agent")).willReturn(safariUserAgent);

            // when
            String userAgent = userAgentUtil.getUserAgent();

            // then
            assertThat(userAgent).isEqualTo(safariUserAgent);
            assertThat(userAgent).contains("Safari");
        }

        @Test
        @DisplayName("[success] 모바일 기기의 User-Agent를 추출한다")
        void success_mobile() {
            // given
            String mobileUserAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 14_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Mobile/15E148 Safari/604.1";
            given(request.getHeader("User-Agent")).willReturn(mobileUserAgent);

            // when
            String userAgent = userAgentUtil.getUserAgent();

            // then
            assertThat(userAgent).isEqualTo(mobileUserAgent);
            assertThat(userAgent).contains("Mobile");
        }

        @Test
        @DisplayName("[success] User-Agent 헤더가 없으면 null을 반환한다")
        void success_noUserAgent() {
            // given
            given(request.getHeader("User-Agent")).willReturn(null);

            // when
            String userAgent = userAgentUtil.getUserAgent();

            // then
            assertThat(userAgent).isNull();
        }

        @Test
        @DisplayName("[success] User-Agent 헤더가 빈 문자열이면 빈 문자열을 반환한다")
        void success_emptyUserAgent() {
            // given
            given(request.getHeader("User-Agent")).willReturn("");

            // when
            String userAgent = userAgentUtil.getUserAgent();

            // then
            assertThat(userAgent).isEmpty();
        }

        @Test
        @DisplayName("[success] API 클라이언트의 User-Agent를 추출한다")
        void success_apiClient() {
            // given
            String apiClientUserAgent = "PostmanRuntime/7.28.4";
            given(request.getHeader("User-Agent")).willReturn(apiClientUserAgent);

            // when
            String userAgent = userAgentUtil.getUserAgent();

            // then
            assertThat(userAgent).isEqualTo(apiClientUserAgent);
            assertThat(userAgent).contains("Postman");
        }

        @Test
        @DisplayName("[success] 커스텀 User-Agent를 추출한다")
        void success_customUserAgent() {
            // given
            String customUserAgent = "MyCustomClient/1.0.0";
            given(request.getHeader("User-Agent")).willReturn(customUserAgent);

            // when
            String userAgent = userAgentUtil.getUserAgent();

            // then
            assertThat(userAgent).isEqualTo(customUserAgent);
        }
    }
}
