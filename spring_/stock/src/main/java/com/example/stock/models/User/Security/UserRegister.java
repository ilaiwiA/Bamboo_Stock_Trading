package com.example.stock.models.User.Security;

import com.example.stock.ExceptionHandlers.CustomAnnotations.UniqueUsername;
import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/*
 * User registration model  with validation
 * Returns message  and error if validation fails
 */
@Data
public class UserRegister {

    // Ensures that the post request for the field is not null OR empty
    @NotEmpty(message = "First name is required")
    @JsonAlias("formFirstName")
    public String firstName;

    @NotEmpty(message = "Last name is required")
    @JsonAlias("formLastName")
    public String lastName;

    @NotEmpty(message = "Username is required")
    // Custom annotation ensures that the username is unique
    @UniqueUsername
    @JsonAlias("formName")
    public String userName;

    @NotNull(message = "Starting balance is required")
    // Validates that the value of the startingBalance is atleast 10000
    @Min(value = 10000, message = "Starting balance must be greater than $10000")
    @JsonAlias("formStartBal")
    public Double startingBalance;

    @NotEmpty(message = "Password is required")
    @JsonAlias("formPass")
    private String password;

    @NotEmpty(message = "Password is required")
    @JsonAlias("formPassVerify")
    private String validatePassword;

    // Validates that the user entered the same password twice
    @AssertTrue(message = "Passwords do not match")
    public boolean isPasswordMatch() {
        return this.password.equals(this.validatePassword);
    }
}
