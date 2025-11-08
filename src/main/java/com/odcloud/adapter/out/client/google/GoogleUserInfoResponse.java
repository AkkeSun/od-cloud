package com.odcloud.adapter.out.client.google;

import lombok.Builder;

@Builder
public record GoogleUserInfoResponse(
    String sub,
    String name,
    String given_name,
    String picture,
    String email,
    boolean email_verified
) {

}
