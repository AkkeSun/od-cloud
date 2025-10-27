package com.odcloud.domain.model;

import lombok.Builder;
import org.springframework.util.AntPathMatcher;

@Builder
public record ApiInfo(

    Long id,

    String domain,

    String httpMethod,

    String uriPattern
) {

    public String getPathVariable(String uri) {
        try {
            return new AntPathMatcher().extractPathWithinPattern(uriPattern, uri);
        } catch (Exception e) {
            return "";
        }
    }
}

