package com.odcloud.adapter.in.controller.group.find_group_account_list;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.group.service.find_group_account_list.FindGroupAccountListServiceResponse;
import com.odcloud.domain.model.GroupAccount;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FindGroupAccountListResponseTest {

    @Nested
    @DisplayName("[of] ServiceResponse를 Response로 변환하는 정적 팩토리 메서드")
    class Describe_of {

        @Test
        @DisplayName("[success] ServiceResponse를 Response로 변환한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.of(2025, 11, 14, 10, 0);
            List<GroupAccount> groupAccounts = List.of(
                GroupAccount.builder()
                    .id(1L)
                    .groupId(1L)
                    .accountId(100L)
                    .name("홍길동")
                    .nickName("gildong")
                    .email("hong@example.com")
                    .status("APPROVED")
                    .modDt(now)
                    .regDt(now.minusDays(5))
                    .build()
            );

            FindGroupAccountListServiceResponse serviceResponse =
                FindGroupAccountListServiceResponse.of(groupAccounts);

            // when
            FindGroupAccountListResponse response =
                FindGroupAccountListResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.groupAccounts()).hasSize(1);

            FindGroupAccountListResponse.GroupAccountInfo accountInfo = response.groupAccounts()
                .get(0);
            assertThat(accountInfo.id()).isEqualTo(1L);
            assertThat(accountInfo.groupId()).isEqualTo(1L);
            assertThat(accountInfo.accountId()).isEqualTo(100L);
            assertThat(accountInfo.name()).isEqualTo("홍길동");
            assertThat(accountInfo.nickName()).isEqualTo("gildong");
            assertThat(accountInfo.email()).isEqualTo("hong@example.com");
            assertThat(accountInfo.status()).isEqualTo("APPROVED");
            assertThat(accountInfo.updateDt()).isEqualTo(now);
            assertThat(accountInfo.regDt()).isEqualTo(now.minusDays(5));
        }

        @Test
        @DisplayName("[success] 빈 리스트를 포함한 ServiceResponse를 Response로 변환한다")
        void success_emptyList() {
            // given
            FindGroupAccountListServiceResponse serviceResponse =
                FindGroupAccountListServiceResponse.of(List.of());

            // when
            FindGroupAccountListResponse response =
                FindGroupAccountListResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.groupAccounts()).isEmpty();
        }

        @Test
        @DisplayName("[success] 여러 항목을 포함한 ServiceResponse를 Response로 변환한다")
        void success_multipleItems() {
            // given
            LocalDateTime now = LocalDateTime.of(2025, 11, 14, 10, 0);
            List<GroupAccount> groupAccounts = List.of(
                GroupAccount.builder()
                    .id(1L)
                    .groupId(1L)
                    .accountId(100L)
                    .name("홍길동")
                    .nickName("gildong")
                    .email("hong@example.com")
                    .status("ACTIVE")
                    .modDt(now)
                    .regDt(now.minusDays(5))
                    .build(),
                GroupAccount.builder()
                    .id(2L)
                    .groupId(1L)
                    .accountId(200L)
                    .name("김철수")
                    .nickName("chulsoo")
                    .email("kim@example.com")
                    .status("PENDING")
                    .modDt(now.minusDays(1))
                    .regDt(now.minusDays(2))
                    .build()
            );

            FindGroupAccountListServiceResponse serviceResponse =
                FindGroupAccountListServiceResponse.of(groupAccounts);

            // when
            FindGroupAccountListResponse response =
                FindGroupAccountListResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.groupAccounts()).hasSize(2);

            FindGroupAccountListResponse.GroupAccountInfo firstAccount = response.groupAccounts()
                .get(0);
            assertThat(firstAccount.id()).isEqualTo(1L);
            assertThat(firstAccount.name()).isEqualTo("홍길동");
            assertThat(firstAccount.status()).isEqualTo("ACTIVE");

            FindGroupAccountListResponse.GroupAccountInfo secondAccount = response.groupAccounts()
                .get(1);
            assertThat(secondAccount.id()).isEqualTo(2L);
            assertThat(secondAccount.name()).isEqualTo("김철수");
            assertThat(secondAccount.status()).isEqualTo("PENDING");
        }

        @Test
        @DisplayName("[success] null 값을 포함한 GroupAccount를 Response로 변환한다")
        void success_withNullValues() {
            // given
            LocalDateTime now = LocalDateTime.of(2025, 11, 14, 10, 0);
            List<GroupAccount> groupAccounts = List.of(
                GroupAccount.builder()
                    .id(1L)
                    .groupId(1L)
                    .accountId(100L)
                    .name(null)
                    .nickName(null)
                    .email(null)
                    .status("ACTIVE")
                    .modDt(now)
                    .regDt(now)
                    .build()
            );

            FindGroupAccountListServiceResponse serviceResponse =
                FindGroupAccountListServiceResponse.of(groupAccounts);

            // when
            FindGroupAccountListResponse response =
                FindGroupAccountListResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.groupAccounts()).hasSize(1);

            FindGroupAccountListResponse.GroupAccountInfo accountInfo = response.groupAccounts()
                .get(0);
            assertThat(accountInfo.name()).isNull();
            assertThat(accountInfo.nickName()).isNull();
            assertThat(accountInfo.email()).isNull();
        }
    }

    @Nested
    @DisplayName("[constructor] Response 생성자 테스트")
    class Describe_constructor {

        @Test
        @DisplayName("[success] groupAccounts 리스트로 Response를 생성한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.of(2025, 11, 14, 10, 0);
            List<FindGroupAccountListResponse.GroupAccountInfo> groupAccounts = List.of(
                FindGroupAccountListResponse.GroupAccountInfo.builder()
                    .id(1L)
                    .groupId(1L)
                    .accountId(100L)
                    .name("홍길동")
                    .nickName("gildong")
                    .email("hong@example.com")
                    .status("APPROVED")
                    .updateDt(now)
                    .regDt(now.minusDays(5))
                    .build()
            );

            // when
            FindGroupAccountListResponse response =
                FindGroupAccountListResponse.builder()
                    .groupAccounts(groupAccounts)
                    .build();

            // then
            assertThat(response).isNotNull();
            assertThat(response.groupAccounts()).hasSize(1);
            assertThat(response.groupAccounts().get(0).name()).isEqualTo("홍길동");
        }

        @Test
        @DisplayName("[success] 빈 리스트로 Response를 생성한다")
        void success_emptyList() {
            // when
            FindGroupAccountListResponse response =
                FindGroupAccountListResponse.builder()
                    .groupAccounts(List.of())
                    .build();

            // then
            assertThat(response.groupAccounts()).isEmpty();
        }
    }

    @Nested
    @DisplayName("[record] Record 불변성 테스트")
    class Describe_immutability {

        @Test
        @DisplayName("[success] Response는 불변 객체이다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.of(2025, 11, 14, 10, 0);
            List<FindGroupAccountListResponse.GroupAccountInfo> groupAccounts = List.of(
                FindGroupAccountListResponse.GroupAccountInfo.builder()
                    .id(1L)
                    .groupId(1L)
                    .accountId(100L)
                    .name("홍길동")
                    .nickName("gildong")
                    .email("hong@example.com")
                    .status("APPROVED")
                    .updateDt(now)
                    .regDt(now.minusDays(5))
                    .build()
            );

            FindGroupAccountListResponse response1 =
                FindGroupAccountListResponse.builder()
                    .groupAccounts(groupAccounts)
                    .build();

            FindGroupAccountListResponse response2 =
                FindGroupAccountListResponse.builder()
                    .groupAccounts(groupAccounts)
                    .build();

            // when & then
            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }

        @Test
        @DisplayName("[success] 다른 값으로 생성된 Response는 동등하지 않다")
        void success_notEqual() {
            // given
            LocalDateTime now = LocalDateTime.of(2025, 11, 14, 10, 0);
            List<FindGroupAccountListResponse.GroupAccountInfo> groupAccounts1 = List.of(
                FindGroupAccountListResponse.GroupAccountInfo.builder()
                    .id(1L)
                    .groupId(1L)
                    .accountId(100L)
                    .name("홍길동")
                    .nickName("gildong")
                    .email("hong@example.com")
                    .status("APPROVED")
                    .updateDt(now)
                    .regDt(now.minusDays(5))
                    .build()
            );

            List<FindGroupAccountListResponse.GroupAccountInfo> groupAccounts2 = List.of(
                FindGroupAccountListResponse.GroupAccountInfo.builder()
                    .id(2L)
                    .groupId(1L)
                    .accountId(200L)
                    .name("김철수")
                    .nickName("chulsoo")
                    .email("kim@example.com")
                    .status("PENDING")
                    .updateDt(now)
                    .regDt(now)
                    .build()
            );

            FindGroupAccountListResponse response1 =
                FindGroupAccountListResponse.builder()
                    .groupAccounts(groupAccounts1)
                    .build();

            FindGroupAccountListResponse response2 =
                FindGroupAccountListResponse.builder()
                    .groupAccounts(groupAccounts2)
                    .build();

            // when & then
            assertThat(response1).isNotEqualTo(response2);
        }
    }

    @Nested
    @DisplayName("[accessor] Response accessor 메서드 테스트")
    class Describe_accessor {

        @Test
        @DisplayName("[success] groupAccounts()로 값을 조회한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.of(2025, 11, 14, 10, 0);
            List<FindGroupAccountListResponse.GroupAccountInfo> groupAccounts = List.of(
                FindGroupAccountListResponse.GroupAccountInfo.builder()
                    .id(1L)
                    .groupId(1L)
                    .accountId(100L)
                    .name("홍길동")
                    .nickName("gildong")
                    .email("hong@example.com")
                    .status("APPROVED")
                    .updateDt(now)
                    .regDt(now.minusDays(5))
                    .build()
            );

            FindGroupAccountListResponse response =
                FindGroupAccountListResponse.builder()
                    .groupAccounts(groupAccounts)
                    .build();

            // when
            List<FindGroupAccountListResponse.GroupAccountInfo> result = response.groupAccounts();

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).name()).isEqualTo("홍길동");
        }
    }

    @Nested
    @DisplayName("[toString] Response toString 메서드 테스트")
    class Describe_toString {

        @Test
        @DisplayName("[success] toString()으로 문자열 표현을 반환한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.of(2025, 11, 14, 10, 0);
            List<FindGroupAccountListResponse.GroupAccountInfo> groupAccounts = List.of(
                FindGroupAccountListResponse.GroupAccountInfo.builder()
                    .id(1L)
                    .groupId(1L)
                    .accountId(100L)
                    .name("홍길동")
                    .nickName("gildong")
                    .email("hong@example.com")
                    .status("APPROVED")
                    .updateDt(now)
                    .regDt(now.minusDays(5))
                    .build()
            );

            FindGroupAccountListResponse response =
                FindGroupAccountListResponse.builder()
                    .groupAccounts(groupAccounts)
                    .build();

            // when
            String result = response.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("FindGroupAccountListResponse");
            assertThat(result).contains("groupAccounts");
        }
    }

    @Nested
    @DisplayName("[GroupAccountInfo] 중첩 Record 테스트")
    class Describe_GroupAccountInfo {

        @Test
        @DisplayName("[success] GroupAccountInfo를 생성한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.of(2025, 11, 14, 10, 0);

            // when
            FindGroupAccountListResponse.GroupAccountInfo accountInfo =
                FindGroupAccountListResponse.GroupAccountInfo.builder()
                    .id(1L)
                    .groupId(1L)
                    .accountId(100L)
                    .name("홍길동")
                    .nickName("gildong")
                    .email("hong@example.com")
                    .status("APPROVED")
                    .updateDt(now)
                    .regDt(now.minusDays(5))
                    .build();

            // then
            assertThat(accountInfo).isNotNull();
            assertThat(accountInfo.id()).isEqualTo(1L);
            assertThat(accountInfo.groupId()).isEqualTo(1L);
            assertThat(accountInfo.accountId()).isEqualTo(100L);
            assertThat(accountInfo.name()).isEqualTo("홍길동");
            assertThat(accountInfo.nickName()).isEqualTo("gildong");
            assertThat(accountInfo.email()).isEqualTo("hong@example.com");
            assertThat(accountInfo.status()).isEqualTo("APPROVED");
            assertThat(accountInfo.updateDt()).isEqualTo(now);
            assertThat(accountInfo.regDt()).isEqualTo(now.minusDays(5));
        }

        @Test
        @DisplayName("[success] GroupAccountInfo는 불변 객체이다")
        void success_immutability() {
            // given
            LocalDateTime now = LocalDateTime.of(2025, 11, 14, 10, 0);

            FindGroupAccountListResponse.GroupAccountInfo accountInfo1 =
                FindGroupAccountListResponse.GroupAccountInfo.builder()
                    .id(1L)
                    .groupId(1L)
                    .accountId(100L)
                    .name("홍길동")
                    .nickName("gildong")
                    .email("hong@example.com")
                    .status("APPROVED")
                    .updateDt(now)
                    .regDt(now.minusDays(5))
                    .build();

            FindGroupAccountListResponse.GroupAccountInfo accountInfo2 =
                FindGroupAccountListResponse.GroupAccountInfo.builder()
                    .id(1L)
                    .groupId(1L)
                    .accountId(100L)
                    .name("홍길동")
                    .nickName("gildong")
                    .email("hong@example.com")
                    .status("APPROVED")
                    .updateDt(now)
                    .regDt(now.minusDays(5))
                    .build();

            // when & then
            assertThat(accountInfo1).isEqualTo(accountInfo2);
            assertThat(accountInfo1.hashCode()).isEqualTo(accountInfo2.hashCode());
        }

        @Test
        @DisplayName("[success] GroupAccountInfo toString()으로 문자열 표현을 반환한다")
        void success_toString() {
            // given
            LocalDateTime now = LocalDateTime.of(2025, 11, 14, 10, 0);

            FindGroupAccountListResponse.GroupAccountInfo accountInfo =
                FindGroupAccountListResponse.GroupAccountInfo.builder()
                    .id(1L)
                    .groupId(1L)
                    .accountId(100L)
                    .name("홍길동")
                    .nickName("gildong")
                    .email("hong@example.com")
                    .status("APPROVED")
                    .updateDt(now)
                    .regDt(now.minusDays(5))
                    .build();

            // when
            String result = accountInfo.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("GroupAccountInfo");
            assertThat(result).contains("홍길동");
        }
    }
}
