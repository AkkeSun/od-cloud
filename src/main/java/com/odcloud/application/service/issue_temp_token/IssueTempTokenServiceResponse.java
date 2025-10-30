package com.odcloud.application.service.issue_temp_token;

public record IssueTempTokenServiceResponse(
    String tempToken
) {

    public static IssueTempTokenServiceResponse of(String tempToken) {
        return new IssueTempTokenServiceResponse(tempToken);
    }
}
