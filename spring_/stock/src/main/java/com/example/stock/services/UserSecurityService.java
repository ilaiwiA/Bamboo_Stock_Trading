package com.example.stock.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.stock.models.User.Security.UserSecurity;

@Service
public class UserSecurityService {

    public UserSecurity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof UserSecurity))
            throw new IllegalStateException("Unexpected Principal");

        return (UserSecurity) authentication.getPrincipal();
    }

    public int getCurrentUserID() {
        return getCurrentUser().getUserID();
    }

}
