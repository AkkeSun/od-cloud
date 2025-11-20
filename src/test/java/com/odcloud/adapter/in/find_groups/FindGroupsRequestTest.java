package com.odcloud.adapter.in.find_groups;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.port.in.command.FindGroupsCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FindGroupsRequestTest {

    @Nested
    @DisplayName("[toCommand] Request를 Command로 변환")
    class Describe_toCommand {

        @Test
        @DisplayName("[success] keyword를 포함한 Command를 생성한다")
        void success() {
            // given
            FindGroupsRequest request = new FindGroupsRequest();
            request.setKeyword("test");

            // when
            FindGroupsCommand command = request.toCommand();

            // then
            assertThat(command).isNotNull();
            assertThat(command.keyword()).isEqualTo("test");
        }

        @Test
        @DisplayName("[success] 'all' keyword로 Command를 생성한다")
        void success_allKeyword() {
            // given
            FindGroupsRequest request = new FindGroupsRequest();
            request.setKeyword("all");

            // when
            FindGroupsCommand command = request.toCommand();

            // then
            assertThat(command).isNotNull();
            assertThat(command.keyword()).isEqualTo("all");
        }

        @Test
        @DisplayName("[success] 다양한 keyword로 Command를 생성한다")
        void success_variousKeywords() {
            // given
            FindGroupsRequest request1 = new FindGroupsRequest();
            request1.setKeyword("개발팀");

            FindGroupsRequest request2 = new FindGroupsRequest();
            request2.setKeyword("마케팅");

            // when
            FindGroupsCommand command1 = request1.toCommand();
            FindGroupsCommand command2 = request2.toCommand();

            // then
            assertThat(command1.keyword()).isEqualTo("개발팀");
            assertThat(command2.keyword()).isEqualTo("마케팅");
        }
    }
}
