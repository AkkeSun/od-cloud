package com.odcloud.application.service.register_group;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.odcloud.application.port.in.command.RegisterGroupCommand;
import com.odcloud.fakeClass.FakeGroupStoragePort;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

@ExtendWith(OutputCaptureExtension.class)
class RegisterGroupServiceTest {

    RegisterGroupService service;
    FakeGroupStoragePort fakeGroupStoragePort;

    RegisterGroupServiceTest() {
        fakeGroupStoragePort = new FakeGroupStoragePort();
        service = new RegisterGroupService(fakeGroupStoragePort);
    }

    @BeforeEach
    void setUp() {
        fakeGroupStoragePort.database.clear();
        fakeGroupStoragePort.shouldThrowException = false;
    }

    @Nested
    @DisplayName("[register] 그룹을 등록하는 메서드")
    class Describe_register {

        @Test
        @DisplayName("[success] 그룹 등록에 성공한다")
        void success(CapturedOutput output) {
            // given
            RegisterGroupCommand command = RegisterGroupCommand.builder()
                .id("test-group")
                .description("테스트 그룹")
                .build();

            // when
            RegisterGroupServiceResponse response = service.register(command);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();

            assertThat(fakeGroupStoragePort.database).hasSize(1);
            assertThat(fakeGroupStoragePort.database.get(0).id()).isEqualTo("test-group");
            assertThat(fakeGroupStoragePort.database.get(0).description()).isEqualTo("테스트 그룹");
            assertThat(fakeGroupStoragePort.database.get(0).regDt()).isNotNull();

            assertThat(output.toString()).contains("FakeGroupStoragePort registered");
            assertThat(output.toString()).contains("test-group");
        }

        @Test
        @DisplayName("[success] 등록 시간이 자동으로 기록된다")
        void success_regDateTimeRecorded() {
            // given
            RegisterGroupCommand command = RegisterGroupCommand.builder()
                .id("group-with-timestamp")
                .description("시간 기록 테스트")
                .build();

            // when
            service.register(command);

            // then
            assertThat(fakeGroupStoragePort.database).hasSize(1);
            assertThat(fakeGroupStoragePort.database.get(0).regDt()).isNotNull();
        }

        @Test
        @DisplayName("[success] 여러 그룹을 등록할 수 있다")
        void success_multipleGroups() {
            // given
            RegisterGroupCommand command1 = RegisterGroupCommand.builder()
                .id("group-1")
                .description("그룹 1")
                .build();

            RegisterGroupCommand command2 = RegisterGroupCommand.builder()
                .id("group-2")
                .description("그룹 2")
                .build();

            // when
            service.register(command1);
            service.register(command2);

            // then
            assertThat(fakeGroupStoragePort.database).hasSize(2);
            assertThat(fakeGroupStoragePort.database.get(0).id()).isEqualTo("group-1");
            assertThat(fakeGroupStoragePort.database.get(1).id()).isEqualTo("group-2");
        }

        @Test
        @DisplayName("[success] 다양한 설명으로 그룹 등록이 가능하다")
        void success_variousDescriptions() {
            // given
            RegisterGroupCommand command = RegisterGroupCommand.builder()
                .id("special-group")
                .description("특수 문자 포함 설명!@#$%")
                .build();

            // when
            RegisterGroupServiceResponse response = service.register(command);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
            assertThat(fakeGroupStoragePort.database).hasSize(1);
            assertThat(fakeGroupStoragePort.database.get(0).description()).isEqualTo("특수 문자 포함 설명!@#$%");
        }

        @Test
        @DisplayName("[error] 이미 등록된 그룹 ID가 존재하면 CustomBusinessException을 던진다")
        void error_duplicateGroupId() {
            // given
            RegisterGroupCommand firstCommand = RegisterGroupCommand.builder()
                .id("existing-group")
                .description("기존 그룹")
                .build();
            service.register(firstCommand);

            RegisterGroupCommand duplicateCommand = RegisterGroupCommand.builder()
                .id("existing-group")
                .description("새 그룹")
                .build();

            // when & then
            CustomBusinessException exception = assertThrows(
                CustomBusinessException.class,
                () -> service.register(duplicateCommand)
            );

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.Business_SAVED_GROUP);
            assertThat(exception.getErrorCode().getMessage()).isEqualTo("등록된 그룹 정보 입니다");

            assertThat(fakeGroupStoragePort.database).hasSize(1);
            assertThat(fakeGroupStoragePort.database.get(0).description()).isEqualTo("기존 그룹");
        }

        @Test
        @DisplayName("[error] 중복 체크가 정확히 동작한다")
        void error_duplicateCheckWorks() {
            // given
            RegisterGroupCommand command1 = RegisterGroupCommand.builder()
                .id("group-1")
                .description("그룹 1")
                .build();

            RegisterGroupCommand command2 = RegisterGroupCommand.builder()
                .id("group-2")
                .description("그룹 2")
                .build();

            RegisterGroupCommand duplicateCommand = RegisterGroupCommand.builder()
                .id("group-1")
                .description("중복된 그룹")
                .build();

            // when
            service.register(command1);
            service.register(command2);

            // then
            CustomBusinessException exception = assertThrows(
                CustomBusinessException.class,
                () -> service.register(duplicateCommand)
            );

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.Business_SAVED_GROUP);
            assertThat(fakeGroupStoragePort.database).hasSize(2);
        }
    }
}
