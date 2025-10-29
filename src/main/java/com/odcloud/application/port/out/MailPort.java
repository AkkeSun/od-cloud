package com.odcloud.application.port.out;

import com.odcloud.adapter.out.persistence.mail.MailRequest;

public interface MailPort {

    void send(MailRequest request);
}
