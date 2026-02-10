package com.odcloud.domain.model;

import com.odcloud.application.group.port.in.command.RegisterGroupCommand;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Group {

    private Long id;
    private String name;
    private String ownerEmail;
    private Long storageUsed;
    private Long storageTotal;
    private List<GroupAccount> groupMembers;
    private LocalDateTime modDt;
    private LocalDateTime regDt;

    public Group(Long id, String ownerEmail, String name, LocalDateTime regDt) {
        this.id = id;
        this.ownerEmail = ownerEmail;
        this.name = name;
        this.regDt = regDt;
    }

    public Group(Long id, String ownerEmail, String name, Long storageUsed, Long storageTotal,
        LocalDateTime regDt) {
        this.id = id;
        this.ownerEmail = ownerEmail;
        this.name = name;
        this.storageUsed = storageUsed;
        this.storageTotal = storageTotal;
        this.regDt = regDt;
    }

    public static Group of(RegisterGroupCommand command) {
        return Group.builder()
            .name(command.name())
            .ownerEmail(command.ownerEmail())
            .storageUsed(0L)
            .storageTotal(3221225472L)
            .regDt(LocalDateTime.now())
            .build();
    }

    public static Group of(String name, String ownerEmail) {
        return Group.builder()
            .name(name)
            .ownerEmail(ownerEmail)
            .storageUsed(0L)
            .storageTotal(3221225472L)
            .regDt(LocalDateTime.now())
            .build();
    }

    public static Group of(Long id) {
        return Group.builder()
            .id(id)
            .build();
    }

    public void updateGroupMembers(List<GroupAccount> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public void increaseStorageUsed(long size) {
        this.storageUsed += size;
        this.modDt = LocalDateTime.now();
    }

    public void decreaseStorageUsed(long size) {
        this.storageUsed = Math.max(0L, this.storageUsed - size);
        this.modDt = LocalDateTime.now();
    }

    public void updateStorageTotal(long size) {
        this.storageTotal = size;
        this.modDt = LocalDateTime.now();
    }

    public void decreaseStorageTotal(long size) {
        this.storageTotal = Math.max(3221225472L, this.storageTotal - size);
        this.modDt = LocalDateTime.now();
    }

    public boolean canUpload(long fileSize) {
        return (storageUsed + fileSize) <= storageTotal;
    }

    public void updateOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
        this.modDt = LocalDateTime.now();
    }

    public boolean needsOwnerEmailUpdate(String newOwnerEmail) {
        return newOwnerEmail != null && !newOwnerEmail.equals(ownerEmail);
    }

    public boolean needsNameUpdate(String newName) {
        return newName != null && !newName.equals(name);
    }

    public void updateName(String name) {
        this.name = name;
        this.modDt = LocalDateTime.now();
    }
}
