package com.odcloud.application.file.service.register_file;

import com.odcloud.domain.model.Account;
import java.util.List;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record RegisterFileCommand(
    Long folderId,
    Account account,
    List<MultipartFile> files
) {

}
