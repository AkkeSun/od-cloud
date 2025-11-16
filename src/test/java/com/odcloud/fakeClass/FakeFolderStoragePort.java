package com.odcloud.fakeClass;

import com.odcloud.application.port.out.FolderStoragePort;
import com.odcloud.domain.model.Folder;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FakeFolderStoragePort implements FolderStoragePort {

    public List<Folder> database = new ArrayList<>();
    public Long id = 0L;
    public boolean shouldThrowException = false;

    @Override
    public void save(Folder folder) {
        if (shouldThrowException) {
            throw new RuntimeException("Storage failure");
        }

        Folder savedFolder = Folder.builder()
            .id(folder.getId() == null ? id++ : folder.getId())
            .groupId(folder.getGroupId())
            .name(folder.getName())
            .path(folder.getPath())
            .parentId(folder.getParentId() == null ? 0 : folder.getParentId())
            .regDt(folder.getRegDt())
            .modDt(folder.getModDt())
            .build();

        database.removeIf(f -> f.getId().equals(savedFolder.getId()));
        database.add(savedFolder);
        log.info("FakeFolderStoragePort saved folder: id={}, name={}", savedFolder.getId(),
            savedFolder.getName());
    }

    @Override
    public Folder findById(Long id) {
        return database.stream()
            .filter(folder -> folder.getId().equals(id))
            .findFirst()
            .orElseThrow(
                () -> new CustomBusinessException(ErrorCode.Business_DoesNotExists_FOLDER));
    }

    @Override
    public boolean existsSameFolderName(Long parentId, String name) {
        return database.stream()
            .anyMatch(folder ->
                Objects.equals(folder.getParentId(), parentId)
                    && Objects.equals(folder.getName(), name)
            );
    }

    public void reset() {
        database.clear();
        id = 0L;
        shouldThrowException = false;
    }
}
