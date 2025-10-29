package com.odcloud.infrastructure.validation.validator;

import com.odcloud.infrastructure.validation.Contains;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;
import org.springframework.util.StringUtils;

public class ContainsValidator implements ConstraintValidator<Contains, Object> {

    private List<String> values;

    @Override
    public void initialize(Contains constraintAnnotation) {
        this.values = Arrays.asList(constraintAnnotation.values());
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object input, ConstraintValidatorContext context) {
        if (input == null) {
            return true;
        }

        String inputString;
        if (input instanceof String) {
            inputString = (String) input;
            if (!StringUtils.hasText(inputString)) {
                return true;
            }
        } else if (input instanceof Integer) {
            inputString = String.valueOf(input);
        } else {
            return false;
        }

        return values.contains(inputString);
    }
}