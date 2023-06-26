package com.example.stock.ExceptionHandlers.CustomAnnotations.Validators;

import com.example.stock.ExceptionHandlers.CustomAnnotations.UniqueUsername;
import com.example.stock.repository.UserRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;

/*
 * Validator -> checks if username is in repository and returns bool
 */
@AllArgsConstructor
public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {
    private final UserRepository userRepository;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        return username != null && !userRepository.existsByUserName(username);
    }
}