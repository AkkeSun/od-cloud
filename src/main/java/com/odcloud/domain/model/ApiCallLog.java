package com.odcloud.domain.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.AntPathMatcher;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiCallLog {

    private ObjectNode accountInfo;
    private Long apiId;
    private String uri;
    private String httpMethod;
    private String requestPathParam;
    private String requestParam;
    private String requestBody;
    private String responseBody;
    private String httpStatus;
    private String errorCode;
    private LocalDateTime regDateTime;

    public void updateResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public void updateHttpStatus(String httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void updateErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void updateApiId(Long apiId) {
        this.apiId = apiId;
    }

    public void updateRequestPathParam(ApiInfo apiInfo) {
        try {
            this.requestPathParam = new AntPathMatcher()
                .extractUriTemplateVariables(apiInfo.uriPattern(), uri).toString();
        } catch (Exception e) {
            this.requestPathParam = "";
        }
    }

    public String getRequestLog() {
        return String.format("[%s %s] request - {\"param\": %s, \"body\": %s, \"account\": %s}",
            httpMethod, uri, requestParam, requestBody, accountInfo);
    }

    public String getResponseLog() {
        return String.format("[%s %s] response - %s", httpMethod, uri, responseBody);
    }
}
