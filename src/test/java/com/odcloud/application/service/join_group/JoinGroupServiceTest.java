package com.odcloud.application.service.join_group;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Group;
import com.odcloud.fakeClass.FakeGroupStoragePort;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class JoinGroupServiceTest {

    private FakeGroupStoragePort fakeGroupStoragePort;
    private JoinGroupService joinGroupService;

    @BeforeEach
    void setUp() {
        fakeGroupStoragePort = new FakeGroupStoragePort();
        joinGroupService = new JoinGroupService(fakeGroupStoragePort);
    }

    @Nested
    @DisplayName("[join] 그룹 가입 요청")
    class Describe_join {

        @Test
        @DisplayName("[success] 정상적으로 그룹 가입 요청을 생성한다")
        void success() {
            // given
            String groupId = "test-group";
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .nickname("User")
                .name("사용자")
                .groups(new ArrayList<>())
                .build();

            Group group = Group.builder()
                .id(groupId)
                .ownerEmail("owner@example.com")
                .name("Test Group")
                .build();
            fakeGroupStoragePort.groupDatabase.add(group);

            // when
            JoinGroupServiceResponse response = joinGroupService.join(groupId, account);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
            assertThat(fakeGroupStoragePort.groupAccountDatabase).hasSize(1);
            assertThat(fakeGroupStoragePort.groupAccountDatabase.get(0).getGroupId()).isEqualTo(
                groupId);
            assertThat(fakeGroupStoragePort.groupAccountDatabase.get(0).getAccountId()).isEqualTo(
                1L);
            assertThat(fakeGroupStoragePort.groupAccountDatabase.get(0).getStatus()).isEqualTo(
                "PENDING");
        }

        @Test
        @DisplayName("[failure] 존재하지 않는 그룹에 가입 요청하면 예외가 발생한다")
        void failure_nonExistentGroup() {
            // given
            String nonExistentGroupId = "non-existent-group";
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .nickname("User")
                .name("사용자")
                .groups(new ArrayList<>())
                .build();

            // when & then
            assertThatThrownBy(() -> joinGroupService.join(nonExistentGroupId, account))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_DoesNotExists_GROUP);

            assertThat(fakeGroupStoragePort.groupAccountDatabase).isEmpty();
        }

        @Test
        @DisplayName("[success] 여러 사용자가 동일한 그룹에 가입 요청할 수 있다")
        void success_multipleUsersJoinSameGroup() {
            // given
            String groupId = "test-group";
            Group group = Group.builder()
                .id(groupId)
                .ownerEmail("owner@example.com")
                .name("Test Group")
                .build();
            fakeGroupStoragePort.groupDatabase.add(group);

            Account account1 = Account.builder()
                .id(1L)
                .email("user1@example.com")
                .nickname("User1")
                .name("사용자1")
                .groups(new ArrayList<>())
                .build();

            Account account2 = Account.builder()
                .id(2L)
                .email("user2@example.com")
                .nickname("User2")
                .name("사용자2")
                .groups(new ArrayList<>())
                .build();

            // when
            joinGroupService.join(groupId, account1);
            joinGroupService.join(groupId, account2);

            // then
            assertThat(fakeGroupStoragePort.groupAccountDatabase).hasSize(2);
            assertThat(fakeGroupStoragePort.groupAccountDatabase.get(0).getAccountId()).isEqualTo(
                1L);
            assertThat(fakeGroupStoragePort.groupAccountDatabase.get(0).getStatus()).isEqualTo(
                "PENDING");
            assertThat(fakeGroupStoragePort.groupAccountDatabase.get(1).getAccountId()).isEqualTo(
                2L);
            assertThat(fakeGroupStoragePort.groupAccountDatabase.get(1).getStatus()).isEqualTo(
                "PENDING");
        }
    }
}
