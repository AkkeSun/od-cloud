package com.odcloud.application.device.port.out;

import com.odcloud.application.device.service.push_fcm.PushFcmCommand;
import com.odcloud.domain.model.AccountDevice;
import java.util.List;

public interface FcmPort {

    List<AccountDevice> push(PushFcmCommand command);
}
