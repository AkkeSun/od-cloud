package com.odcloud.infrastructure.validation;

import com.odcloud.infrastructure.validation.validator.DatePatternValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Constraint(validatedBy = DatePatternValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface DatePattern {

    String message() default "유효하지 않은 날짜 형식입니다";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String pattern() default "yyyy-MM-dd";

}
