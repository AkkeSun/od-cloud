package com.odcloud.infrastructure.validation;

import com.odcloud.infrastructure.validation.validator.ValidVoucherGroupIdValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValidVoucherGroupIdValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidVoucherGroupId {

    String message() default "스토리지 바우처인 경우 groupId는 필수값 입니다";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
