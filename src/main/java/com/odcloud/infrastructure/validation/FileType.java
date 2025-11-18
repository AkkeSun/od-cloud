package com.odcloud.infrastructure.validation;

import com.odcloud.infrastructure.validation.validator.FileTypeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Constraint(validatedBy = FileTypeValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface FileType {

    String message() default "유효하지 파일 형식 입니다";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] allowed() default {};
}
