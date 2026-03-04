package com.odcloud.application.group.port.in;

import com.odcloud.application.group.service.find_group.FindGroupResponse;

public interface FindGroupUseCase {

    FindGroupResponse findById(Long groupId);
}
