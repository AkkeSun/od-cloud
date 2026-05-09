package com.odcloud.application.auth.port.out;

public record GoogleUserInfo(
    String email,
    String name,
    String picture
) {

}