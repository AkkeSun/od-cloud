package com.odcloud.adapter.out.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account_device")
class AccountDeviceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "os_type")
    private String osType;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "app_version")
    private String appVersion;

    @Column(name = "fcm_token")
    private String fcmToken;

    @Column(name = "push_yn")
    private String pushYn;

    @Column(name = "last_login_dt")
    private LocalDateTime lastLoginDt;

    @Column(name = "mod_dt")
    private LocalDateTime modDt;

    @Column(name = "reg_dt")
    private LocalDateTime regDt;
}
