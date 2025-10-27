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
@Table(name = "TBL_API_CALL_LOG")
class ApiCallLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "API_ID")
    private Long apiId;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "REQUEST_PATH_PARAM")
    private String requestPathParam;

    @Column(name = "REQUEST_PARAM")
    private String requestParam;

    @Column(name = "REQUEST_BODY")
    private String requestBody;

    @Column(name = "HTTP_STATUS")
    private String httpStatus;

    @Column(name = "ERROR_CODE")
    private String errorCode;

    @Column(name = "REG_DATE_TIME")
    private LocalDateTime regDateTime;


    static ApiCallLogEntity of(ApiCallLog domain) {
        return ApiCallLogEntity.builder()
            .username(domain.getAccountInfo().has("username") ?
                domain.getAccountInfo().get("username").asText() : "")
            .apiId(domain.getApiId())
            .requestPathParam(domain.getRequestPathParam())
            .requestParam(domain.getRequestParam())
            .requestBody(domain.getRequestBody())
            .httpStatus(domain.getHttpStatus())
            .errorCode(domain.getErrorCode())
            .regDateTime(domain.getRegDateTime())
            .build();
    }

    ApiCallLog toDomain() {
        return ApiCallLog.builder()
            .apiId(apiId)
            .requestParam(requestParam)
            .requestBody(requestBody)
            .httpStatus(httpStatus)
            .errorCode(errorCode)
            .regDateTime(regDateTime)
            .build();
    }
}

