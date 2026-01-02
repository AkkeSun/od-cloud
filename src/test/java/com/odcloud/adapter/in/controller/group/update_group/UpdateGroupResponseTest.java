package com.odcloud.adapter.in.controller.group.update_group;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.group.service.update_group.UpdateGroupServiceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UpdateGroupResponseTest {

    @Nested
    @DisplayName("[of] ServiceResponse를 Response로 변환하는 정적 팩토리 메서드")
    class Describe_of {

        @Test
        @DisplayName("[success] ServiceResponse를 Response로 변환한다")
        void success() {
            // given
            UpdateGroupServiceResponse serviceResponse = new UpdateGroupServiceResponse(
                Boolean.TRUE,
                "owner@example.com",
                "Test Group"
            );

            // when
            UpdateGroupResponse response = UpdateGroupResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
            assertThat(response.ownerEmail()).isEqualTo("owner@example.com");
            assertThat(response.name()).isEqualTo("Test Group");
        }

        @Test
        @DisplayName("[success] false 값을 포함한 ServiceResponse를 Response로 변환한다")
        void success_falseValue() {
            // given
            UpdateGroupServiceResponse serviceResponse = new UpdateGroupServiceResponse(
                Boolean.FALSE,
                "owner@example.com",
                "Test Group"
            );

            // when
            UpdateGroupResponse response = UpdateGroupResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isFalse();
            assertThat(response.ownerEmail()).isEqualTo("owner@example.com");
            assertThat(response.name()).isEqualTo("Test Group");
        }

        @Test
        @DisplayName("[success] null 값을 포함한 ServiceResponse를 Response로 변환한다")
        void success_nullValue() {
            // given
            UpdateGroupServiceResponse serviceResponse = new UpdateGroupServiceResponse(
                null,
                null,
                null
            );

            // when
            UpdateGroupResponse response = UpdateGroupResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isNull();
            assertThat(response.ownerEmail()).isNull();
            assertThat(response.name()).isNull();
        }
    }

    @Nested
    @DisplayName("[builder] Builder 패턴 테스트")
    class Describe_builder {

        @Test
        @DisplayName("[success] Builder로 모든 필드를 포함한 Response를 생성한다")
        void success() {
            // when
            UpdateGroupResponse response = UpdateGroupResponse.builder()
                .result(true)
                .ownerEmail("owner@example.com")
                .name("Test Group")
                .build();

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
            assertThat(response.ownerEmail()).isEqualTo("owner@example.com");
            assertThat(response.name()).isEqualTo("Test Group");
        }

        @Test
        @DisplayName("[success] null 값으로 Response를 생성한다")
        void success_nullValue() {
            // when
            UpdateGroupResponse response = UpdateGroupResponse.builder()
                .result(null)
                .ownerEmail(null)
                .name(null)
                .build();

            // then
            assertThat(response.result()).isNull();
            assertThat(response.ownerEmail()).isNull();
            assertThat(response.name()).isNull();
        }
    }

    @Nested
    @DisplayName("[record] Record 불변성 테스트")
    class Describe_immutability {

        @Test
        @DisplayName("[success] Response는 불변 객체이다")
        void success() {
            // given
            UpdateGroupResponse response1 = UpdateGroupResponse.builder()
                .result(true)
                .ownerEmail("owner@example.com")
                .name("Test Group")
                .build();

            UpdateGroupResponse response2 = UpdateGroupResponse.builder()
                .result(true)
                .ownerEmail("owner@example.com")
                .name("Test Group")
                .build();

            // when & then
            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }

        @Test
        @DisplayName("[success] 다른 값으로 생성된 Response는 동등하지 않다")
        void success_notEqual() {
            // given
            UpdateGroupResponse response1 = UpdateGroupResponse.builder()
                .result(true)
                .ownerEmail("owner@example.com")
                .name("Test Group")
                .build();

            UpdateGroupResponse response2 = UpdateGroupResponse.builder()
                .result(false)
                .ownerEmail("other@example.com")
                .name("Other Group")
                .build();

            // when & then
            assertThat(response1).isNotEqualTo(response2);
        }
    }

    @Nested
    @DisplayName("[accessor] Response accessor 메서드 테스트")
    class Describe_accessor {

        @Test
        @DisplayName("[success] result()로 값을 조회한다")
        void success_result() {
            // given
            UpdateGroupResponse response = UpdateGroupResponse.builder()
                .result(true)
                .ownerEmail("owner@example.com")
                .name("Test Group")
                .build();

            // when
            Boolean result = response.result();

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("[success] ownerEmail()로 값을 조회한다")
        void success_ownerEmail() {
            // given
            UpdateGroupResponse response = UpdateGroupResponse.builder()
                .result(true)
                .ownerEmail("owner@example.com")
                .name("Test Group")
                .build();

            // when
            String ownerEmail = response.ownerEmail();

            // then
            assertThat(ownerEmail).isEqualTo("owner@example.com");
        }

        @Test
        @DisplayName("[success] name()으로 값을 조회한다")
        void success_name() {
            // given
            UpdateGroupResponse response = UpdateGroupResponse.builder()
                .result(true)
                .ownerEmail("owner@example.com")
                .name("Test Group")
                .build();

            // when
            String name = response.name();

            // then
            assertThat(name).isEqualTo("Test Group");
        }
    }

    @Nested
    @DisplayName("[toString] Response toString 메서드 테스트")
    class Describe_toString {

        @Test
        @DisplayName("[success] toString()으로 문자열 표현을 반환한다")
        void success() {
            // given
            UpdateGroupResponse response = UpdateGroupResponse.builder()
                .result(true)
                .ownerEmail("owner@example.com")
                .name("Test Group")
                .build();

            // when
            String result = response.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("UpdateGroupResponse");
            assertThat(result).contains("true");
            assertThat(result).contains("owner@example.com");
            assertThat(result).contains("Test Group");
        }

        @Test
        @DisplayName("[success] null 값을 포함한 toString()을 반환한다")
        void success_nullValue() {
            // given
            UpdateGroupResponse response = UpdateGroupResponse.builder()
                .result(null)
                .ownerEmail(null)
                .name(null)
                .build();

            // when
            String result = response.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("UpdateGroupResponse");
            assertThat(result).contains("null");
        }
    }
}
