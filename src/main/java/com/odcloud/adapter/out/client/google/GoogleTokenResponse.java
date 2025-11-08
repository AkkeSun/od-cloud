package com.odcloud.adapter.out.client.google;

public record GoogleTokenResponse(
    String access_token,
    String id_token,
    String refresh_token,
    String scope,
    String token_type,
    Long expires_in

) {

}
