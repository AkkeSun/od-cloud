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
@Table(name = "folder_info")
class FolderInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "name")
    private String name;

    @Column(name = "owner")
    private String owner;

    @Column(name = "mod_dt")
    private LocalDateTime modDt;

    @Column(name = "reg_dt")
    private LocalDateTime regDt;

    static FolderInfoEntity of(FolderInfo folder) {
        return FolderInfoEntity.builder()
            .id(folder.getId())
            .parentId(folder.getParentId())
            .groupId(folder.getGroupId())
            .name(folder.getName())
            .owner(folder.getOwner())
            .modDt(folder.getModDt())
            .regDt(folder.getRegDt())
            .build();
    }
}
