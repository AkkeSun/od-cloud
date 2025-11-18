package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.domain.model.FolderInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FOLDER_INFO")
class FolderInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "PARENT_ID")
    private Long parentId;

    @Column(name = "GROUP_ID")
    private String groupId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "OWNER")
    private String owner;

    @Column(name = "PATH")
    private String path;

    @Column(name = "ACCESS_LEVEL")
    private String accessLevel;

    @Column(name = "MOD_DT")
    private LocalDateTime modDt;

    @Column(name = "REG_DT")
    private LocalDateTime regDt;

    static FolderInfoEntity of(FolderInfo folder) {
        return FolderInfoEntity.builder()
            .id(folder.getId())
            .parentId(folder.getParentId())
            .groupId(folder.getGroupId())
            .name(folder.getName())
            .owner(folder.getOwner())
            .path(folder.getPath())
            .accessLevel(folder.getAccessLevel())
            .modDt(folder.getModDt())
            .regDt(folder.getRegDt())
            .build();
    }
}
