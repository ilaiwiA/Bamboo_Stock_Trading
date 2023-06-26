package com.example.stock.ExceptionHandlers.CustomAnnotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.example.stock.ExceptionHandlers.CustomAnnotations.Validators.UniqueUsernameValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/*
 * Validator for registration ->  interface for UniqueUsernameValidator
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueUsernameValidator.class)
@Documented
public @interface UniqueUsername {
    String message() default "Username is already in use";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
