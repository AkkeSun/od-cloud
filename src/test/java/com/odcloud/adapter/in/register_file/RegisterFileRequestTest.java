package com.odcloud.adapter.in.register_file;

import com.odcloud.application.port.in.command.RegisterFileCommand;
import java.util.ArrayList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RegisterFileRequestTest {

    @Nested
    @DisplayName("[toCommand] 입력 객체를 command 로 변환하는 메소드")
    class Describe_toCommand {

        @Test
        @DisplayName("[success] 입력객체를 command 로 잘 변환한다")
        void success() {
            // given
            RegisterFileRequest request = RegisterFileRequest.builder()
                .folderId(10L)
                .files(new ArrayList<>())
                .build();

            // when
            RegisterFileCommand command = request.toCommand();

            // then
            assert command.folderId().equals(request.getFolderId());
        }
    }
}