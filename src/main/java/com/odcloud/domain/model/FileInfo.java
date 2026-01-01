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
public class FileInfo {

    private Long id;
    private Long folderId;
    private String fileName;
    private String fileLoc;
    private Long fileSize;
    private MultipartFile multipartFile;
    private LocalDateTime modDt;
    private LocalDateTime regDt;

    public FileInfo(Long id, Long folderId, String fileName, String fileLoc, Long fileSize,
        LocalDateTime modDt, LocalDateTime regDt) {
        this.id = id;
        this.folderId = folderId;
        this.fileName = fileName;
        this.fileLoc = fileLoc;
        this.fileSize = fileSize;
        this.modDt = modDt;
        this.regDt = regDt;
    }

    public static FileInfo create(FolderInfo folder, MultipartFile multipartFile) {
        String originalFileName = multipartFile.getOriginalFilename();
        String extension = getFileExtension(originalFileName);
        String onlyFileName = originalFileName.replace(extension, "");
        if (onlyFileName.length() > 40) {
            onlyFileName = onlyFileName.substring(0, onlyFileName.length() - 40);
            originalFileName = onlyFileName + extension;
        }

        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String serverFileName = uuid + "_" + date + extension;

        return FileInfo.builder()
            .folderId(folder.getId())
            .fileName(originalFileName)
            .fileLoc(folder.getPath() + "/" + serverFileName)
            .fileSize(multipartFile.getSize())
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

    public static FileInfo ofProfilePicture(MultipartFile multipartFile) {
        String extension = getFileExtension(multipartFile.getOriginalFilename());
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String serverFileName = uuid + "_" + date + extension;

        return FileInfo.builder()
            .folderId(null)
            .fileName(null)
            .fileLoc("/pictures/" + serverFileName)
            .multipartFile(multipartFile)
            .regDt(LocalDateTime.now())
            .build();
    }

    public String getRegDtString() {
        return DateUtil.formatDateTime(regDt);
    }


    public void updateFileName(String newFileName) {
        this.fileName = newFileName;
        this.modDt = LocalDateTime.now();
    }

    public void updateFileLocForHosting(String webServerHost) {
        this.fileLoc = webServerHost + fileLoc;
    }

    public void updateFolder(Long newFolderId, String newFileLoc) {
        this.folderId = newFolderId;
        this.fileLoc = newFileLoc;
        this.modDt = LocalDateTime.now();
    }

    public void addFileNameNumber(int number) {
        this.fileName = fileName.split("\\.")[0] + "(" + number + ")" + fileName.substring(
            fileName.lastIndexOf(".") + 1);
    }
}
