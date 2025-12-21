package com.odcloud.application.port.out;

import com.odcloud.application.port.in.command.PushFcmCommand;
import com.odcloud.domain.model.AccountDevice;
import java.util.List;

public interface FcmPort {

    List<AccountDevice> push(PushFcmCommand command);
}
