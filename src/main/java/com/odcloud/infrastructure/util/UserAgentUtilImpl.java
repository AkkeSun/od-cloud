package com.odcloud.infrastructure.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAgentUtilImpl implements UserAgentUtil {

    private final HttpServletRequest request;

    public String getUserAgent() {
        return request.getHeader("User-Agent");
    }
}

