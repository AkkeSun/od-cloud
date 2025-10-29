package com.odcloud.adapter.out.persistence.mail;

import java.util.List;
import lombok.Builder;

@Builder
public record MailRequest(
    String from,
    String subject,
    String contents,
    List<String> toList,
    List<Attachment> fileList
) {

}
