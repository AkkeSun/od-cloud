package com.odcloud.infrastructure.resolver;

import static com.odcloud.infrastructure.exception.ErrorCode.INVALID_ACCESS_TOKEN;

import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.Voucher;
import com.odcloud.domain.model.VoucherType;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import com.odcloud.infrastructure.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String token = extractTokenFromCookie(request);
        try {
            return fromClaims(jwtUtil.getClaims(token));
        } catch (Exception e) {
            throw new CustomAuthenticationException(INVALID_ACCESS_TOKEN);
        }
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        return Arrays.stream(cookies)
            .filter(c -> "accessToken".equals(c.getName()))
            .map(Cookie::getValue)
            .findFirst()
            .orElse(null);
    }

    private Account fromClaims(Claims claims) {
        List<Map<String, Object>> groupsInfo = (List<Map<String, Object>>) claims.get("groups");
        List<String> vouchersInfo = (List<String>) claims.get("vouchers");
        return Account.builder()
            .email(claims.getSubject())
            .id(((Number) claims.get("id")).longValue())
            .nickname(claims.get("nickname").toString())
            .picture(claims.get("picture").toString())
            .groups(groupsInfo.stream()
                .map(groupInfo -> Group.builder()
                    .id(((Number) groupInfo.get("id")).longValue())
                    .name(groupInfo.get("name").toString())
                    .build())
                .collect(Collectors.toList()))
            .vouchers(vouchersInfo != null ? vouchersInfo.stream()
                .map(type -> Voucher.builder().voucherType(VoucherType.valueOf(type)).build())
                .collect(Collectors.toList()) : List.of())
            .build();
    }
}
