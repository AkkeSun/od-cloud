package com.odcloud.domain.model;

import com.odcloud.infrastructure.util.DateUtil;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class File {

    private Long id;
    private Long folderId;
    private String fileName;
    private String fileLoc;
    private MultipartFile multipartFile;
    private LocalDateTime modDt;
    private LocalDateTime regDt;

    public File(Long id, Long folderId, String fileName, String fileLoc, LocalDateTime modDt,
        LocalDateTime regDt) {
        this.id = id;
        this.folderId = folderId;
        this.fileName = fileName;
        this.fileLoc = fileLoc;
        this.modDt = modDt;
        this.regDt = regDt;
    }

    public static File create(Folder folder, MultipartFile multipartFile) {
        String originalFileName = multipartFile.getOriginalFilename();
        String extension = getFileExtension(originalFileName);
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String serverFileName = uuid + "_" + date + extension;

        return File.builder()
            .folderId(folder.getId())
            .fileName(originalFileName)
            .fileLoc(folder.getPath() + "/" + serverFileName)
            .multipartFile(multipartFile)
            .regDt(LocalDateTime.now())
            .build();
    }

    private static String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public String getRegDtString() {
        return DateUtil.formatDateTime(regDt);
    }
}
