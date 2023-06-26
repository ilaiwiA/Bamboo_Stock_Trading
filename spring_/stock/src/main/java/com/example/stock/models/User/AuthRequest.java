package com.example.stock.models.User;

import lombok.AllArgsConstructor;
import lombok.Data;

/*Authentication model
Stores user login credentials
 */
@Data
@AllArgsConstructor
public class AuthRequest {

    private String username;
    private String password;
}
