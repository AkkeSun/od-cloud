package com.odcloud.application.group.port.in;

import com.odcloud.application.group.service.find_groups.FindGroupsResponse;

public interface FindGroupsUseCase {

    FindGroupsResponse findAll(String keyword);
}
