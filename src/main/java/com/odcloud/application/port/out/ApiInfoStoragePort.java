package com.odcloud.application.port.out;

import com.odcloud.domain.model.ApiCallLog;
import com.odcloud.domain.model.ApiInfo;
import java.util.List;

public interface ApiInfoStoragePort {

    List<ApiInfo> findAll();

    ApiInfo findByApiCallLog(ApiCallLog apiCallLog);

    void deleteApiInfoCache();

}
