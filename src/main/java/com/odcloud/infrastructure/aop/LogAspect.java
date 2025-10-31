package com.odcloud.infrastructure.aop;

import static com.odcloud.infrastructure.util.JsonUtil.extractJsonField;
import static com.odcloud.infrastructure.util.JsonUtil.toJsonParams;
import static com.odcloud.infrastructure.util.TextUtil.truncateTextLimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.odcloud.domain.model.ApiCallLog;
import com.odcloud.infrastructure.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final JwtUtil jwtUtil;
    private final HttpServletRequest request;

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    private void controllerMethods() {
    }

    @Pointcut("@annotation(com.odcloud.infrastructure.aop.ExceptionHandlerLog))")
    private void controllerAdviceMethods() {
    }

    @Around("controllerMethods()")
    public Object controllerLog(ProceedingJoinPoint joinPoint) throws Throwable {
        ApiCallLog apiCallLog = ApiCallLog.builder()
            .accountInfo(jwtUtil.getAccountInfo(request))
            .uri(request.getRequestURI())
            .httpMethod(request.getMethod())
            .requestParam(toJsonParams(request))
            .requestBody(truncateTextLimit(getRequestBody(joinPoint)))
            .regDateTime(LocalDateTime.now())
            .build();

        log.info(apiCallLog.getRequestLog());

        Object result = joinPoint.proceed();

        String responseBody = result.toString();
        apiCallLog.updateResponseBody(truncateTextLimit(responseBody));
        apiCallLog.updateHttpStatus(extractJsonField(responseBody, "httpStatus"));
        apiCallLog.updateErrorCode(extractJsonField(responseBody, "data", "errorCode"));
        log.info(apiCallLog.getResponseLog());
        return result;
    }

    @Around("controllerAdviceMethods()")
    public Object controllerAdviceResponseLog(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        log.info("[{} {}] response - {}", request.getMethod(), request.getRequestURI(), result);
        return result;
    }

    private String getRequestBody(ProceedingJoinPoint joinPoint) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode requestBody = objectMapper.createObjectNode();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Annotation[][] parameterAnnotations = signature.getMethod().getParameterAnnotations();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < args.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof RequestBody && args[i] != null) {
                    requestBody = objectMapper.valueToTree(args[i]);
                }
            }
        }

        if (requestBody.has("password")) {
            requestBody.put("password", "SECRET");
        }

        return requestBody.toString();
    }

}
