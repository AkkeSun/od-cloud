package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.domain.model.ApiCallLog;
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
@Table(name = "api_call_log")
class ApiCallLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "api_id")
    private Long apiId;

    @Column(name = "email")
    private String email;

    @Column(name = "request_path_param")
    private String requestPathParam;

    @Column(name = "request_param")
    private String requestParam;

    @Column(name = "request_body")
    private String requestBody;

    @Column(name = "http_status")
    private String httpStatus;

    @Column(name = "error_code")
    private String errorCode;

    @Column(name = "reg_dt")
    private LocalDateTime regDt;


    static ApiCallLogEntity of(ApiCallLog domain) {
        return ApiCallLogEntity.builder()
            .email(domain.getAccountInfo().has("email") ?
                domain.getAccountInfo().get("email").asText() : "")
            .apiId(domain.getApiId())
            .requestPathParam(domain.getRequestPathParam())
            .requestParam(domain.getRequestParam())
            .requestBody(domain.getRequestBody())
            .httpStatus(domain.getHttpStatus())
            .errorCode(domain.getErrorCode())
            .regDt(domain.getRegDt())
            .build();
    }

    ApiCallLog toDomain() {
        return ApiCallLog.builder()
            .apiId(apiId)
            .requestParam(requestParam)
            .requestBody(requestBody)
            .httpStatus(httpStatus)
            .errorCode(errorCode)
            .regDt(regDt)
            .build();
    }
}

