package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.domain.model.Group;
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
@Table(name = "\"group\"")
class GroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "owner_email")
    private String ownerEmail;

    @Column(name = "storage_used")
    private Long storageUsed;

    @Column(name = "storage_total")
    private Long storageTotal;

    @Column(name = "mod_dt")
    private LocalDateTime modDt;

    @Column(name = "reg_dt")
    private LocalDateTime regDt;

    static GroupEntity of(Group group) {
        return GroupEntity.builder()
            .id(group.getId())
            .ownerEmail(group.getOwnerEmail())
            .name(group.getName())
            .storageUsed(group.getStorageUsed())
            .storageTotal(group.getStorageTotal())
            .modDt(group.getModDt())
            .regDt(group.getRegDt())
            .build();
    }

    public Group toDomain() {
        return Group.builder()
            .id(id)
            .ownerEmail(ownerEmail)
            .name(name)
            .storageUsed(storageUsed)
            .storageTotal(storageTotal)
            .modDt(modDt)
            .regDt(regDt)
            .build();
    }
}
