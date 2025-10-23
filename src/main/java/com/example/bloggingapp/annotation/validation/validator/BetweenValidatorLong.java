package com.example.bloggingapp.annotation.validation.validator;

import com.example.bloggingapp.annotation.validation.Between;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BetweenValidatorLong implements ConstraintValidator<Between, Long> {
    private long min;
    private long max;

    @Override
    public void initialize(Between constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Long number, ConstraintValidatorContext context) {
        return number != null && number >= min && number <= max;
    }
}
