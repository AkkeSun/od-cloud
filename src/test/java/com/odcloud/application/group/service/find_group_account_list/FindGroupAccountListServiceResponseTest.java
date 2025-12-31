package com.odcloud.application.group.service.find_group_account_list;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.domain.model.GroupAccount;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FindGroupAccountListServiceResponseTest {

    @Nested
    @DisplayName("[constructor] Constructor 테스트")
    class Describe_constructor {

        @Test
        @DisplayName("[success] Constructor로 FindGroupAccountListServiceResponse를 생성한다")
        void success() {
            // given
            List<GroupAccount> groupAccounts = new ArrayList<>();
            groupAccounts.add(GroupAccount.builder().id(1L).build());
            groupAccounts.add(GroupAccount.builder().id(2L).build());

            // when
            FindGroupAccountListServiceResponse response = new FindGroupAccountListServiceResponse(
                groupAccounts);

            // then
            assertThat(response).isNotNull();
            assertThat(response.groupAccounts()).hasSize(2);
            assertThat(response.groupAccounts().get(0).getId()).isEqualTo(1L);
            assertThat(response.groupAccounts().get(1).getId()).isEqualTo(2L);
        }

        @Test
        @DisplayName("[success] 빈 리스트로 Constructor로 FindGroupAccountListServiceResponse를 생성한다")
        void success_emptyList() {
            // given
            List<GroupAccount> groupAccounts = new ArrayList<>();

            // when
            FindGroupAccountListServiceResponse response = new FindGroupAccountListServiceResponse(
                groupAccounts);

            // then
            assertThat(response).isNotNull();
            assertThat(response.groupAccounts()).isEmpty();
        }

        @Test
        @DisplayName("[success] null 값으로 Constructor로 FindGroupAccountListServiceResponse를 생성한다")
        void success_nullValue() {
            // when
            FindGroupAccountListServiceResponse response = new FindGroupAccountListServiceResponse(
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
        @DisplayName("[success] Builder로 FindGroupAccountListServiceResponse를 생성한다")
        void success() {
            // given
            List<GroupAccount> groupAccounts = new ArrayList<>();
            groupAccounts.add(GroupAccount.builder().id(1L).build());

            // when
            FindGroupAccountListServiceResponse response = FindGroupAccountListServiceResponse.builder()
                .groupAccounts(groupAccounts)
                .build();

            // then
            assertThat(response).isNotNull();
            assertThat(response.groupAccounts()).hasSize(1);
            assertThat(response.groupAccounts().get(0).getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("[success] Builder로 null 값을 가진 FindGroupAccountListServiceResponse를 생성한다")
        void success_nullValue() {
            // when
            FindGroupAccountListServiceResponse response = FindGroupAccountListServiceResponse.builder()
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
        @DisplayName("[success] of()로 FindGroupAccountListServiceResponse를 생성한다")
        void success() {
            // given
            List<GroupAccount> groupAccounts = new ArrayList<>();
            groupAccounts.add(GroupAccount.builder().id(1L).groupId("group-1").build());
            groupAccounts.add(GroupAccount.builder().id(2L).groupId("group-2").build());

            // when
            FindGroupAccountListServiceResponse response = FindGroupAccountListServiceResponse.of(
                groupAccounts);

            // then
            assertThat(response).isNotNull();
            assertThat(response.groupAccounts()).hasSize(2);
            assertThat(response.groupAccounts().get(0).getId()).isEqualTo(1L);
            assertThat(response.groupAccounts().get(0).getGroupId()).isEqualTo("group-1");
            assertThat(response.groupAccounts().get(1).getId()).isEqualTo(2L);
            assertThat(response.groupAccounts().get(1).getGroupId()).isEqualTo("group-2");
        }

        @Test
        @DisplayName("[success] of()로 빈 리스트를 가진 FindGroupAccountListServiceResponse를 생성한다")
        void success_emptyList() {
            // given
            List<GroupAccount> groupAccounts = new ArrayList<>();

            // when
            FindGroupAccountListServiceResponse response = FindGroupAccountListServiceResponse.of(
                groupAccounts);

            // then
            assertThat(response).isNotNull();
            assertThat(response.groupAccounts()).isEmpty();
        }

        @Test
        @DisplayName("[success] of()로 null을 가진 FindGroupAccountListServiceResponse를 생성한다")
        void success_null() {
            // when
            FindGroupAccountListServiceResponse response = FindGroupAccountListServiceResponse.of(
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
            FindGroupAccountListServiceResponse response = FindGroupAccountListServiceResponse.builder()
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
        @DisplayName("[success] FindGroupAccountListServiceResponse는 불변 객체이다")
        void success() {
            // given
            List<GroupAccount> groupAccounts = new ArrayList<>();
            groupAccounts.add(GroupAccount.builder().id(1L).build());

            FindGroupAccountListServiceResponse response1 = new FindGroupAccountListServiceResponse(
                groupAccounts);
            FindGroupAccountListServiceResponse response2 = new FindGroupAccountListServiceResponse(
                groupAccounts);

            // when & then
            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }

        @Test
        @DisplayName("[success] 다른 값으로 생성된 FindGroupAccountListServiceResponse는 동등하지 않다")
        void success_notEqual() {
            // given
            List<GroupAccount> groupAccounts1 = new ArrayList<>();
            groupAccounts1.add(GroupAccount.builder().id(1L).build());

            List<GroupAccount> groupAccounts2 = new ArrayList<>();
            groupAccounts2.add(GroupAccount.builder().id(2L).build());

            FindGroupAccountListServiceResponse response1 = new FindGroupAccountListServiceResponse(
                groupAccounts1);
            FindGroupAccountListServiceResponse response2 = new FindGroupAccountListServiceResponse(
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
            FindGroupAccountListServiceResponse response = new FindGroupAccountListServiceResponse(
                groupAccounts);

            // when
            String result = response.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("FindGroupAccountListServiceResponse");
            assertThat(result).contains("groupAccounts");
        }

        @Test
        @DisplayName("[success] null 값을 포함한 toString()을 반환한다")
        void success_nullValue() {
            // given
            FindGroupAccountListServiceResponse response = new FindGroupAccountListServiceResponse(
                null);

            // when
            String result = response.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("FindGroupAccountListServiceResponse");
            assertThat(result).contains("null");
        }
    }
}
