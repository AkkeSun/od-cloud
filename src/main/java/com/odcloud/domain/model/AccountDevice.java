package com.odcloud.domain.model;

import com.google.gson.JsonObject;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDevice {

    private Long id;
    private Long accountId;
    private String osType;
    private String deviceId;
    private String appVersion;
    private String fcmToken;
    private String pushYn;
    private LocalDateTime lastLoginDt;
    private LocalDateTime modDt;
    private LocalDateTime regDt;

    public AccountDevice(Long id, Long accountId, String osType, String deviceId, String fcmToken,
        String appVersion) {
        this.id = id;
        this.accountId = accountId;
        this.osType = osType;
        this.deviceId = deviceId;
        this.fcmToken = fcmToken;
        this.appVersion = appVersion;
    }

    public void updateLastLoginDt() {
        this.lastLoginDt = LocalDateTime.now();
    }

    public boolean needsUpdate(String fcmToken, String appVersion) {
        return !this.fcmToken.equals(fcmToken) || !this.appVersion.equals(appVersion);
    }

    public void updateDeviceInfo(String fcmToken, String appVersion) {
        lastLoginDt = LocalDateTime.now();
        if (!this.fcmToken.equals(fcmToken)) {
            this.fcmToken = fcmToken;
            this.modDt = LocalDateTime.now();
        }
        if (!this.appVersion.equals(appVersion)) {
            this.appVersion = appVersion;
            this.modDt = LocalDateTime.now();
        }
    }

    public void resetFcmToken() {
        this.fcmToken = "RESET";
        this.modDt = LocalDateTime.now();
    }

    public void updateDevice(String pushYn, String fcmToken) {
        if (pushYn != null && !this.pushYn.equals(pushYn)) {
            this.pushYn = pushYn;
            this.modDt = LocalDateTime.now();
        }
        if (fcmToken != null && !this.fcmToken.equals(fcmToken)) {
            this.fcmToken = fcmToken;
            this.modDt = LocalDateTime.now();
        }
    }

    @Override
    public String toString() {
        JsonObject obj = new JsonObject();
        obj.addProperty("accountId", this.accountId);
        obj.addProperty("osType", this.osType);
        obj.addProperty("deviceId", this.deviceId);
        obj.addProperty("appVersion", this.appVersion);
        return obj.toString();
    }
}
