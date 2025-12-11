package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.domain.model.Group;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "`GROUP`")
class GroupEntity {

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "OWNER_EMAIL")
    private String ownerEmail;

    @Column(name = "REG_DT")
    private LocalDateTime regDt;

    static GroupEntity of(Group group) {
        return GroupEntity.builder()
            .id(group.getId())
            .ownerEmail(group.getOwnerEmail())
            .name(group.getName())
            .regDt(group.getRegDt())
            .build();
    }

    public Group toDomain() {
        return Group.builder()
            .id(id)
            .ownerEmail(ownerEmail)
            .name(name)
            .regDt(regDt)
            .build();
    }
}
