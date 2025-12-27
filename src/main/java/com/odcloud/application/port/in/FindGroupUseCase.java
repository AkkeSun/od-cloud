package com.odcloud.application.port.in;

import com.odcloud.application.service.find_group.FindGroupServiceResponse;

public interface FindGroupUseCase {

    FindGroupServiceResponse findById(String groupId);
}
