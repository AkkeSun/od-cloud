package com.odcloud.application.port.in.query;

import lombok.Builder;

@Builder
public record GetGroupAccountListQuery(
    String groupId
) {

}
