package com.odcloud.application.service.register_folder;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RegisterFolderServiceResponseTest {

    @Nested
    @DisplayName("[constructor] Constructor 테스트")
    class Describe_constructor {

        @Test
        @DisplayName("[success] Constructor로 RegisterFolderServiceResponse를 생성한다")
        void success() {
            // when
            RegisterFolderServiceResponse response = new RegisterFolderServiceResponse(Boolean.TRUE);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
        }

        @Test
        @DisplayName("[success] false 값으로 Constructor로 RegisterFolderServiceResponse를 생성한다")
        void success_false() {
            // when
            RegisterFolderServiceResponse response = new RegisterFolderServiceResponse(Boolean.FALSE);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isFalse();
        }

        @Test
        @DisplayName("[success] null 값으로 Constructor로 RegisterFolderServiceResponse를 생성한다")
        void success_nullValue() {
            // when
            RegisterFolderServiceResponse response = new RegisterFolderServiceResponse(null);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isNull();
        }
    }

    @Nested
    @DisplayName("[ofSuccess] 팩토리 메서드 테스트")
    class Describe_ofSuccess {

        @Test
        @DisplayName("[success] ofSuccess()로 성공 응답을 생성한다")
        void success() {
            // when
            RegisterFolderServiceResponse response = RegisterFolderServiceResponse.ofSuccess();

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
        }
    }

    @Nested
    @DisplayName("[accessor] Record accessor 메서드 테스트")
    class Describe_accessor {

        @Test
        @DisplayName("[success] result()로 result를 조회한다")
        void success() {
            // given
            RegisterFolderServiceResponse response = new RegisterFolderServiceResponse(Boolean.TRUE);

            // when
            Boolean result = response.result();

            // then
            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("[record] Record 불변성 테스트")
    class Describe_immutability {

        @Test
        @DisplayName("[success] RegisterFolderServiceResponse는 불변 객체이다")
        void success() {
            // given
            RegisterFolderServiceResponse response1 = new RegisterFolderServiceResponse(Boolean.TRUE);
            RegisterFolderServiceResponse response2 = new RegisterFolderServiceResponse(Boolean.TRUE);

            // when & then
            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }

        @Test
        @DisplayName("[success] 다른 값으로 생성된 RegisterFolderServiceResponse는 동등하지 않다")
        void success_notEqual() {
            // given
            RegisterFolderServiceResponse response1 = new RegisterFolderServiceResponse(Boolean.TRUE);
            RegisterFolderServiceResponse response2 = new RegisterFolderServiceResponse(Boolean.FALSE);

            // when & then
            assertThat(response1).isNotEqualTo(response2);
        }
    }

    @Nested
    @DisplayName("[toString] Record toString 메서드 테스트")
    class Describe_toString {

        @Test
        @DisplayName("[success] toString()으로 문자열 표현을 반환한다")
        void success() {
            // given
            RegisterFolderServiceResponse response = new RegisterFolderServiceResponse(Boolean.TRUE);

            // when
            String result = response.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("RegisterFolderServiceResponse");
            assertThat(result).contains("true");
        }

        @Test
        @DisplayName("[success] null 값을 포함한 toString()을 반환한다")
        void success_nullValue() {
            // given
            RegisterFolderServiceResponse response = new RegisterFolderServiceResponse(null);

            // when
            String result = response.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("RegisterFolderServiceResponse");
            assertThat(result).contains("null");
        }
    }
}
