package com.odcloud.infrastructure.config;

import com.odcloud.infrastructure.constant.ProfileConstant;
import com.odcloud.infrastructure.resolver.LoginAccountResolver;
import java.nio.file.Paths;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final ProfileConstant constant;

    private final LoginAccountResolver loginAccountResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginAccountResolver);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (constant.profile().equals("prod")) {
            return;
        }

        registry.addResourceHandler("/**")
            .addResourceLocations(Paths.get(constant.fileUpload().basePath()).toAbsolutePath()
                .normalize().toUri().toString());
    }
}
