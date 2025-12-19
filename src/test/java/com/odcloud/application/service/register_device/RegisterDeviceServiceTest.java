package com.odcloud.application.service.register_device;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.port.in.command.RegisterDeviceCommand;
import com.odcloud.domain.model.AccountDevice;
import com.odcloud.fakeClass.FakeAccountDeviceStoragePort;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RegisterDeviceServiceTest {

    private FakeAccountDeviceStoragePort fakeAccountDeviceStoragePort;
    private RegisterDeviceService registerDeviceService;

    @BeforeEach
    void setUp() {
        fakeAccountDeviceStoragePort = new FakeAccountDeviceStoragePort();
        registerDeviceService = new RegisterDeviceService(fakeAccountDeviceStoragePort);
    }

    @Nested
    @DisplayName("[register] 디바이스를 등록하는 메서드")
    class Describe_register {

        @Test
        @DisplayName("[success] 새로운 디바이스를 등록한다")
        void success_newDevice() {
            // given
            RegisterDeviceCommand command = RegisterDeviceCommand.builder()
                .accountId(1L)
                .osType("iOS")
                .deviceId("device-123")
                .appVersion("1.0.0")
                .fcmToken("fcm-token-123")
                .build();

            // when
            RegisterDeviceServiceResponse response = registerDeviceService.register(command);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
            assertThat(fakeAccountDeviceStoragePort.database).hasSize(1);

            AccountDevice savedDevice = fakeAccountDeviceStoragePort.database.get(0);
            assertThat(savedDevice.getAccountId()).isEqualTo(1L);
            assertThat(savedDevice.getOsType()).isEqualTo("iOS");
            assertThat(savedDevice.getDeviceId()).isEqualTo("device-123");
            assertThat(savedDevice.getAppVersion()).isEqualTo("1.0.0");
            assertThat(savedDevice.getFcmToken()).isEqualTo("fcm-token-123");
            assertThat(savedDevice.getPushYn()).isEqualTo("Y");
            assertThat(savedDevice.getLastLoginDt()).isNotNull();
            assertThat(savedDevice.getRegDt()).isNotNull();
        }

        @Test
        @DisplayName("[success] 기존 디바이스 정보를 업데이트한다 - FCM 토큰과 앱 버전 변경")
        void success_updateExistingDevice() {
            // given
            LocalDateTime initialRegDt = LocalDateTime.of(2024, 1, 1, 10, 0);
            LocalDateTime initialLastLoginDt = LocalDateTime.of(2024, 1, 1, 10, 0);

            AccountDevice existingDevice = AccountDevice.builder()
                .id(1L)
                .accountId(1L)
                .osType("iOS")
                .deviceId("device-123")
                .appVersion("1.0.0")
                .fcmToken("old-fcm-token")
                .pushYn("Y")
                .lastLoginDt(initialLastLoginDt)
                .regDt(initialRegDt)
                .build();
            fakeAccountDeviceStoragePort.database.add(existingDevice);

            RegisterDeviceCommand command = RegisterDeviceCommand.builder()
                .accountId(1L)
                .osType("iOS")
                .deviceId("device-123")
                .appVersion("2.0.0")
                .fcmToken("new-fcm-token")
                .build();

            LocalDateTime before = LocalDateTime.now().minusSeconds(1);

            // when
            RegisterDeviceServiceResponse response = registerDeviceService.register(command);

            // then
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);

            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
            assertThat(fakeAccountDeviceStoragePort.database).hasSize(1);

            AccountDevice updatedDevice = fakeAccountDeviceStoragePort.database.get(0);
            assertThat(updatedDevice.getAccountId()).isEqualTo(1L);
            assertThat(updatedDevice.getOsType()).isEqualTo("iOS");
            assertThat(updatedDevice.getDeviceId()).isEqualTo("device-123");
            assertThat(updatedDevice.getAppVersion()).isEqualTo("2.0.0");
            assertThat(updatedDevice.getFcmToken()).isEqualTo("new-fcm-token");
            assertThat(updatedDevice.getLastLoginDt()).isAfter(before);
            assertThat(updatedDevice.getLastLoginDt()).isBefore(after);
            assertThat(updatedDevice.getModDt()).isNotNull();
            assertThat(updatedDevice.getModDt()).isAfter(before);
            assertThat(updatedDevice.getModDt()).isBefore(after);
        }

        @Test
        @DisplayName("[success] 기존 디바이스 정보를 업데이트한다 - FCM 토큰만 변경")
        void success_updateFcmTokenOnly() {
            // given
            LocalDateTime initialRegDt = LocalDateTime.of(2024, 1, 1, 10, 0);
            LocalDateTime initialLastLoginDt = LocalDateTime.of(2024, 1, 1, 10, 0);

            AccountDevice existingDevice = AccountDevice.builder()
                .id(1L)
                .accountId(1L)
                .osType("Android")
                .deviceId("device-456")
                .appVersion("1.5.0")
                .fcmToken("old-fcm-token")
                .pushYn("Y")
                .lastLoginDt(initialLastLoginDt)
                .regDt(initialRegDt)
                .build();
            fakeAccountDeviceStoragePort.database.add(existingDevice);

            RegisterDeviceCommand command = RegisterDeviceCommand.builder()
                .accountId(1L)
                .osType("Android")
                .deviceId("device-456")
                .appVersion("1.5.0")
                .fcmToken("new-fcm-token")
                .build();

            LocalDateTime before = LocalDateTime.now().minusSeconds(1);

            // when
            RegisterDeviceServiceResponse response = registerDeviceService.register(command);

            // then
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);

            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
            assertThat(fakeAccountDeviceStoragePort.database).hasSize(1);

            AccountDevice updatedDevice = fakeAccountDeviceStoragePort.database.get(0);
            assertThat(updatedDevice.getFcmToken()).isEqualTo("new-fcm-token");
            assertThat(updatedDevice.getAppVersion()).isEqualTo("1.5.0");
            assertThat(updatedDevice.getLastLoginDt()).isAfter(before);
            assertThat(updatedDevice.getLastLoginDt()).isBefore(after);
            assertThat(updatedDevice.getModDt()).isNotNull();
            assertThat(updatedDevice.getModDt()).isAfter(before);
            assertThat(updatedDevice.getModDt()).isBefore(after);
        }

        @Test
        @DisplayName("[success] 기존 디바이스와 동일한 정보로 등록 시 lastLoginDt만 업데이트된다")
        void success_sameDeviceInfo() {
            // given
            LocalDateTime initialRegDt = LocalDateTime.of(2024, 1, 1, 10, 0);
            LocalDateTime initialLastLoginDt = LocalDateTime.of(2024, 1, 1, 10, 0);

            AccountDevice existingDevice = AccountDevice.builder()
                .id(1L)
                .accountId(1L)
                .osType("iOS")
                .deviceId("device-789")
                .appVersion("1.0.0")
                .fcmToken("same-fcm-token")
                .pushYn("Y")
                .lastLoginDt(initialLastLoginDt)
                .regDt(initialRegDt)
                .build();
            fakeAccountDeviceStoragePort.database.add(existingDevice);

            RegisterDeviceCommand command = RegisterDeviceCommand.builder()
                .accountId(1L)
                .osType("iOS")
                .deviceId("device-789")
                .appVersion("1.0.0")
                .fcmToken("same-fcm-token")
                .build();

            LocalDateTime before = LocalDateTime.now().minusSeconds(1);

            // when
            RegisterDeviceServiceResponse response = registerDeviceService.register(command);

            // then
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);

            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
            assertThat(fakeAccountDeviceStoragePort.database).hasSize(1);

            AccountDevice updatedDevice = fakeAccountDeviceStoragePort.database.get(0);
            assertThat(updatedDevice.getLastLoginDt()).isAfter(before);
            assertThat(updatedDevice.getLastLoginDt()).isBefore(after);
            assertThat(updatedDevice.getLastLoginDt()).isAfter(initialLastLoginDt);
        }

        @Test
        @DisplayName("[success] 같은 계정의 다른 디바이스를 등록한다")
        void success_differentDevice() {
            // given
            AccountDevice existingDevice = AccountDevice.builder()
                .accountId(1L)
                .osType("iOS")
                .deviceId("device-123")
                .appVersion("1.0.0")
                .fcmToken("fcm-token-123")
                .pushYn("Y")
                .lastLoginDt(LocalDateTime.now())
                .regDt(LocalDateTime.now())
                .build();
            fakeAccountDeviceStoragePort.save(existingDevice);

            RegisterDeviceCommand command = RegisterDeviceCommand.builder()
                .accountId(1L)
                .osType("Android")
                .deviceId("device-456")
                .appVersion("1.0.0")
                .fcmToken("fcm-token-456")
                .build();

            // when
            RegisterDeviceServiceResponse response = registerDeviceService.register(command);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
            assertThat(fakeAccountDeviceStoragePort.database).hasSize(2);

            AccountDevice newDevice = fakeAccountDeviceStoragePort.database.stream()
                .filter(d -> d.getDeviceId().equals("device-456"))
                .findFirst()
                .orElseThrow();
            assertThat(newDevice.getAccountId()).isEqualTo(1L);
            assertThat(newDevice.getOsType()).isEqualTo("Android");
            assertThat(newDevice.getDeviceId()).isEqualTo("device-456");
        }
    }
}
