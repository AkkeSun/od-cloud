package com.odcloud.adapter.in.controller.file.find_files;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.port.in.command.FindFilesCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Group;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FindFilesRequestTest {

    @Nested
    @DisplayName("[toCommand] Request를 Command로 변환")
    class Describe_toCommand {

        @Test
        @DisplayName("[success] 모든 필드를 포함한 Command를 생성한다")
        void success_allFields() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(List.of(Group.of("group1")))
                .build();

            FindFilesRequest request = FindFilesRequest.builder()
                .sortType("NAME_ASC")
                .folderId(10L)
                .groupId("group1")
                .keyword("test")
                .build();

            // when
            FindFilesCommand command = request.toCommand(account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.account()).isEqualTo(account);
            assertThat(command.sortType()).isEqualTo("NAME_ASC");
            assertThat(command.folderId()).isEqualTo(10L);
            assertThat(command.groupId()).isEqualTo("group1");
            assertThat(command.keyword()).isEqualTo("test");
        }

        @Test
        @DisplayName("[success] folderId로 검색하는 Command를 생성한다")
        void success_byFolderId() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(List.of(Group.of("group1")))
                .build();

            FindFilesRequest request = FindFilesRequest.builder()
                .sortType("REG_DT_DESC")
                .folderId(10L)
                .build();

            // when
            FindFilesCommand command = request.toCommand(account);

            // then
            assertThat(command.account()).isEqualTo(account);
            assertThat(command.sortType()).isEqualTo("REG_DT_DESC");
            assertThat(command.folderId()).isEqualTo(10L);
            assertThat(command.groupId()).isNull();
            assertThat(command.keyword()).isNull();
        }

        @Test
        @DisplayName("[success] keyword로 검색하는 Command를 생성한다")
        void success_byKeyword() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(List.of(Group.of("group1")))
                .build();

            FindFilesRequest request = FindFilesRequest.builder()
                .sortType("NAME_DESC")
                .keyword("report")
                .build();

            // when
            FindFilesCommand command = request.toCommand(account);

            // then
            assertThat(command.account()).isEqualTo(account);
            assertThat(command.sortType()).isEqualTo("NAME_DESC");
            assertThat(command.keyword()).isEqualTo("report");
            assertThat(command.folderId()).isNull();
            assertThat(command.groupId()).isNull();
        }

        @Test
        @DisplayName("[success] groupId로 필터링하는 Command를 생성한다")
        void success_withGroupId() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(List.of(Group.of("group1"), Group.of("group2")))
                .build();

            FindFilesRequest request = FindFilesRequest.builder()
                .sortType("NAME_ASC")
                .folderId(0L)
                .groupId("group2")
                .build();

            // when
            FindFilesCommand command = request.toCommand(account);

            // then
            assertThat(command.account()).isEqualTo(account);
            assertThat(command.folderId()).isEqualTo(0L);
            assertThat(command.groupId()).isEqualTo("group2");
        }

        @Test
        @DisplayName("[success] 다양한 sortType으로 Command를 생성한다")
        void success_variousSortTypes() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(List.of(Group.of("group1")))
                .build();

            List<String> sortTypes = List.of("NAME_ASC", "NAME_DESC", "REG_DT_ASC", "REG_DT_DESC");

            for (String sortType : sortTypes) {
                FindFilesRequest request = FindFilesRequest.builder()
                    .sortType(sortType)
                    .folderId(1L)
                    .build();

                // when
                FindFilesCommand command = request.toCommand(account);

                // then
                assertThat(command.sortType()).isEqualTo(sortType);
            }
        }

        @Test
        @DisplayName("[success] sortType이 null이어도 Command를 생성한다")
        void success_nullSortType() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(List.of(Group.of("group1")))
                .build();

            FindFilesRequest request = FindFilesRequest.builder()
                .folderId(1L)
                .sortType(null)
                .build();

            // when
            FindFilesCommand command = request.toCommand(account);

            // then
            assertThat(command.sortType()).isNull();
            assertThat(command.account()).isEqualTo(account);
            assertThat(command.folderId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("[success] 최소한의 필드로 Command를 생성한다")
        void success_minimalFields() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(List.of())
                .build();

            FindFilesRequest request = FindFilesRequest.builder()
                .build();

            // when
            FindFilesCommand command = request.toCommand(account);

            // then
            assertThat(command.account()).isEqualTo(account);
            assertThat(command.sortType()).isNull();
            assertThat(command.folderId()).isNull();
            assertThat(command.groupId()).isNull();
            assertThat(command.keyword()).isNull();
        }
    }

    @Nested
    @DisplayName("[builder] Builder로 Request 생성")
    class Describe_builder {

        @Test
        @DisplayName("[success] Builder로 Request를 생성한다")
        void success() {
            // given & when
            FindFilesRequest request = FindFilesRequest.builder()
                .sortType("NAME_ASC")
                .folderId(10L)
                .groupId("group1")
                .keyword("test")
                .build();

            // then
            assertThat(request).isNotNull();
            assertThat(request.getSortType()).isEqualTo("NAME_ASC");
            assertThat(request.getFolderId()).isEqualTo(10L);
            assertThat(request.getGroupId()).isEqualTo("group1");
            assertThat(request.getKeyword()).isEqualTo("test");
        }

        @Test
        @DisplayName("[success] 빈 Builder로 Request를 생성한다")
        void success_empty() {
            // given & when
            FindFilesRequest request = FindFilesRequest.builder()
                .build();

            // then
            assertThat(request).isNotNull();
            assertThat(request.getSortType()).isNull();
            assertThat(request.getFolderId()).isNull();
            assertThat(request.getGroupId()).isNull();
            assertThat(request.getKeyword()).isNull();
        }

        @Test
        @DisplayName("[success] NoArgsConstructor로 Request를 생성한다")
        void success_noArgsConstructor() {
            // given & when
            FindFilesRequest request = new FindFilesRequest();

            // then
            assertThat(request).isNotNull();
            assertThat(request.getSortType()).isNull();
            assertThat(request.getFolderId()).isNull();
            assertThat(request.getGroupId()).isNull();
            assertThat(request.getKeyword()).isNull();
        }

        @Test
        @DisplayName("[success] AllArgsConstructor로 Request를 생성한다")
        void success_allArgsConstructor() {
            // given & when
            FindFilesRequest request = new FindFilesRequest(
                "NAME_ASC",
                10L,
                "group1",
                "test"
            );

            // then
            assertThat(request).isNotNull();
            assertThat(request.getSortType()).isEqualTo("NAME_ASC");
            assertThat(request.getFolderId()).isEqualTo(10L);
            assertThat(request.getGroupId()).isEqualTo("group1");
            assertThat(request.getKeyword()).isEqualTo("test");
        }
    }
}
