package com.odcloud.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AccountDeviceTest {

    @Nested
    @DisplayName("[updateLastLoginDt] 마지막 로그인 시간을 업데이트하는 메서드")
    class Describe_updateLastLoginDt {

        @Test
        @DisplayName("[success] 마지막 로그인 시간을 현재 시간으로 업데이트한다")
        void success() {
            // given
            LocalDateTime initialLastLoginDt = LocalDateTime.of(2024, 1, 1, 10, 0);
            AccountDevice device = AccountDevice.builder()
                .lastLoginDt(initialLastLoginDt)
                .build();

            LocalDateTime before = LocalDateTime.now().minusSeconds(1);

            // when
            device.updateLastLoginDt();

            // then
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);

            assertThat(device.getLastLoginDt()).isAfter(before);
            assertThat(device.getLastLoginDt()).isBefore(after);
            assertThat(device.getLastLoginDt()).isAfter(initialLastLoginDt);
        }
    }

    @Nested
    @DisplayName("[needsUpdate] FCM 토큰 또는 앱 버전 변경 여부를 확인하는 메서드")
    class Describe_needsUpdate {

        @Test
        @DisplayName("[success] FCM 토큰과 앱 버전이 모두 변경된 경우 true를 반환한다")
        void success_bothChanged() {
            // given
            AccountDevice device = AccountDevice.builder()
                .fcmToken("old-fcm-token")
                .appVersion("1.0.0")
                .build();

            // when
            boolean needsUpdate = device.needsUpdate("new-fcm-token", "2.0.0");

            // then
            assertThat(needsUpdate).isTrue();
        }

        @Test
        @DisplayName("[success] FCM 토큰만 변경된 경우 true를 반환한다")
        void success_fcmTokenChanged() {
            // given
            AccountDevice device = AccountDevice.builder()
                .fcmToken("old-fcm-token")
                .appVersion("1.0.0")
                .build();

            // when
            boolean needsUpdate = device.needsUpdate("new-fcm-token", "1.0.0");

            // then
            assertThat(needsUpdate).isTrue();
        }

        @Test
        @DisplayName("[success] 앱 버전만 변경된 경우 true를 반환한다")
        void success_appVersionChanged() {
            // given
            AccountDevice device = AccountDevice.builder()
                .fcmToken("fcm-token")
                .appVersion("1.0.0")
                .build();

            // when
            boolean needsUpdate = device.needsUpdate("fcm-token", "2.0.0");

            // then
            assertThat(needsUpdate).isTrue();
        }

        @Test
        @DisplayName("[success] FCM 토큰과 앱 버전이 모두 동일한 경우 false를 반환한다")
        void success_nothingChanged() {
            // given
            AccountDevice device = AccountDevice.builder()
                .fcmToken("fcm-token")
                .appVersion("1.0.0")
                .build();

            // when
            boolean needsUpdate = device.needsUpdate("fcm-token", "1.0.0");

            // then
            assertThat(needsUpdate).isFalse();
        }
    }

    @Nested
    @DisplayName("[updateDeviceInfo] 디바이스 정보를 업데이트하는 메서드")
    class Describe_updateDeviceInfo {

        @Test
        @DisplayName("[success] FCM 토큰과 앱 버전을 모두 업데이트하고 lastLoginDt와 modDt를 갱신한다")
        void success_updateBoth() {
            // given
            LocalDateTime initialLastLoginDt = LocalDateTime.of(2024, 1, 1, 10, 0);
            LocalDateTime initialModDt = LocalDateTime.of(2024, 1, 1, 10, 0);

            AccountDevice device = AccountDevice.builder()
                .fcmToken("old-fcm-token")
                .appVersion("1.0.0")
                .lastLoginDt(initialLastLoginDt)
                .modDt(initialModDt)
                .build();

            LocalDateTime before = LocalDateTime.now().minusSeconds(1);

            // when
            device.updateDeviceInfo("new-fcm-token", "2.0.0");

            // then
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);

            assertThat(device.getFcmToken()).isEqualTo("new-fcm-token");
            assertThat(device.getAppVersion()).isEqualTo("2.0.0");
            assertThat(device.getLastLoginDt()).isAfter(before);
            assertThat(device.getLastLoginDt()).isBefore(after);
            assertThat(device.getModDt()).isAfter(before);
            assertThat(device.getModDt()).isBefore(after);
        }

        @Test
        @DisplayName("[success] FCM 토큰만 업데이트하고 lastLoginDt와 modDt를 갱신한다")
        void success_updateFcmTokenOnly() {
            // given
            LocalDateTime initialLastLoginDt = LocalDateTime.of(2024, 1, 1, 10, 0);
            LocalDateTime initialModDt = LocalDateTime.of(2024, 1, 1, 10, 0);

            AccountDevice device = AccountDevice.builder()
                .fcmToken("old-fcm-token")
                .appVersion("1.0.0")
                .lastLoginDt(initialLastLoginDt)
                .modDt(initialModDt)
                .build();

            LocalDateTime before = LocalDateTime.now().minusSeconds(1);

            // when
            device.updateDeviceInfo("new-fcm-token", "1.0.0");

            // then
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);

            assertThat(device.getFcmToken()).isEqualTo("new-fcm-token");
            assertThat(device.getAppVersion()).isEqualTo("1.0.0");
            assertThat(device.getLastLoginDt()).isAfter(before);
            assertThat(device.getLastLoginDt()).isBefore(after);
            assertThat(device.getModDt()).isAfter(before);
            assertThat(device.getModDt()).isBefore(after);
        }

        @Test
        @DisplayName("[success] 앱 버전만 업데이트하고 lastLoginDt와 modDt를 갱신한다")
        void success_updateAppVersionOnly() {
            // given
            LocalDateTime initialLastLoginDt = LocalDateTime.of(2024, 1, 1, 10, 0);
            LocalDateTime initialModDt = LocalDateTime.of(2024, 1, 1, 10, 0);

            AccountDevice device = AccountDevice.builder()
                .fcmToken("fcm-token")
                .appVersion("1.0.0")
                .lastLoginDt(initialLastLoginDt)
                .modDt(initialModDt)
                .build();

            LocalDateTime before = LocalDateTime.now().minusSeconds(1);

            // when
            device.updateDeviceInfo("fcm-token", "2.0.0");

            // then
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);

            assertThat(device.getFcmToken()).isEqualTo("fcm-token");
            assertThat(device.getAppVersion()).isEqualTo("2.0.0");
            assertThat(device.getLastLoginDt()).isAfter(before);
            assertThat(device.getLastLoginDt()).isBefore(after);
            assertThat(device.getLastLoginDt()).isAfter(initialLastLoginDt);
        }

        @Test
        @DisplayName("[success] FCM 토큰과 앱 버전이 동일하면 lastLoginDt만 갱신한다")
        void success_noChange() {
            // given
            LocalDateTime initialLastLoginDt = LocalDateTime.of(2024, 1, 1, 10, 0);

            AccountDevice device = AccountDevice.builder()
                .fcmToken("fcm-token")
                .appVersion("1.0.0")
                .lastLoginDt(initialLastLoginDt)
                .build();

            LocalDateTime before = LocalDateTime.now().minusSeconds(1);

            // when
            device.updateDeviceInfo("fcm-token", "1.0.0");

            // then
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);

            assertThat(device.getFcmToken()).isEqualTo("fcm-token");
            assertThat(device.getAppVersion()).isEqualTo("1.0.0");
            assertThat(device.getLastLoginDt()).isAfter(before);
            assertThat(device.getLastLoginDt()).isBefore(after);
            assertThat(device.getLastLoginDt()).isAfter(initialLastLoginDt);
        }
    }

    @Nested
    @DisplayName("[Constructor] Custom Constructor로 AccountDevice를 생성하는 메서드")
    class Describe_Constructor {

        @Test
        @DisplayName("[success] Custom Constructor로 AccountDevice를 생성한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // when
            AccountDevice device = new AccountDevice(
                1L,
                100L,
                "iOS",
                "device-123",
                "1.0.0",
                "fcm-token-123",
                "Y",
                now,
                now,
                now
            );

            // then
            assertThat(device).isNotNull();
            assertThat(device.getId()).isEqualTo(1L);
            assertThat(device.getAccountId()).isEqualTo(100L);
            assertThat(device.getOsType()).isEqualTo("iOS");
            assertThat(device.getDeviceId()).isEqualTo("device-123");
            assertThat(device.getAppVersion()).isEqualTo("1.0.0");
            assertThat(device.getFcmToken()).isEqualTo("fcm-token-123");
            assertThat(device.getPushYn()).isEqualTo("Y");
            assertThat(device.getLastLoginDt()).isEqualTo(now);
            assertThat(device.getModDt()).isEqualTo(now);
            assertThat(device.getRegDt()).isEqualTo(now);
        }

        @Test
        @DisplayName("[success] null 값으로 Custom Constructor로 AccountDevice를 생성한다")
        void success_nullValues() {
            // when
            AccountDevice device = new AccountDevice(
                null, null, null, null, null, null, null, null, null, null
            );

            // then
            assertThat(device).isNotNull();
            assertThat(device.getId()).isNull();
            assertThat(device.getAccountId()).isNull();
            assertThat(device.getOsType()).isNull();
            assertThat(device.getDeviceId()).isNull();
            assertThat(device.getAppVersion()).isNull();
            assertThat(device.getFcmToken()).isNull();
            assertThat(device.getPushYn()).isNull();
            assertThat(device.getLastLoginDt()).isNull();
            assertThat(device.getModDt()).isNull();
            assertThat(device.getRegDt()).isNull();
        }
    }
}
