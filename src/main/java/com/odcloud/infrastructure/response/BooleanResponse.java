package com.odcloud.infrastructure.response;

public record BooleanResponse(boolean result) {

    public static BooleanResponse success() {
        return new BooleanResponse(true);
    }
}
