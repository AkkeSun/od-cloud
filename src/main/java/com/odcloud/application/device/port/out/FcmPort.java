package com.odcloud.application.device.port.out;

import com.odcloud.application.device.port.in.command.PushFcmCommand;
import com.odcloud.domain.model.AccountDevice;
import java.util.List;

public interface FcmPort {

    List<AccountDevice> push(PushFcmCommand command);
}
