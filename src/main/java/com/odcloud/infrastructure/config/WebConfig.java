package com.odcloud.infrastructure.config;

import com.odcloud.application.port.out.GroupStoragePort;
import com.odcloud.domain.model.Group;
import com.odcloud.resolver.LoginAccountResolver;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final GroupStoragePort groupStoragePort;
    private final LoginAccountResolver loginAccountResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginAccountResolver);
    }

    @PostConstruct // todo : for test
    public void init() {
        groupStoragePort.register(Group.builder()
            .id("TEST")
            .ownerEmail("akkessun@gmail.com")
            .description("테스트 그룹")
            .regDt(LocalDateTime.now())
            .build());
    }
}
