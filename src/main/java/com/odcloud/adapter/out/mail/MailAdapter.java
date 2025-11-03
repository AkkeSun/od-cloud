package com.odcloud.adapter.out.mail;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_SEND_EMAIL_ERROR;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED;

import com.odcloud.application.port.out.MailPort;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class MailAdapter implements MailPort {

    private final JavaMailSender mailSender;

    @Async
    @Override
    public void send(MailRequest request) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MULTIPART_MODE_MIXED_RELATED,
                UTF_8.name());

            helper.setSubject(request.subject());
            helper.setText(request.contents(), true);
            helper.setFrom(request.from());

            for (Attachment attachment : request.fileList()) {
                helper.addAttachment(attachment.fileName(), attachment.resource());
            }

            for (String to : request.toList()) {
                helper.setTo(request.from());
                mailSender.send(message);
                log.info("[send email] {}, {}", to, request.subject());
            }
        } catch (Exception e) {
            log.error("[send email] - {}", e.getMessage());
            throw new CustomBusinessException(Business_SEND_EMAIL_ERROR);
        }
    }
}
