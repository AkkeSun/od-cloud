package com.odcloud.application.service.delete_device;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.application.port.in.command.DeleteDeviceCommand;
import com.odcloud.domain.model.AccountDevice;
import com.odcloud.fakeClass.FakeAccountDeviceStoragePort;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DeleteDeviceServiceTest {

    private FakeAccountDeviceStoragePort fakeAccountDeviceStoragePort;
    private DeleteDeviceService deleteDeviceService;

    @BeforeEach
    void setUp() {
        fakeAccountDeviceStoragePort = new FakeAccountDeviceStoragePort();
        deleteDeviceService = new DeleteDeviceService(fakeAccountDeviceStoragePort);
    }

    @Nested
    @DisplayName("[delete] 디바이스를 삭제하는 메서드")
    class Describe_delete {

        @Test
        @DisplayName("[success] 디바이스를 정상적으로 삭제한다")
        void success() {
            // given
            AccountDevice device = AccountDevice.builder()
                .accountId(1L)
                .osType("iOS")
                .deviceId("device-123")
                .appVersion("1.0.0")
                .fcmToken("fcm-token-123")
                .pushYn("Y")
                .lastLoginDt(LocalDateTime.now())
                .regDt(LocalDateTime.now())
                .build();
            AccountDevice savedDevice = fakeAccountDeviceStoragePort.save(device);

            DeleteDeviceCommand command = DeleteDeviceCommand.builder()
                .accountId(1L)
                .osType("iOS")
                .deviceId("device-123")
                .build();

            // when
            DeleteDeviceServiceResponse response = deleteDeviceService.delete(command);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
            assertThat(fakeAccountDeviceStoragePort.database).isEmpty();
        }

        @Test
        @DisplayName("[failure] 존재하지 않는 디바이스 삭제 시 예외가 발생한다")
        void failure_deviceNotFound() {
            // given
            DeleteDeviceCommand command = DeleteDeviceCommand.builder()
                .accountId(1L)
                .osType("iOS")
                .deviceId("device-999")
                .build();

            // when & then
            assertThatThrownBy(() -> deleteDeviceService.delete(command))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode",
                    ErrorCode.Business_DoesNotExists_DEVICE);
        }

        @Test
        @DisplayName("[failure] 다른 사용자의 디바이스 삭제 시 예외가 발생한다")
        void failure_notOwnDevice() {
            // given
            AccountDevice device = AccountDevice.builder()
                .accountId(1L)
                .osType("iOS")
                .deviceId("device-123")
                .appVersion("1.0.0")
                .fcmToken("fcm-token-123")
                .pushYn("Y")
                .lastLoginDt(LocalDateTime.now())
                .regDt(LocalDateTime.now())
                .build();
            fakeAccountDeviceStoragePort.save(device);

            DeleteDeviceCommand command = DeleteDeviceCommand.builder()
                .accountId(2L)
                .osType("iOS")
                .deviceId("device-123")
                .build();

            // when & then
            assertThatThrownBy(() -> deleteDeviceService.delete(command))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode",
                    ErrorCode.Business_DoesNotExists_DEVICE);

            assertThat(fakeAccountDeviceStoragePort.database).hasSize(1);
        }

        @Test
        @DisplayName("[success] 같은 계정의 여러 디바이스 중 하나만 삭제한다")
        void success_deleteOneOfMultipleDevices() {
            // given
            AccountDevice device1 = AccountDevice.builder()
                .accountId(1L)
                .osType("iOS")
                .deviceId("device-123")
                .appVersion("1.0.0")
                .fcmToken("fcm-token-123")
                .pushYn("Y")
                .lastLoginDt(LocalDateTime.now())
                .regDt(LocalDateTime.now())
                .build();
            fakeAccountDeviceStoragePort.save(device1);

            AccountDevice device2 = AccountDevice.builder()
                .accountId(1L)
                .osType("Android")
                .deviceId("device-456")
                .appVersion("1.0.0")
                .fcmToken("fcm-token-456")
                .pushYn("Y")
                .lastLoginDt(LocalDateTime.now())
                .regDt(LocalDateTime.now())
                .build();
            fakeAccountDeviceStoragePort.save(device2);

            DeleteDeviceCommand command = DeleteDeviceCommand.builder()
                .accountId(1L)
                .osType("iOS")
                .deviceId("device-123")
                .build();

            // when
            DeleteDeviceServiceResponse response = deleteDeviceService.delete(command);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
            assertThat(fakeAccountDeviceStoragePort.database).hasSize(1);
            assertThat(fakeAccountDeviceStoragePort.database.get(0).getDeviceId()).isEqualTo(
                "device-456");
        }
    }
}
