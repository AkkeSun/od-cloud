package com.odcloud.application.port.in;

import com.odcloud.application.port.in.command.FindGroupsCommand;
import com.odcloud.application.service.find_groups.FindGroupsServiceResponse;

public interface FindGroupsUseCase {

    FindGroupsServiceResponse findAll(FindGroupsCommand command);
}
