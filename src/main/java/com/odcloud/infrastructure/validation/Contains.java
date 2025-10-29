package com.odcloud.infrastructure.validation;

import com.odcloud.infrastructure.validation.validator.ContainsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Constraint(validatedBy = ContainsValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Contains {

    String[] values() default {"Y", "N"};

    String message() default "입력값이 동일하지 않습니다";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
