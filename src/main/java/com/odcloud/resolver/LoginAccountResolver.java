package com.odcloud.resolver;

import static com.odcloud.infrastructure.exception.ErrorCode.INVALID_ACCESS_TOKEN;

import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import com.odcloud.infrastructure.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class LoginAccountResolver implements HandlerMethodArgumentResolver {

    private final JwtUtil jwtUtil;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginAccount.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String token = webRequest.getHeader("Authorization");
        if (token == null) {
            return null;
        }
        try {
            return Account.of(jwtUtil.getClaims(token));
        } catch (Exception e) {
            throw new CustomAuthenticationException(INVALID_ACCESS_TOKEN);
        }
    }
}
