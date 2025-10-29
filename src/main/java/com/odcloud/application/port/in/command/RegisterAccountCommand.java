package com.odcloud.application.port.in.command;

import lombok.Builder;

@Builder
public record RegisterAccountCommand(

    String username,

    String email,
    
    String role
) {

}
