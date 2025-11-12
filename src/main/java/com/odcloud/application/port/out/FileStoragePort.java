package com.odcloud.application.port.out;

import com.odcloud.domain.model.File;

public interface FileStoragePort {

    void save(File file);
}
