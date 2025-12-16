package com.odcloud.adapter.in.find_files;

import com.odcloud.application.service.find_files.FindFilesServiceResponse;
import java.util.List;
import lombok.Builder;

@Builder
record FindFilesResponse(
    Long parentFolderId,
    List<FolderResponse> folders,
    List<FileResponse> files
) {

    public static FindFilesResponse of(FindFilesServiceResponse serviceResponse) {
        return FindFilesResponse.builder()
            .parentFolderId(serviceResponse.parentFolderId())
            .folders(serviceResponse.folders().stream()
                .map(FolderResponse::of)
                .toList())
            .files(serviceResponse.files().stream()
                .map(FileResponse::of)
                .toList())
            .build();
    }

    @Builder
    record FolderResponse(
        Long id,
        String name,
        String groupId,
        String regDt
    ) {
        public static FolderResponse of(FindFilesServiceResponse.FolderResponseItem item) {
            return FolderResponse.builder()
                .id(item.id())
                .name(item.name())
                .groupId(item.groupId())
                .regDt(item.regDt())
                .build();
        }
    }

    @Builder
    record FileResponse(
        Long id,
        String name,
        String fileLoc,
        String regDt
    ) {
        public static FileResponse of(FindFilesServiceResponse.FileResponseItem item) {
            return FileResponse.builder()
                .id(item.id())
                .name(item.name())
                .fileLoc(item.fileLoc())
                .regDt(item.regDt())
                .build();
        }
    }

}
