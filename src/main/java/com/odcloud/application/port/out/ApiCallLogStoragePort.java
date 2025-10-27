package com.odcloud.application.port.out;

import com.odcloud.domain.model.ApiCallLog;

public interface ApiCallLogStoragePort {

    ApiCallLog register(ApiCallLog apiCallLog);
}
