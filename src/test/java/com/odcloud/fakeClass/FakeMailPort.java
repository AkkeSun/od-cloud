package com.odcloud.fakeClass;

import com.odcloud.adapter.out.mail.MailRequest;
import com.odcloud.application.port.out.MailPort;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FakeMailPort implements MailPort {

    public List<MailRequest> sentMails = new ArrayList<>();
    public boolean shouldThrowException = false;

    @Override
    public void send(MailRequest request) {
        if (shouldThrowException) {
            throw new RuntimeException("Mail sending failure");
        }
        sentMails.add(request);
        log.info("FakeMailPort send: to={}", request.toList());
    }

    public void reset() {
        sentMails.clear();
        shouldThrowException = false;
    }
}
