package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.domain.model.ApiInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "api_info")
class ApiInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "domain")
    private String domain;

    @Column(name = "http_method")
    private String httpMethod;

    @Column(name = "uri_pattern")
    private String uriPattern;

    static ApiInfoEntity of(ApiInfo domain) {
        return ApiInfoEntity.builder()
            .id(domain.id())
            .domain(domain.domain())
            .httpMethod(domain.httpMethod())
            .uriPattern(domain.uriPattern())
            .build();
    }

    ApiInfo toDomain() {
        return ApiInfo.builder()
            .id(id)
            .domain(domain)
            .httpMethod(httpMethod)
            .uriPattern(uriPattern)
            .build();
    }
}
