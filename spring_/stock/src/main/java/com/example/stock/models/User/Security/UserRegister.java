package com.example.stock.models.User.Security;

import com.example.stock.ExceptionHandlers.CustomAnnotations.UniqueUsername;
import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRegister {

    @NotEmpty(message = "First name is required")
    @JsonAlias("formFirstName")
    public String firstName;

    @NotEmpty(message = "Last name is required")
    @JsonAlias("formLastName")
    public String lastName;

    @NotEmpty(message = "Username is required")
    @UniqueUsername
    @JsonAlias("formName")
    public String userName;

    @NotNull(message = "Starting balance is required")
    @Min(value = 10000, message = "Starting balance must be greater than $10000")
    @JsonAlias("formStartBal")
    public Double startingBalance;

    @NotEmpty(message = "Password is required")
    @JsonAlias("formPass")
    private String password;

    @NotEmpty(message = "Password is required")
    @JsonAlias("formPassVerify")
    private String validatePassword;

    @AssertTrue(message = "Passwords do not match")
    public boolean isPasswordMatch() {
        return this.password.equals(this.validatePassword);
    }
}
