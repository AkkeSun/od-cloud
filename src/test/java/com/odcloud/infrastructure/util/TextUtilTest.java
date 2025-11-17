package com.odcloud.infrastructure.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TextUtilTest {

    @Nested
    @DisplayName("[truncateTextLimit] 텍스트 길이 제한")
    class Describe_truncateTextLimit {

        @Test
        @DisplayName("[success] 65533 바이트 이하의 텍스트는 그대로 반환한다")
        void success_shortText() {
            // given
            String shortText = "Hello World";

            // when
            String result = TextUtil.truncateTextLimit(shortText);

            // then
            assertThat(result).isEqualTo(shortText);
        }

        @Test
        @DisplayName("[success] 빈 문자열은 그대로 반환한다")
        void success_emptyString() {
            // given
            String emptyText = "";

            // when
            String result = TextUtil.truncateTextLimit(emptyText);

            // then
            assertThat(result).isEqualTo(emptyText);
        }

        @Test
        @DisplayName("[success] 한글 텍스트가 65533 바이트 이하이면 그대로 반환한다")
        void success_koreanText() {
            // given
            String koreanText = "안녕하세요 반갑습니다";

            // when
            String result = TextUtil.truncateTextLimit(koreanText);

            // then
            assertThat(result).isEqualTo(koreanText);
        }

        @Test
        @DisplayName("[success] 65535 바이트를 초과하는 ASCII 텍스트를 잘라낸다")
        void success_truncateLongAsciiText() {
            // given
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 70000; i++) {
                sb.append("a");
            }
            String longText = sb.toString();

            // when
            String result = TextUtil.truncateTextLimit(longText);

            // then
            int resultByteSize = result.getBytes(StandardCharsets.UTF_8).length;
            assertThat(resultByteSize).isLessThanOrEqualTo(65535);
            assertThat(result.length()).isLessThan(longText.length());
        }

        @Test
        @DisplayName("[success] 65535 바이트를 초과하는 한글 텍스트를 잘라낸다")
        void success_truncateLongKoreanText() {
            // given
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 25000; i++) {
                sb.append("한");
            }
            String longText = sb.toString();

            // when
            String result = TextUtil.truncateTextLimit(longText);

            // then
            int resultByteSize = result.getBytes(StandardCharsets.UTF_8).length;
            assertThat(resultByteSize).isLessThanOrEqualTo(65535);
            assertThat(result.length()).isLessThan(longText.length());
        }

        @Test
        @DisplayName("[success] 65535 바이트를 초과하는 혼합 텍스트를 잘라낸다")
        void success_truncateMixedText() {
            // given
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 10000; i++) {
                sb.append("Hello안녕");
            }
            String longText = sb.toString();

            // when
            String result = TextUtil.truncateTextLimit(longText);

            // then
            int resultByteSize = result.getBytes(StandardCharsets.UTF_8).length;
            assertThat(resultByteSize).isLessThanOrEqualTo(65535);
            assertThat(result.length()).isLessThan(longText.length());
        }

        @Test
        @DisplayName("[success] 특수 문자를 포함한 긴 텍스트를 잘라낸다")
        void success_truncateTextWithSpecialCharacters() {
            // given
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 30000; i++) {
                sb.append("@#$");
            }
            String longText = sb.toString();

            // when
            String result = TextUtil.truncateTextLimit(longText);

            // then
            int resultByteSize = result.getBytes(StandardCharsets.UTF_8).length;
            assertThat(resultByteSize).isLessThanOrEqualTo(65535);
        }

        @Test
        @DisplayName("[success] 65535 바이트 근처의 텍스트를 정확히 잘라낸다")
        void success_exactBoundary() {
            // given
            StringBuilder sb = new StringBuilder();
            int targetSize = 65530;
            for (int i = 0; i < targetSize; i++) {
                sb.append("a");
            }
            String boundaryText = sb.toString();

            // when
            String result = TextUtil.truncateTextLimit(boundaryText);

            // then
            int resultByteSize = result.getBytes(StandardCharsets.UTF_8).length;
            assertThat(resultByteSize).isLessThanOrEqualTo(65535);
        }


        @Test
        @DisplayName("[success] 정확히 65533 바이트인 텍스트는 그대로 반환한다")
        void success_exactly65533Bytes() {
            // given
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 65533; i++) {
                sb.append("a");
            }
            String exactText = sb.toString();

            // when
            String result = TextUtil.truncateTextLimit(exactText);

            // then
            assertThat(result).isEqualTo(exactText);
            assertThat(result.getBytes(StandardCharsets.UTF_8).length).isEqualTo(65533);
        }
    }
}
