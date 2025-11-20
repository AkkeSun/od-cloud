package com.odcloud.adapter.in.delete_file;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.port.in.command.DeleteFileCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Group;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DeleteFileRequestTest {

    @Nested
    @DisplayName("[toCommand] 입력 객체를 command 로 변환하는 메소드")
    class Describe_toCommand {

        @Test
        @DisplayName("[success] 입력객체를 command 로 잘 변환한다")
        void success() {
            // given
            List<Long> fileIds = List.of(1L, 2L, 3L);
            Account account = Account.builder()
                .email("test@test.com")
                .groups(List.of(Group.of("group-1")))
                .build();

            DeleteFileRequest request = DeleteFileRequest.builder()
                .fileIds(fileIds)
                .build();

            // when
            DeleteFileCommand command = request.toCommand(account);

            // then
            assertThat(command.fileIds()).isEqualTo(fileIds);
            assertThat(command.account()).isEqualTo(account);
        }
    }
}
