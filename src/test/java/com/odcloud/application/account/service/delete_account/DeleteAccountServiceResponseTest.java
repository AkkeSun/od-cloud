package com.odcloud.application.account.service.delete_account;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DeleteAccountServiceResponseTest {

    @Nested
    @DisplayName("[constructor] Constructor 테스트")
    class Describe_constructor {

        @Test
        @DisplayName("[success] Constructor로 DeleteAccountServiceResponse를 생성한다")
        void success() {
            // when
            DeleteAccountServiceResponse response = new DeleteAccountServiceResponse(Boolean.TRUE);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
        }

        @Test
        @DisplayName("[success] null 값으로 Constructor로 DeleteAccountServiceResponse를 생성한다")
        void success_nullValue() {
            // when
            DeleteAccountServiceResponse response = new DeleteAccountServiceResponse(null);

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
            DeleteAccountServiceResponse response = DeleteAccountServiceResponse.ofSuccess();

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
        void success_result() {
            // given
            DeleteAccountServiceResponse response = new DeleteAccountServiceResponse(Boolean.TRUE);

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
        @DisplayName("[success] DeleteAccountServiceResponse는 불변 객체이다")
        void success() {
            // given
            DeleteAccountServiceResponse response1 = new DeleteAccountServiceResponse(Boolean.TRUE);
            DeleteAccountServiceResponse response2 = new DeleteAccountServiceResponse(Boolean.TRUE);

            // when & then
            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }

        @Test
        @DisplayName("[success] 다른 값으로 생성된 DeleteAccountServiceResponse는 동등하지 않다")
        void success_notEqual() {
            // given
            DeleteAccountServiceResponse response1 = new DeleteAccountServiceResponse(Boolean.TRUE);
            DeleteAccountServiceResponse response2 = new DeleteAccountServiceResponse(Boolean.FALSE);

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
            DeleteAccountServiceResponse response = new DeleteAccountServiceResponse(Boolean.TRUE);

            // when
            String result = response.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("DeleteAccountServiceResponse");
            assertThat(result).contains("true");
        }

        @Test
        @DisplayName("[success] null 값을 포함한 toString()을 반환한다")
        void success_nullValue() {
            // given
            DeleteAccountServiceResponse response = new DeleteAccountServiceResponse(null);

            // when
            String result = response.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("DeleteAccountServiceResponse");
            assertThat(result).contains("null");
        }
    }
}
