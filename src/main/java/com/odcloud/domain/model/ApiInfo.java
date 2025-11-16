package com.odcloud.domain.model;

import lombok.Builder;

@Builder
public record ApiInfo(

    Long id,

    String domain,

    String httpMethod,

    String uriPattern
) {

}

