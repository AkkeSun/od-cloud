package com.odcloud.application.util.port.out;

import com.odcloud.application.util.port.out.dto.MailRequest;

public interface MailPort {

    void send(MailRequest request);
}
