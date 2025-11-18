package com.odcloud.application.service.find_files;

import com.odcloud.domain.model.FileInfo;
import com.odcloud.domain.model.FolderInfo;
import java.util.List;
import lombok.Builder;

@Builder
public record FindFilesServiceResponse(
    Long parentFolderId,
    List<FolderResponseItem> folders,
    List<FileResponseItem> files
) {

    public static FindFilesServiceResponse of(
        List<FileInfo> files,
        List<FolderInfo> folders,
        Long parentFolderId
    ) {
        return FindFilesServiceResponse.builder()
            .parentFolderId(parentFolderId)
            .folders(folders.stream()
                .map(FolderResponseItem::of)
                .toList())
            .files(files.stream()
                .map(FileResponseItem::of)
                .toList())
            .build();
    }

    @Builder
    public record FolderResponseItem(
        Long id,
        String name,
        String groupId,
        String accessLevel,
        String regDt
    ) {

        public static FolderResponseItem of(FolderInfo folder) {
            return FolderResponseItem.builder()
                .id(folder.getId())
                .name(folder.getName())
                .groupId(folder.getGroupId())
                .accessLevel(folder.getAccessLevel())
                .regDt(folder.getRegDtString())
                .build();
        }
    }

    @Builder
    public record FileResponseItem(
        Long id,
        String name,
        String fileLoc,
        String regDt
    ) {

        public static FileResponseItem of(FileInfo file) {
            return FileResponseItem.builder()
                .id(file.getId())
                .name(file.getFileName())
                .fileLoc(file.getFileLoc())
                .regDt(file.getRegDtString())
                .build();
        }
    }
}
