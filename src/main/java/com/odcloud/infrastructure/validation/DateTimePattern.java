package com.odcloud.infrastructure.validation;

import com.odcloud.infrastructure.validation.validator.DateTimePatternValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Constraint(validatedBy = DateTimePatternValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface DateTimePattern {

    String message() default "유효하지 않은 형식 입니다";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String pattern() default "yyyy-MM-dd HH:mm:ss";

}
