package com.odcloud.domain.model;

import com.odcloud.application.port.in.command.RegisterGroupCommand;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record Group(
    String id,
    String ownerEmail,
    String description,
    LocalDateTime regDt
) {

    public static Group of(RegisterGroupCommand command) {
        return Group.builder()
            .id(command.id())
            .ownerEmail(command.ownerEmail())
            .description(command.description())
            .regDt(LocalDateTime.now())
            .build();
    }

    public static Group of(String id) {
        return Group.builder()
            .id(id)
            .build();
    }
}
