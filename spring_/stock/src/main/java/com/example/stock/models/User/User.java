package com.example.stock.models.User;

import com.example.stock.models.User.Portfolio.Portfolio;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class User {
    String firstName;
    String lastName;
    String userName;

    Portfolio portfolio;
}
