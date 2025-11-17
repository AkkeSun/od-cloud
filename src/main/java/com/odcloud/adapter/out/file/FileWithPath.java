package com.odcloud.adapter.out.file;

import com.odcloud.domain.model.File;

public record FileWithPath(
    File file,
    String path
) {

    public static FileWithPath ofRoot(File file) {
        return new FileWithPath(file, "");
    }

    public static FileWithPath of(File file, String folderName) {
        return new FileWithPath(file, folderName);
    }

    public String getFullPath() {
        if (path == null || path.isEmpty()) {
            return file.getFileName();
        }
        return path + "/" + file.getFileName();
    }
}
