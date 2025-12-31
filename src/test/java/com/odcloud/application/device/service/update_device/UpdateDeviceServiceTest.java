package com.odcloud.application.device.service.update_device;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.application.device.port.in.command.UpdateDeviceCommand;
import com.odcloud.domain.model.AccountDevice;
import com.odcloud.fakeClass.FakeAccountDeviceStoragePort;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UpdateDeviceServiceTest {

    private FakeAccountDeviceStoragePort fakeAccountDeviceStoragePort;
    private UpdateDeviceService updateDeviceService;

    @BeforeEach
    void setUp() {
        fakeAccountDeviceStoragePort = new FakeAccountDeviceStoragePort();
        updateDeviceService = new UpdateDeviceService(fakeAccountDeviceStoragePort);
    }

    @Nested
    @DisplayName("[update] 디바이스 정보를 업데이트하는 메서드")
    class Describe_update {

        @Test
        @DisplayName("[success] pushYn과 fcmToken을 모두 업데이트한다")
        void success_updateBoth() {
            // given
            AccountDevice existingDevice = AccountDevice.builder()
                .id(1L)
                .accountId(1L)
                .osType("iOS")
                .deviceId("device-123")
                .appVersion("1.0.0")
                .fcmToken("old-fcm-token")
                .pushYn("Y")
                .lastLoginDt(LocalDateTime.now())
                .regDt(LocalDateTime.now())
                .build();
            fakeAccountDeviceStoragePort.database.add(existingDevice);

            UpdateDeviceCommand command = UpdateDeviceCommand.builder()
                .accountId(1L)
                .osType("iOS")
                .deviceId("device-123")
                .pushYn("N")
                .fcmToken("new-fcm-token")
                .build();

            LocalDateTime before = LocalDateTime.now().minusSeconds(1);

            // when
            UpdateDeviceServiceResponse response = updateDeviceService.update(command);

            // then
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);

            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();

            AccountDevice updatedDevice = fakeAccountDeviceStoragePort.database.get(0);
            assertThat(updatedDevice.getPushYn()).isEqualTo("N");
            assertThat(updatedDevice.getFcmToken()).isEqualTo("new-fcm-token");
            assertThat(updatedDevice.getModDt()).isNotNull();
            assertThat(updatedDevice.getModDt()).isAfter(before);
            assertThat(updatedDevice.getModDt()).isBefore(after);
        }

        @Test
        @DisplayName("[success] pushYn만 업데이트한다")
        void success_updatePushYnOnly() {
            // given
            AccountDevice existingDevice = AccountDevice.builder()
                .id(1L)
                .accountId(1L)
                .osType("Android")
                .deviceId("device-456")
                .appVersion("2.0.0")
                .fcmToken("fcm-token-456")
                .pushYn("Y")
                .lastLoginDt(LocalDateTime.now())
                .regDt(LocalDateTime.now())
                .build();
            fakeAccountDeviceStoragePort.database.add(existingDevice);

            UpdateDeviceCommand command = UpdateDeviceCommand.builder()
                .accountId(1L)
                .osType("Android")
                .deviceId("device-456")
                .pushYn("N")
                .fcmToken(null)
                .build();

            LocalDateTime before = LocalDateTime.now().minusSeconds(1);

            // when
            UpdateDeviceServiceResponse response = updateDeviceService.update(command);

            // then
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);

            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();

            AccountDevice updatedDevice = fakeAccountDeviceStoragePort.database.get(0);
            assertThat(updatedDevice.getPushYn()).isEqualTo("N");
            assertThat(updatedDevice.getFcmToken()).isEqualTo("fcm-token-456");
            assertThat(updatedDevice.getModDt()).isNotNull();
            assertThat(updatedDevice.getModDt()).isAfter(before);
            assertThat(updatedDevice.getModDt()).isBefore(after);
        }

        @Test
        @DisplayName("[success] fcmToken만 업데이트한다")
        void success_updateFcmTokenOnly() {
            // given
            AccountDevice existingDevice = AccountDevice.builder()
                .id(1L)
                .accountId(1L)
                .osType("iOS")
                .deviceId("device-789")
                .appVersion("1.5.0")
                .fcmToken("old-token")
                .pushYn("Y")
                .lastLoginDt(LocalDateTime.now())
                .regDt(LocalDateTime.now())
                .build();
            fakeAccountDeviceStoragePort.database.add(existingDevice);

            UpdateDeviceCommand command = UpdateDeviceCommand.builder()
                .accountId(1L)
                .osType("iOS")
                .deviceId("device-789")
                .pushYn(null)
                .fcmToken("new-token")
                .build();

            LocalDateTime before = LocalDateTime.now().minusSeconds(1);

            // when
            UpdateDeviceServiceResponse response = updateDeviceService.update(command);

            // then
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);

            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();

            AccountDevice updatedDevice = fakeAccountDeviceStoragePort.database.get(0);
            assertThat(updatedDevice.getPushYn()).isEqualTo("Y");
            assertThat(updatedDevice.getFcmToken()).isEqualTo("new-token");
            assertThat(updatedDevice.getModDt()).isNotNull();
            assertThat(updatedDevice.getModDt()).isAfter(before);
            assertThat(updatedDevice.getModDt()).isBefore(after);
        }

        @Test
        @DisplayName("[success] 동일한 값으로 업데이트하면 modDt가 변경되지 않는다")
        void success_noChangeWhenSameValues() {
            // given
            LocalDateTime initialModDt = LocalDateTime.of(2024, 1, 1, 10, 0);

            AccountDevice existingDevice = AccountDevice.builder()
                .id(1L)
                .accountId(1L)
                .osType("Android")
                .deviceId("device-abc")
                .appVersion("1.0.0")
                .fcmToken("same-token")
                .pushYn("Y")
                .modDt(initialModDt)
                .lastLoginDt(LocalDateTime.now())
                .regDt(LocalDateTime.now())
                .build();
            fakeAccountDeviceStoragePort.database.add(existingDevice);

            UpdateDeviceCommand command = UpdateDeviceCommand.builder()
                .accountId(1L)
                .osType("Android")
                .deviceId("device-abc")
                .pushYn("Y")
                .fcmToken("same-token")
                .build();

            // when
            UpdateDeviceServiceResponse response = updateDeviceService.update(command);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();

            AccountDevice updatedDevice = fakeAccountDeviceStoragePort.database.get(0);
            assertThat(updatedDevice.getPushYn()).isEqualTo("Y");
            assertThat(updatedDevice.getFcmToken()).isEqualTo("same-token");
            assertThat(updatedDevice.getModDt()).isEqualTo(initialModDt);
        }

        @Test
        @DisplayName("[error] 존재하지 않는 디바이스를 업데이트하려 하면 예외가 발생한다")
        void error_deviceNotFound() {
            // given
            UpdateDeviceCommand command = UpdateDeviceCommand.builder()
                .accountId(1L)
                .osType("iOS")
                .deviceId("non-existent-device")
                .pushYn("N")
                .fcmToken("new-token")
                .build();

            // when & then
            assertThatThrownBy(() -> updateDeviceService.update(command))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode",
                    ErrorCode.Business_DoesNotExists_DEVICE);
        }
    }
}
