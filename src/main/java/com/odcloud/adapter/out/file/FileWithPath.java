package com.odcloud.adapter.out.file;

import com.odcloud.domain.model.FileInfo;

public record FileWithPath(
    FileInfo file,
    String path
) {

    public static FileWithPath ofRoot(FileInfo file) {
        return new FileWithPath(file, "");
    }

    public static FileWithPath of(FileInfo file, String folderName) {
        return new FileWithPath(file, folderName);
    }

    public String getFullPath() {
        if (path == null || path.isEmpty()) {
            return file.getFileName();
        }
        return path + "/" + file.getFileName();
    }
}
