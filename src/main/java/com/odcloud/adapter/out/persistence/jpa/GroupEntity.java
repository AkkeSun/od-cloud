package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.domain.model.Group;
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
    private String id;

    private String description;

    private LocalDateTime regDt;

    static GroupEntity of(Group group) {
        return GroupEntity.builder()
            .id(group.id())
            .description(group.description())
            .regDt(group.regDt())
            .build();
    }


    Group toDomain() {
        return Group.builder()
            .id(id)
            .description(description)
            .regDt(regDt)
            .build();
    }
}
