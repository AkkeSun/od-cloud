package com.odcloud.application.group.service.update_group;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.domain.model.Group;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UpdateGroupServiceResponseTest {

    @Nested
    @DisplayName("[constructor] Constructor 테스트")
    class Describe_constructor {

        @Test
        @DisplayName("[success] Constructor로 UpdateGroupServiceResponse를 생성한다")
        void success() {
            // when
            UpdateGroupServiceResponse response = new UpdateGroupServiceResponse(
                Boolean.TRUE,
                "owner@example.com",
                "Test Group"
            );

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
            assertThat(response.ownerEmail()).isEqualTo("owner@example.com");
            assertThat(response.name()).isEqualTo("Test Group");
        }

        @Test
        @DisplayName("[success] null 값으로 Constructor로 UpdateGroupServiceResponse를 생성한다")
        void success_nullValue() {
            // when
            UpdateGroupServiceResponse response = new UpdateGroupServiceResponse(
                null,
                null,
                null
            );

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isNull();
            assertThat(response.ownerEmail()).isNull();
            assertThat(response.name()).isNull();
        }
    }

    @Nested
    @DisplayName("[of] 팩토리 메서드 테스트")
    class Describe_of {

        @Test
        @DisplayName("[success] of()로 Group으로부터 응답을 생성한다")
        void success() {
            // given
            Group group = Group.builder()
                .id(1L)
                .name("Test Group")
                .ownerEmail("owner@example.com")
                .storageUsed(0L)
                .storageTotal(3221225472L)
                .regDt(LocalDateTime.now())
                .build();

            // when
            UpdateGroupServiceResponse response = UpdateGroupServiceResponse.of(group);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
            assertThat(response.ownerEmail()).isEqualTo("owner@example.com");
            assertThat(response.name()).isEqualTo("Test Group");
        }

        @Test
        @DisplayName("[success] null 필드를 가진 Group으로부터 응답을 생성한다")
        void success_nullFields() {
            // given
            Group group = Group.builder()
                .id(1L)
                .build();

            // when
            UpdateGroupServiceResponse response = UpdateGroupServiceResponse.of(group);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
            assertThat(response.ownerEmail()).isNull();
            assertThat(response.name()).isNull();
        }
    }

    @Nested
    @DisplayName("[accessor] Record accessor 메서드 테스트")
    class Describe_accessor {

        @Test
        @DisplayName("[success] result()로 result를 조회한다")
        void success_result() {
            // given
            UpdateGroupServiceResponse response = new UpdateGroupServiceResponse(
                Boolean.TRUE,
                "owner@example.com",
                "Test Group"
            );

            // when
            Boolean result = response.result();

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("[success] ownerEmail()로 ownerEmail을 조회한다")
        void success_ownerEmail() {
            // given
            UpdateGroupServiceResponse response = new UpdateGroupServiceResponse(
                Boolean.TRUE,
                "owner@example.com",
                "Test Group"
            );

            // when
            String ownerEmail = response.ownerEmail();

            // then
            assertThat(ownerEmail).isEqualTo("owner@example.com");
        }

        @Test
        @DisplayName("[success] name()으로 name을 조회한다")
        void success_name() {
            // given
            UpdateGroupServiceResponse response = new UpdateGroupServiceResponse(
                Boolean.TRUE,
                "owner@example.com",
                "Test Group"
            );

            // when
            String name = response.name();

            // then
            assertThat(name).isEqualTo("Test Group");
        }
    }

    @Nested
    @DisplayName("[record] Record 불변성 테스트")
    class Describe_immutability {

        @Test
        @DisplayName("[success] UpdateGroupServiceResponse는 불변 객체이다")
        void success() {
            // given
            UpdateGroupServiceResponse response1 = new UpdateGroupServiceResponse(
                Boolean.TRUE,
                "owner@example.com",
                "Test Group"
            );
            UpdateGroupServiceResponse response2 = new UpdateGroupServiceResponse(
                Boolean.TRUE,
                "owner@example.com",
                "Test Group"
            );

            // when & then
            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }

        @Test
        @DisplayName("[success] 다른 값으로 생성된 UpdateGroupServiceResponse는 동등하지 않다")
        void success_notEqual() {
            // given
            UpdateGroupServiceResponse response1 = new UpdateGroupServiceResponse(
                Boolean.TRUE,
                "owner@example.com",
                "Test Group"
            );
            UpdateGroupServiceResponse response2 = new UpdateGroupServiceResponse(
                Boolean.FALSE,
                "other@example.com",
                "Other Group"
            );

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
            UpdateGroupServiceResponse response = new UpdateGroupServiceResponse(
                Boolean.TRUE,
                "owner@example.com",
                "Test Group"
            );

            // when
            String result = response.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("UpdateGroupServiceResponse");
            assertThat(result).contains("true");
            assertThat(result).contains("owner@example.com");
            assertThat(result).contains("Test Group");
        }

        @Test
        @DisplayName("[success] null 값을 포함한 toString()을 반환한다")
        void success_nullValue() {
            // given
            UpdateGroupServiceResponse response = new UpdateGroupServiceResponse(
                null,
                null,
                null
            );

            // when
            String result = response.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("UpdateGroupServiceResponse");
            assertThat(result).contains("null");
        }
    }
}
