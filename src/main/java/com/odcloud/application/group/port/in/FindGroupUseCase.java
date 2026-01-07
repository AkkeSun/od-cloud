package com.odcloud.application.group.port.in;

import com.odcloud.application.group.service.find_group.FindGroupServiceResponse;

public interface FindGroupUseCase {

    FindGroupServiceResponse findById(Long groupId);
}
