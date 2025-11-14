package com.odcloud.adapter.in.register_folder;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.service.register_folder.RegisterFolderServiceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RegisterFolderResponseTest {

    @Nested
    @DisplayName("[of] ServiceResponse → Response 변환 테스트")
    class Describe_of {

        @Test
        @DisplayName("[success] true 값이 정상적으로 변환된다.")
        void success() {
            // given
            RegisterFolderServiceResponse serviceResponse =
                new RegisterFolderServiceResponse(true);

            // when
            RegisterFolderResponse response = RegisterFolderResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
        }

        @Test
        @DisplayName("[success] false 값이 정상적으로 변환된다.")
        void success_falseValue() {
            // given
            RegisterFolderServiceResponse serviceResponse =
                new RegisterFolderServiceResponse(false);

            // when
            RegisterFolderResponse response = RegisterFolderResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isFalse();
        }

        @Test
        @DisplayName("[success] null 값이 정상적으로 변환된다.")
        void success_nullValue() {
            // given
            RegisterFolderServiceResponse serviceResponse =
                new RegisterFolderServiceResponse(null);

            // when
            RegisterFolderResponse response = RegisterFolderResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isNull();
        }
    }

    @Nested
    @DisplayName("[constructor] Record 생성자 테스트")
    class Describe_constructor {

        @Test
        @DisplayName("[success] true 값으로 생성된다.")
        void success() {
            // when
            RegisterFolderResponse response = new RegisterFolderResponse(true);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
        }

        @Test
        @DisplayName("[success] null 값으로 생성된다.")
        void success_nullValue() {
            // when
            RegisterFolderResponse response = new RegisterFolderResponse(null);

            // then
            assertThat(response.result()).isNull();
        }
    }

    @Nested
    @DisplayName("[record] Record 불변성 테스트")
    class Describe_immutability {

        @Test
        @DisplayName("[success] 동일한 값이면 equals/hashCode도 동일하다.")
        void success() {
            // given
            RegisterFolderResponse response1 = new RegisterFolderResponse(true);
            RegisterFolderResponse response2 = new RegisterFolderResponse(true);

            // when & then
            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }

        @Test
        @DisplayName("[success] 값이 다르면 equals 비교 시 false가 나온다.")
        void success_notEqual() {
            // given
            RegisterFolderResponse response1 = new RegisterFolderResponse(true);
            RegisterFolderResponse response2 = new RegisterFolderResponse(false);

            // when & then
            assertThat(response1).isNotEqualTo(response2);
        }
    }

    @Nested
    @DisplayName("[accessor] Response accessor 테스트")
    class Describe_accessor {

        @Test
        @DisplayName("[success] result() 값이 정상적으로 반환된다.")
        void success() {
            // given
            RegisterFolderResponse response = new RegisterFolderResponse(true);

            // when
            Boolean result = response.result();

            // then
            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("[toString] Response toString 테스트")
    class Describe_toString {

        @Test
        @DisplayName("[success] toString() 결과가 정상적으로 생성된다.")
        void success() {
            // given
            RegisterFolderResponse response = new RegisterFolderResponse(true);

            // when
            String result = response.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("RegisterFolderResponse");
            assertThat(result).contains("true");
        }
    }
}
