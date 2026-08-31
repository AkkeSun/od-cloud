package com.odcloud.domain.model;

import com.odcloud.infrastructure.util.DateUtil;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.regex.Pattern;
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
    private Long groupId;
    private String fileName;
    private String fileLoc;
    private Long fileSize;
    private MultipartFile multipartFile;
    private LocalDateTime modDt;
    private LocalDateTime regDt;

    public FileInfo(Long id, Long folderId, Long groupId, String fileName, String fileLoc,
        Long fileSize,
        LocalDateTime modDt, LocalDateTime regDt) {
        this.id = id;
        this.folderId = folderId;
        this.groupId = groupId;
        this.fileName = fileName;
        this.fileLoc = fileLoc;
        this.fileSize = fileSize;
        this.modDt = modDt;
        this.regDt = regDt;
    }

    public static FileInfo create(String diskPath, FolderInfo folderInfo,
        MultipartFile multipartFile) {
        String originalFileName = multipartFile.getOriginalFilename();
        String extension = getFileExtension(originalFileName);
        String onlyFileName = removeFileExtension(originalFileName);
        if (onlyFileName.length() > 40) {
            onlyFileName = onlyFileName.substring(0, 40);
            originalFileName = onlyFileName + extension;
        }

        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        return FileInfo.builder()
            .folderId(folderInfo.getId())
            .groupId(folderInfo.getGroupId())
            .fileName(originalFileName)
            .fileLoc(diskPath + "/" + folderInfo.getGroupId() + "_" + uuid + "_" + date + extension)
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

    private static String removeFileExtension(String fileName) {
        String extension = getFileExtension(fileName);
        if (extension.isEmpty()) {
            return fileName;
        }
        return fileName.substring(0, fileName.length() - extension.length());
    }

    public static FileInfo ofProfilePicture(String diskPath, MultipartFile multipartFile) {
        String extension = getFileExtension(multipartFile.getOriginalFilename());
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return FileInfo.builder()
            .fileLoc(diskPath + "/" + uuid + "_" + date + extension)
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

    public void updateFolderId(Long newFolderId) {
        this.folderId = newFolderId;
        this.modDt = LocalDateTime.now();
    }

    public void addFileNameNumber(int number) {
        String baseName = Pattern.compile("\\(\\d+\\)$").matcher(removeFileExtension(fileName))
            .replaceFirst("");
        this.fileName = baseName + "(" + number + ")" + getFileExtension(fileName);
    }
}
