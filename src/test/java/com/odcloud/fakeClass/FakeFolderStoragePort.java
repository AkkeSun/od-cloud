package com.odcloud.fakeClass;

import com.odcloud.application.file.port.in.command.FindFilesCommand;
import com.odcloud.application.file.port.out.FolderInfoStoragePort;
import com.odcloud.domain.model.FolderInfo;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FakeFolderStoragePort implements FolderInfoStoragePort {

    public List<FolderInfo> database = new ArrayList<>();
    public Long id = 0L;
    public boolean shouldThrowException = false;

    @Override
    public void save(FolderInfo folder) {
        if (shouldThrowException) {
            throw new RuntimeException("Storage failure");
        }

        FolderInfo savedFolder = FolderInfo.builder()
            .id(folder.getId() == null ? id++ : folder.getId())
            .groupId(folder.getGroupId())
            .name(folder.getName())
            .owner(folder.getOwner())
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
    public FolderInfo findById(Long id) {
        return database.stream()
            .filter(folder -> folder.getId().equals(id))
            .findFirst()
            .orElseThrow(
                () -> new CustomBusinessException(ErrorCode.Business_DoesNotExists_FOLDER));
    }

    @Override
    public List<FolderInfo> findAll(FindFilesCommand command) {
        if (shouldThrowException) {
            throw new RuntimeException("Storage failure");
        }

        // keyword 검색인 경우
        if (command.keyword() != null && !command.keyword().isBlank()) {
            return database.stream()
                .filter(folder -> folder.getName().contains(command.keyword()))
                .toList();
        }

        // folderId와 groupId로 필터링
        return database.stream()
            .filter(folder -> {
                if (command.folderId() == null) {
                    return true;
                }
                return folder.getParentId() != null && folder.getParentId()
                    .equals(command.folderId());
            })
            .filter(folder -> {
                if (command.groupId() == null) {
                    return true;
                }
                return folder.getGroupId().equals(command.groupId());
            })
            .toList();
    }

    @Override
    public boolean existsSameFolderName(Long parentId, String name) {
        return database.stream()
            .anyMatch(folder ->
                Objects.equals(folder.getParentId(), parentId)
                    && Objects.equals(folder.getName(), name)
            );
    }

    @Override
    public List<FolderInfo> findByParentId(Long parentId) {
        return database.stream()
            .filter(folder -> Objects.equals(folder.getParentId(), parentId))
            .toList();
    }

    @Override
    public void delete(FolderInfo folder) {
        database.removeIf(f -> f.getId().equals(folder.getId()));
        log.info("FakeFolderStoragePort deleted folder: id={}, name={}", folder.getId(),
            folder.getName());
    }

    @Override
    public FolderInfo findRootFolderByGroupId(Long groupId) {
        return database.stream()
            .filter(folder -> folder.getGroupId().equals(groupId))
            .filter(folder -> folder.getParentId() == null || folder.getParentId() == 0)
            .findFirst()
            .orElseThrow(
                () -> new CustomBusinessException(ErrorCode.Business_DoesNotExists_FOLDER));
    }

    @Override
    public List<FolderInfo> findByGroupId(Long groupId) {
        return database.stream()
            .filter(folder -> folder.getGroupId().equals(groupId))
            .toList();
    }

    @Override
    public void deleteByGroupId(Long groupId) {
        database.removeIf(folder -> folder.getGroupId().equals(groupId));
        log.info("FakeFolderStoragePort deleted folders by groupId: {}", groupId);
    }

    @Override
    public boolean existsById(Long id) {
        return database.stream()
            .anyMatch(folder -> folder.getId().equals(id));
    }

    public void reset() {
        database.clear();
        id = 0L;
        shouldThrowException = false;
    }
}
