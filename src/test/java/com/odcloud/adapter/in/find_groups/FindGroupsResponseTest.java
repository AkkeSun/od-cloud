package com.odcloud.adapter.in.find_groups;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.service.find_groups.FindGroupsServiceResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FindGroupsResponseTest {

    @Nested
    @DisplayName("[of] ServiceResponse로 Response 생성")
    class Describe_of {

        @Test
        @DisplayName("[success] ServiceResponse로 Response를 생성한다")
        void success() {
            // given
            FindGroupsServiceResponse.GroupResponseItem item1 =
                FindGroupsServiceResponse.GroupResponseItem.builder()
                    .id("group-1")
                    .ownerEmail("owner1@example.com")
                    .description("Development Team")
                    .regDt("2024-01-01T12:00:00")
                    .status("ACTIVE")
                    .build();

            FindGroupsServiceResponse.GroupResponseItem item2 =
                FindGroupsServiceResponse.GroupResponseItem.builder()
                    .id("group-2")
                    .ownerEmail("owner2@example.com")
                    .description("Marketing Team")
                    .regDt("2024-01-02T12:00:00")
                    .status("PENDING")
                    .build();

            FindGroupsServiceResponse serviceResponse = FindGroupsServiceResponse.builder()
                .groups(List.of(item1, item2))
                .build();

            // when
            FindGroupsResponse response = FindGroupsResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.groups()).hasSize(2);

            // Group 1 검증
            assertThat(response.groups().get(0).id()).isEqualTo("group-1");
            assertThat(response.groups().get(0).ownerEmail()).isEqualTo("owner1@example.com");
            assertThat(response.groups().get(0).description()).isEqualTo("Development Team");
            assertThat(response.groups().get(0).regDt()).isEqualTo("2024-01-01T12:00:00");
            assertThat(response.groups().get(0).status()).isEqualTo("ACTIVE");

            // Group 2 검증
            assertThat(response.groups().get(1).id()).isEqualTo("group-2");
            assertThat(response.groups().get(1).ownerEmail()).isEqualTo("owner2@example.com");
            assertThat(response.groups().get(1).description()).isEqualTo("Marketing Team");
            assertThat(response.groups().get(1).regDt()).isEqualTo("2024-01-02T12:00:00");
            assertThat(response.groups().get(1).status()).isEqualTo("PENDING");
        }

        @Test
        @DisplayName("[success] 빈 ServiceResponse로 Response를 생성한다")
        void success_emptyList() {
            // given
            FindGroupsServiceResponse serviceResponse = FindGroupsServiceResponse.builder()
                .groups(List.of())
                .build();

            // when
            FindGroupsResponse response = FindGroupsResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.groups()).isEmpty();
        }

        @Test
        @DisplayName("[success] 하나의 그룹만 있는 ServiceResponse로 Response를 생성한다")
        void success_singleGroup() {
            // given
            FindGroupsServiceResponse.GroupResponseItem item =
                FindGroupsServiceResponse.GroupResponseItem.builder()
                    .id("group-1")
                    .ownerEmail("owner@example.com")
                    .description("Test Group")
                    .regDt("2024-01-01T12:00:00")
                    .status("ACTIVE")
                    .build();

            FindGroupsServiceResponse serviceResponse = FindGroupsServiceResponse.builder()
                .groups(List.of(item))
                .build();

            // when
            FindGroupsResponse response = FindGroupsResponse.of(serviceResponse);

            // then
            assertThat(response.groups()).hasSize(1);
            assertThat(response.groups().get(0).id()).isEqualTo("group-1");
            assertThat(response.groups().get(0).ownerEmail()).isEqualTo("owner@example.com");
        }

        @Test
        @DisplayName("[success] status가 null인 그룹도 Response로 변환한다")
        void success_nullStatus() {
            // given
            FindGroupsServiceResponse.GroupResponseItem item =
                FindGroupsServiceResponse.GroupResponseItem.builder()
                    .id("group-1")
                    .ownerEmail("owner@example.com")
                    .description("Test Group")
                    .regDt("2024-01-01T12:00:00")
                    .status(null)
                    .build();

            FindGroupsServiceResponse serviceResponse = FindGroupsServiceResponse.builder()
                .groups(List.of(item))
                .build();

            // when
            FindGroupsResponse response = FindGroupsResponse.of(serviceResponse);

            // then
            assertThat(response.groups()).hasSize(1);
            assertThat(response.groups().get(0).status()).isNull();
        }

        @Test
        @DisplayName("[success] 다양한 status를 가진 그룹들을 Response로 변환한다")
        void success_variousStatuses() {
            // given
            FindGroupsServiceResponse.GroupResponseItem activeGroup =
                FindGroupsServiceResponse.GroupResponseItem.builder()
                    .id("group-1")
                    .ownerEmail("owner1@example.com")
                    .description("Active Group")
                    .regDt("2024-01-01T12:00:00")
                    .status("ACTIVE")
                    .build();

            FindGroupsServiceResponse.GroupResponseItem pendingGroup =
                FindGroupsServiceResponse.GroupResponseItem.builder()
                    .id("group-2")
                    .ownerEmail("owner2@example.com")
                    .description("Pending Group")
                    .regDt("2024-01-02T12:00:00")
                    .status("PENDING")
                    .build();

            FindGroupsServiceResponse.GroupResponseItem noStatusGroup =
                FindGroupsServiceResponse.GroupResponseItem.builder()
                    .id("group-3")
                    .ownerEmail("owner3@example.com")
                    .description("No Status Group")
                    .regDt("2024-01-03T12:00:00")
                    .status(null)
                    .build();

            FindGroupsServiceResponse serviceResponse = FindGroupsServiceResponse.builder()
                .groups(List.of(activeGroup, pendingGroup, noStatusGroup))
                .build();

            // when
            FindGroupsResponse response = FindGroupsResponse.of(serviceResponse);

            // then
            assertThat(response.groups()).hasSize(3);
            assertThat(response.groups().get(0).status()).isEqualTo("ACTIVE");
            assertThat(response.groups().get(1).status()).isEqualTo("PENDING");
            assertThat(response.groups().get(2).status()).isNull();
        }
    }

    @Nested
    @DisplayName("[GroupResponse.of] GroupResponseItem으로 GroupResponse 생성")
    class Describe_GroupResponse_of {

        @Test
        @DisplayName("[success] GroupResponseItem으로 GroupResponse를 생성한다")
        void success() {
            // given
            FindGroupsServiceResponse.GroupResponseItem item =
                FindGroupsServiceResponse.GroupResponseItem.builder()
                    .id("group-1")
                    .ownerEmail("owner@example.com")
                    .description("Test Group")
                    .regDt("2024-01-01T12:00:00")
                    .status("ACTIVE")
                    .build();

            // when
            FindGroupsResponse.GroupResponse response =
                FindGroupsResponse.GroupResponse.of(item);

            // then
            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo("group-1");
            assertThat(response.ownerEmail()).isEqualTo("owner@example.com");
            assertThat(response.description()).isEqualTo("Test Group");
            assertThat(response.regDt()).isEqualTo("2024-01-01T12:00:00");
            assertThat(response.status()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("[success] PENDING 상태의 GroupResponse를 생성한다")
        void success_pendingStatus() {
            // given
            FindGroupsServiceResponse.GroupResponseItem item =
                FindGroupsServiceResponse.GroupResponseItem.builder()
                    .id("group-2")
                    .ownerEmail("pending@example.com")
                    .description("Pending Group")
                    .regDt("2024-01-02T12:00:00")
                    .status("PENDING")
                    .build();

            // when
            FindGroupsResponse.GroupResponse response =
                FindGroupsResponse.GroupResponse.of(item);

            // then
            assertThat(response.id()).isEqualTo("group-2");
            assertThat(response.status()).isEqualTo("PENDING");
        }

        @Test
        @DisplayName("[success] status가 null인 GroupResponse를 생성한다")
        void success_nullStatus() {
            // given
            FindGroupsServiceResponse.GroupResponseItem item =
                FindGroupsServiceResponse.GroupResponseItem.builder()
                    .id("group-3")
                    .ownerEmail("nostatus@example.com")
                    .description("No Status Group")
                    .regDt("2024-01-03T12:00:00")
                    .status(null)
                    .build();

            // when
            FindGroupsResponse.GroupResponse response =
                FindGroupsResponse.GroupResponse.of(item);

            // then
            assertThat(response.id()).isEqualTo("group-3");
            assertThat(response.status()).isNull();
        }
    }

    @Nested
    @DisplayName("[builder] Builder로 Response 생성")
    class Describe_builder {

        @Test
        @DisplayName("[success] Builder로 Response를 생성한다")
        void success() {
            // given
            FindGroupsResponse.GroupResponse groupResponse1 =
                FindGroupsResponse.GroupResponse.builder()
                    .id("group-1")
                    .ownerEmail("owner1@example.com")
                    .description("Development Team")
                    .regDt("2024-01-01T12:00:00")
                    .status("ACTIVE")
                    .build();

            FindGroupsResponse.GroupResponse groupResponse2 =
                FindGroupsResponse.GroupResponse.builder()
                    .id("group-2")
                    .ownerEmail("owner2@example.com")
                    .description("Marketing Team")
                    .regDt("2024-01-02T12:00:00")
                    .status("PENDING")
                    .build();

            // when
            FindGroupsResponse response = FindGroupsResponse.builder()
                .groups(List.of(groupResponse1, groupResponse2))
                .build();

            // then
            assertThat(response).isNotNull();
            assertThat(response.groups()).hasSize(2);
            assertThat(response.groups().get(0).id()).isEqualTo("group-1");
            assertThat(response.groups().get(1).id()).isEqualTo("group-2");
        }

        @Test
        @DisplayName("[success] 빈 리스트로 Response를 생성한다")
        void success_emptyList() {
            // given & when
            FindGroupsResponse response = FindGroupsResponse.builder()
                .groups(List.of())
                .build();

            // then
            assertThat(response).isNotNull();
            assertThat(response.groups()).isEmpty();
        }
    }
}
