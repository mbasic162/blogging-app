package com.example.bloggingapp.annotation.validation.validator;

import com.example.bloggingapp.annotation.validation.Between;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BetweenValidatorInteger implements ConstraintValidator<Between, Integer> {
    private long min;
    private long max;

    @Override
    public void initialize(Between constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Integer number, ConstraintValidatorContext context) {
        return number != null && number >= min && number <= max;
    }
}
