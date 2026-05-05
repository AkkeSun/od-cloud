package com.odcloud.application.group.service.find_group_account_list;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.domain.model.GroupAccount;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FindGroupAccountListResponseTest {

    @Nested
    @DisplayName("[constructor] Constructor 테스트")
    class Describe_constructor {

        @Test
        @DisplayName("[success] Constructor로 FindGroupAccountListResponse를 생성한다")
        void success() {
            // given
            List<GroupAccount> groupAccounts = new ArrayList<>();
            groupAccounts.add(GroupAccount.builder().id(1L).build());
            groupAccounts.add(GroupAccount.builder().id(2L).build());

            // when
            FindGroupAccountListResponse response = new FindGroupAccountListResponse(
                groupAccounts);

            // then
            assertThat(response).isNotNull();
            assertThat(response.groupAccounts()).hasSize(2);
            assertThat(response.groupAccounts().get(0).getId()).isEqualTo(1L);
            assertThat(response.groupAccounts().get(1).getId()).isEqualTo(2L);
        }

        @Test
        @DisplayName("[success] 빈 리스트로 Constructor로 FindGroupAccountListResponse를 생성한다")
        void success_emptyList() {
            // given
            List<GroupAccount> groupAccounts = new ArrayList<>();

            // when
            FindGroupAccountListResponse response = new FindGroupAccountListResponse(
                groupAccounts);

            // then
            assertThat(response).isNotNull();
            assertThat(response.groupAccounts()).isEmpty();
        }

        @Test
        @DisplayName("[success] null 값으로 Constructor로 FindGroupAccountListResponse를 생성한다")
        void success_nullValue() {
            // when
            FindGroupAccountListResponse response = new FindGroupAccountListResponse(
                null);

            // then
            assertThat(response).isNotNull();
            assertThat(response.groupAccounts()).isNull();
        }
    }

    @Nested
    @DisplayName("[builder] Builder 테스트")
    class Describe_builder {

        @Test
        @DisplayName("[success] Builder로 FindGroupAccountListResponse를 생성한다")
        void success() {
            // given
            List<GroupAccount> groupAccounts = new ArrayList<>();
            groupAccounts.add(GroupAccount.builder().id(1L).build());

            // when
            FindGroupAccountListResponse response = FindGroupAccountListResponse.builder()
                .groupAccounts(groupAccounts)
                .build();

            // then
            assertThat(response).isNotNull();
            assertThat(response.groupAccounts()).hasSize(1);
            assertThat(response.groupAccounts().get(0).getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("[success] Builder로 null 값을 가진 FindGroupAccountListResponse를 생성한다")
        void success_nullValue() {
            // when
            FindGroupAccountListResponse response = FindGroupAccountListResponse.builder()
                .groupAccounts(null)
                .build();

            // then
            assertThat(response).isNotNull();
            assertThat(response.groupAccounts()).isNull();
        }
    }

    @Nested
    @DisplayName("[of] 팩토리 메서드 테스트")
    class Describe_of {

        @Test
        @DisplayName("[success] of()로 FindGroupAccountListResponse를 생성한다")
        void success() {
            // given
            List<GroupAccount> groupAccounts = new ArrayList<>();
            groupAccounts.add(GroupAccount.builder().id(1L).groupId(1L).build());
            groupAccounts.add(GroupAccount.builder().id(2L).groupId(1L).build());

            // when
            FindGroupAccountListResponse response = FindGroupAccountListResponse.of(
                groupAccounts);

            // then
            assertThat(response).isNotNull();
            assertThat(response.groupAccounts()).hasSize(2);
            assertThat(response.groupAccounts().get(0).getId()).isEqualTo(1L);
            assertThat(response.groupAccounts().get(0).getGroupId()).isEqualTo(1L);
            assertThat(response.groupAccounts().get(1).getId()).isEqualTo(2L);
            assertThat(response.groupAccounts().get(1).getGroupId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("[success] of()로 빈 리스트를 가진 FindGroupAccountListResponse를 생성한다")
        void success_emptyList() {
            // given
            List<GroupAccount> groupAccounts = new ArrayList<>();

            // when
            FindGroupAccountListResponse response = FindGroupAccountListResponse.of(
                groupAccounts);

            // then
            assertThat(response).isNotNull();
            assertThat(response.groupAccounts()).isEmpty();
        }

        @Test
        @DisplayName("[success] of()로 null을 가진 FindGroupAccountListResponse를 생성한다")
        void success_null() {
            // when
            FindGroupAccountListResponse response = FindGroupAccountListResponse.of(
                null);

            // then
            assertThat(response).isNotNull();
            assertThat(response.groupAccounts()).isNull();
        }
    }

    @Nested
    @DisplayName("[accessor] Record accessor 메서드 테스트")
    class Describe_accessor {

        @Test
        @DisplayName("[success] groupAccounts()로 groupAccounts를 조회한다")
        void success() {
            // given
            List<GroupAccount> groupAccounts = new ArrayList<>();
            groupAccounts.add(GroupAccount.builder().id(1L).build());
            FindGroupAccountListResponse response = FindGroupAccountListResponse.builder()
                .groupAccounts(groupAccounts)
                .build();

            // when
            List<GroupAccount> result = response.groupAccounts();

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("[record] Record 불변성 테스트")
    class Describe_immutability {

        @Test
        @DisplayName("[success] FindGroupAccountListResponse는 불변 객체이다")
        void success() {
            // given
            List<GroupAccount> groupAccounts = new ArrayList<>();
            groupAccounts.add(GroupAccount.builder().id(1L).build());

            FindGroupAccountListResponse response1 = new FindGroupAccountListResponse(
                groupAccounts);
            FindGroupAccountListResponse response2 = new FindGroupAccountListResponse(
                groupAccounts);

            // when & then
            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }

        @Test
        @DisplayName("[success] 다른 값으로 생성된 FindGroupAccountListResponse는 동등하지 않다")
        void success_notEqual() {
            // given
            List<GroupAccount> groupAccounts1 = new ArrayList<>();
            groupAccounts1.add(GroupAccount.builder().id(1L).build());

            List<GroupAccount> groupAccounts2 = new ArrayList<>();
            groupAccounts2.add(GroupAccount.builder().id(2L).build());

            FindGroupAccountListResponse response1 = new FindGroupAccountListResponse(
                groupAccounts1);
            FindGroupAccountListResponse response2 = new FindGroupAccountListResponse(
                groupAccounts2);

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
            List<GroupAccount> groupAccounts = new ArrayList<>();
            groupAccounts.add(GroupAccount.builder().id(1L).build());
            FindGroupAccountListResponse response = new FindGroupAccountListResponse(
                groupAccounts);

            // when
            String result = response.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("FindGroupAccountListResponse");
            assertThat(result).contains("groupAccounts");
        }

        @Test
        @DisplayName("[success] null 값을 포함한 toString()을 반환한다")
        void success_nullValue() {
            // given
            FindGroupAccountListResponse response = new FindGroupAccountListResponse(
                null);

            // when
            String result = response.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("FindGroupAccountListResponse");
            assertThat(result).contains("null");
        }
    }
}
