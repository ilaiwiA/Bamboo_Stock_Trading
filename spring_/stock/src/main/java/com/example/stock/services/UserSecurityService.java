package com.example.stock.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.stock.models.User.Security.UserSecurity;

// Service for retrieving current logged in user
@Service
public class UserSecurityService {

    // finds user in security context throws exception if not found
    public UserSecurity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof UserSecurity))
            throw new IllegalStateException("Unexpected Principal");

        return (UserSecurity) authentication.getPrincipal();
    }

    // retrieve authenticated user id in order to user repository to find details
    public int getCurrentUserID() {
        return getCurrentUser().getUserID();
    }

}
