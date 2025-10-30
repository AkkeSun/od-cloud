package com.odcloud.adapter.in.issue_temp_token;

import com.odcloud.application.service.issue_temp_token.IssueTempTokenServiceResponse;

record IssueTempTokenResponse(
    String tempToken
) {

    static IssueTempTokenResponse of(IssueTempTokenServiceResponse serviceResponse) {
        return new IssueTempTokenResponse(serviceResponse.tempToken());
    }
}
