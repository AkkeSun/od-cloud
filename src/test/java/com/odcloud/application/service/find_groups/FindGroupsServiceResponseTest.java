package com.odcloud.application.service.find_groups;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.domain.model.Group;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FindGroupsServiceResponseTest {

    @Nested
    @DisplayName("[of] Group 리스트와 상태 맵으로 Response 생성")
    class Describe_of {

        @Test
        @DisplayName("[success] Group 리스트와 상태 맵으로 ServiceResponse를 생성한다")
        void success() {
            // given
            Group group1 = Group.builder()
                .id("group-1")
                .ownerEmail("owner1@example.com")
                .description("Development Team")
                .regDt(LocalDateTime.of(2024, 1, 1, 12, 0, 0))
                .build();

            Group group2 = Group.builder()
                .id("group-2")
                .ownerEmail("owner2@example.com")
                .description("Marketing Team")
                .regDt(LocalDateTime.of(2024, 1, 2, 12, 0, 0))
                .build();

            List<Group> groups = List.of(group1, group2);

            Map<String, String> groupStatusMap = new HashMap<>();
            groupStatusMap.put("group-1", "ACTIVE");
            groupStatusMap.put("group-2", "PENDING");

            // when
            FindGroupsServiceResponse response = FindGroupsServiceResponse.of(groups, groupStatusMap);

            // then
            assertThat(response).isNotNull();
            assertThat(response.groups()).hasSize(2);

            // Group 1 검증
            FindGroupsServiceResponse.GroupResponseItem responseItem1 = response.groups().get(0);
            assertThat(responseItem1.id()).isEqualTo("group-1");
            assertThat(responseItem1.ownerEmail()).isEqualTo("owner1@example.com");
            assertThat(responseItem1.description()).isEqualTo("Development Team");
            assertThat(responseItem1.regDt()).isEqualTo("2024-01-01T12:00");
            assertThat(responseItem1.status()).isEqualTo("ACTIVE");

            // Group 2 검증
            FindGroupsServiceResponse.GroupResponseItem responseItem2 = response.groups().get(1);
            assertThat(responseItem2.id()).isEqualTo("group-2");
            assertThat(responseItem2.ownerEmail()).isEqualTo("owner2@example.com");
            assertThat(responseItem2.description()).isEqualTo("Marketing Team");
            assertThat(responseItem2.regDt()).isEqualTo("2024-01-02T12:00");
            assertThat(responseItem2.status()).isEqualTo("PENDING");
        }

        @Test
        @DisplayName("[success] 빈 Group 리스트로 ServiceResponse를 생성한다")
        void success_emptyGroups() {
            // given
            List<Group> groups = List.of();
            Map<String, String> groupStatusMap = new HashMap<>();

            // when
            FindGroupsServiceResponse response = FindGroupsServiceResponse.of(groups, groupStatusMap);

            // then
            assertThat(response).isNotNull();
            assertThat(response.groups()).isEmpty();
        }

        @Test
        @DisplayName("[success] 상태 맵에 없는 그룹은 status가 null이다")
        void success_groupWithoutStatus() {
            // given
            Group group1 = Group.builder()
                .id("group-1")
                .ownerEmail("owner1@example.com")
                .description("Development Team")
                .regDt(LocalDateTime.of(2024, 1, 1, 12, 0, 0))
                .build();

            Group group2 = Group.builder()
                .id("group-2")
                .ownerEmail("owner2@example.com")
                .description("Marketing Team")
                .regDt(LocalDateTime.of(2024, 1, 2, 12, 0, 0))
                .build();

            List<Group> groups = List.of(group1, group2);

            Map<String, String> groupStatusMap = new HashMap<>();
            groupStatusMap.put("group-1", "ACTIVE");
            // group-2는 상태 맵에 없음

            // when
            FindGroupsServiceResponse response = FindGroupsServiceResponse.of(groups, groupStatusMap);

            // then
            assertThat(response.groups()).hasSize(2);
            assertThat(response.groups().get(0).status()).isEqualTo("ACTIVE");
            assertThat(response.groups().get(1).status()).isNull();
        }

        @Test
        @DisplayName("[success] regDt가 null인 Group도 변환한다")
        void success_nullRegDt() {
            // given
            Group group = Group.builder()
                .id("group-1")
                .ownerEmail("owner@example.com")
                .description("Test Group")
                .regDt(null)
                .build();

            List<Group> groups = List.of(group);
            Map<String, String> groupStatusMap = new HashMap<>();
            groupStatusMap.put("group-1", "ACTIVE");

            // when
            FindGroupsServiceResponse response = FindGroupsServiceResponse.of(groups, groupStatusMap);

            // then
            assertThat(response.groups()).hasSize(1);
            assertThat(response.groups().get(0).regDt()).isNull();
        }

        @Test
        @DisplayName("[success] 다양한 상태를 가진 그룹들을 변환한다")
        void success_variousStatuses() {
            // given
            Group group1 = Group.builder()
                .id("group-1")
                .ownerEmail("owner1@example.com")
                .description("Active Group")
                .regDt(LocalDateTime.of(2024, 1, 1, 12, 0, 0))
                .build();

            Group group2 = Group.builder()
                .id("group-2")
                .ownerEmail("owner2@example.com")
                .description("Pending Group")
                .regDt(LocalDateTime.of(2024, 1, 2, 12, 0, 0))
                .build();

            Group group3 = Group.builder()
                .id("group-3")
                .ownerEmail("owner3@example.com")
                .description("No Status Group")
                .regDt(LocalDateTime.of(2024, 1, 3, 12, 0, 0))
                .build();

            List<Group> groups = List.of(group1, group2, group3);

            Map<String, String> groupStatusMap = new HashMap<>();
            groupStatusMap.put("group-1", "ACTIVE");
            groupStatusMap.put("group-2", "PENDING");
            // group-3은 상태 맵에 없음

            // when
            FindGroupsServiceResponse response = FindGroupsServiceResponse.of(groups, groupStatusMap);

            // then
            assertThat(response.groups()).hasSize(3);
            assertThat(response.groups().get(0).status()).isEqualTo("ACTIVE");
            assertThat(response.groups().get(1).status()).isEqualTo("PENDING");
            assertThat(response.groups().get(2).status()).isNull();
        }
    }

    @Nested
    @DisplayName("[GroupResponseItem.of] Group과 status로 GroupResponseItem 생성")
    class Describe_GroupResponseItem_of {

        @Test
        @DisplayName("[success] Group과 status로 GroupResponseItem을 생성한다")
        void success() {
            // given
            Group group = Group.builder()
                .id("group-1")
                .ownerEmail("owner@example.com")
                .description("Test Group")
                .regDt(LocalDateTime.of(2024, 1, 1, 12, 0, 0))
                .build();

            String status = "ACTIVE";

            // when
            FindGroupsServiceResponse.GroupResponseItem item =
                FindGroupsServiceResponse.GroupResponseItem.of(group, status);

            // then
            assertThat(item).isNotNull();
            assertThat(item.id()).isEqualTo("group-1");
            assertThat(item.ownerEmail()).isEqualTo("owner@example.com");
            assertThat(item.description()).isEqualTo("Test Group");
            assertThat(item.regDt()).isEqualTo("2024-01-01T12:00");
            assertThat(item.status()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("[success] status가 null인 GroupResponseItem을 생성한다")
        void success_nullStatus() {
            // given
            Group group = Group.builder()
                .id("group-1")
                .ownerEmail("owner@example.com")
                .description("Test Group")
                .regDt(LocalDateTime.of(2024, 1, 1, 12, 0, 0))
                .build();

            String status = null;

            // when
            FindGroupsServiceResponse.GroupResponseItem item =
                FindGroupsServiceResponse.GroupResponseItem.of(group, status);

            // then
            assertThat(item.id()).isEqualTo("group-1");
            assertThat(item.status()).isNull();
        }

        @Test
        @DisplayName("[success] regDt가 null인 GroupResponseItem을 생성한다")
        void success_nullRegDt() {
            // given
            Group group = Group.builder()
                .id("group-1")
                .ownerEmail("owner@example.com")
                .description("Test Group")
                .regDt(null)
                .build();

            String status = "ACTIVE";

            // when
            FindGroupsServiceResponse.GroupResponseItem item =
                FindGroupsServiceResponse.GroupResponseItem.of(group, status);

            // then
            assertThat(item.id()).isEqualTo("group-1");
            assertThat(item.regDt()).isNull();
            assertThat(item.status()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("[success] PENDING 상태의 GroupResponseItem을 생성한다")
        void success_pendingStatus() {
            // given
            Group group = Group.builder()
                .id("group-2")
                .ownerEmail("pending@example.com")
                .description("Pending Group")
                .regDt(LocalDateTime.of(2024, 1, 2, 12, 0, 0))
                .build();

            String status = "PENDING";

            // when
            FindGroupsServiceResponse.GroupResponseItem item =
                FindGroupsServiceResponse.GroupResponseItem.of(group, status);

            // then
            assertThat(item.id()).isEqualTo("group-2");
            assertThat(item.status()).isEqualTo("PENDING");
        }

        @Test
        @DisplayName("[success] LocalDateTime의 초 단위까지 표현한다")
        void success_fullDateTime() {
            // given
            Group group = Group.builder()
                .id("group-1")
                .ownerEmail("owner@example.com")
                .description("Test Group")
                .regDt(LocalDateTime.of(2024, 1, 1, 12, 30, 45))
                .build();

            String status = "ACTIVE";

            // when
            FindGroupsServiceResponse.GroupResponseItem item =
                FindGroupsServiceResponse.GroupResponseItem.of(group, status);

            // then
            assertThat(item.regDt()).isEqualTo("2024-01-01T12:30:45");
        }
    }

    @Nested
    @DisplayName("[builder] Builder로 Response 생성")
    class Describe_builder {

        @Test
        @DisplayName("[success] Builder로 ServiceResponse를 생성한다")
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

            // when
            FindGroupsServiceResponse response = FindGroupsServiceResponse.builder()
                .groups(List.of(item1, item2))
                .build();

            // then
            assertThat(response).isNotNull();
            assertThat(response.groups()).hasSize(2);
            assertThat(response.groups().get(0).id()).isEqualTo("group-1");
            assertThat(response.groups().get(1).id()).isEqualTo("group-2");
        }

        @Test
        @DisplayName("[success] 빈 리스트로 ServiceResponse를 생성한다")
        void success_emptyList() {
            // given & when
            FindGroupsServiceResponse response = FindGroupsServiceResponse.builder()
                .groups(List.of())
                .build();

            // then
            assertThat(response).isNotNull();
            assertThat(response.groups()).isEmpty();
        }
    }
}
