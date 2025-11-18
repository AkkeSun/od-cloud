package com.odcloud.infrastructure.validation.validator;


import com.odcloud.infrastructure.validation.DateTimePattern;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class DateTimePatternValidator implements ConstraintValidator<DateTimePattern, String> {

    private String pattern;

    @Override
    public void initialize(DateTimePattern constraintAnnotation) {
        this.pattern = constraintAnnotation.pattern();
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        try {
            if (value.contains("h") || value.contains("m") || value.contains("s")) {
                LocalDateTime.parse(value, formatter);
                return true;
            }
            if (value.contains("d")) {
                LocalDate.parse(value, formatter);
                return true;
            }

            YearMonth.parse(value, formatter);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}