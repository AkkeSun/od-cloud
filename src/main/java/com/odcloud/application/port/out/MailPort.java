package com.odcloud.application.port.out;

import com.odcloud.adapter.out.mail.MailRequest;

public interface MailPort {

    void send(MailRequest request);
}
