package com.example.bloggingapp.annotation.validation;

import com.example.bloggingapp.annotation.validation.validator.BetweenValidatorInteger;
import com.example.bloggingapp.annotation.validation.validator.BetweenValidatorLong;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {BetweenValidatorInteger.class, BetweenValidatorLong.class})
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Between {
    String message() default "Invalid number!";

    long min();

    long max();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
