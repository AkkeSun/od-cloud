package com.odcloud.application.util.port.out;

import com.odcloud.domain.model.ApiCallLog;

public interface ApiCallLogStoragePort {

    ApiCallLog register(ApiCallLog apiCallLog);
}
