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
@Table(name = "TBL_GROUP")
class GroupEntity {

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "OWNER_EMAIL")
    private String ownerEmail;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "REG_DT")
    private LocalDateTime regDt;

    static GroupEntity of(Group group) {
        return GroupEntity.builder()
            .id(group.getId())
            .ownerEmail(group.getOwnerEmail())
            .description(group.getDescription())
            .regDt(group.getRegDt())
            .build();
    }
}
