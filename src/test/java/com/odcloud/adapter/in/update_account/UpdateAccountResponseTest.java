package com.odcloud.adapter.in.update_account;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.service.update_account.UpdateAccountServiceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UpdateAccountResponseTest {

    @Nested
    @DisplayName("[of] ServiceResponse를 Response로 변환하는 정적 팩토리 메서드")
    class Describe_of {

        @Test
        @DisplayName("[success] ServiceResponse를 Response로 변환한다")
        void success() {
            // given
            UpdateAccountServiceResponse serviceResponse =
                UpdateAccountServiceResponse.ofSuccess();

            // when
            UpdateAccountResponse response = UpdateAccountResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
        }

        @Test
        @DisplayName("[success] false 값을 포함한 ServiceResponse를 Response로 변환한다")
        void success_falseValue() {
            // given
            UpdateAccountServiceResponse serviceResponse =
                new UpdateAccountServiceResponse(false);

            // when
            UpdateAccountResponse response = UpdateAccountResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isFalse();
        }

        @Test
        @DisplayName("[success] null 값을 포함한 ServiceResponse를 Response로 변환한다")
        void success_nullValue() {
            // given
            UpdateAccountServiceResponse serviceResponse =
                new UpdateAccountServiceResponse(null);

            // when
            UpdateAccountResponse response = UpdateAccountResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isNull();
        }
    }

    @Nested
    @DisplayName("[constructor] Response 생성자 테스트")
    class Describe_constructor {

        @Test
        @DisplayName("[success] result로 Response를 생성한다")
        void success() {
            // when
            UpdateAccountResponse response = new UpdateAccountResponse(true);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
        }

        @Test
        @DisplayName("[success] null 값으로 Response를 생성한다")
        void success_nullValue() {
            // when
            UpdateAccountResponse response = new UpdateAccountResponse(null);

            // then
            assertThat(response.result()).isNull();
        }

        @Test
        @DisplayName("[success] false 값으로 Response를 생성한다")
        void success_falseValue() {
            // when
            UpdateAccountResponse response = new UpdateAccountResponse(false);

            // then
            assertThat(response.result()).isFalse();
        }
    }

    @Nested
    @DisplayName("[record] Record 불변성 테스트")
    class Describe_immutability {

        @Test
        @DisplayName("[success] Response는 불변 객체이다")
        void success() {
            // given
            UpdateAccountResponse response1 = new UpdateAccountResponse(true);
            UpdateAccountResponse response2 = new UpdateAccountResponse(true);

            // when & then
            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }

        @Test
        @DisplayName("[success] 다른 값으로 생성된 Response는 동등하지 않다")
        void success_notEqual() {
            // given
            UpdateAccountResponse response1 = new UpdateAccountResponse(true);
            UpdateAccountResponse response2 = new UpdateAccountResponse(false);

            // when & then
            assertThat(response1).isNotEqualTo(response2);
        }

        @Test
        @DisplayName("[success] null 값을 가진 Response들은 동등하다")
        void success_nullEqual() {
            // given
            UpdateAccountResponse response1 = new UpdateAccountResponse(null);
            UpdateAccountResponse response2 = new UpdateAccountResponse(null);

            // when & then
            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }
    }

    @Nested
    @DisplayName("[accessor] Response accessor 메서드 테스트")
    class Describe_accessor {

        @Test
        @DisplayName("[success] result()로 값을 조회한다")
        void success() {
            // given
            UpdateAccountResponse response = new UpdateAccountResponse(true);

            // when
            Boolean result = response.result();

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("[success] result()로 null 값을 조회한다")
        void success_nullValue() {
            // given
            UpdateAccountResponse response = new UpdateAccountResponse(null);

            // when
            Boolean result = response.result();

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("[success] result()로 false 값을 조회한다")
        void success_falseValue() {
            // given
            UpdateAccountResponse response = new UpdateAccountResponse(false);

            // when
            Boolean result = response.result();

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("[toString] Response toString 메서드 테스트")
    class Describe_toString {

        @Test
        @DisplayName("[success] toString()으로 문자열 표현을 반환한다")
        void success() {
            // given
            UpdateAccountResponse response = new UpdateAccountResponse(true);

            // when
            String result = response.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("UpdateAccountResponse");
            assertThat(result).contains("true");
        }

        @Test
        @DisplayName("[success] null 값을 포함한 toString()을 반환한다")
        void success_nullValue() {
            // given
            UpdateAccountResponse response = new UpdateAccountResponse(null);

            // when
            String result = response.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("UpdateAccountResponse");
            assertThat(result).contains("null");
        }

        @Test
        @DisplayName("[success] false 값을 포함한 toString()을 반환한다")
        void success_falseValue() {
            // given
            UpdateAccountResponse response = new UpdateAccountResponse(false);

            // when
            String result = response.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("UpdateAccountResponse");
            assertThat(result).contains("false");
        }
    }
}
