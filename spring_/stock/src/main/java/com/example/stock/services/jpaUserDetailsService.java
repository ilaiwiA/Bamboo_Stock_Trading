package com.example.stock.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.stock.models.User.User;
import com.example.stock.models.User.Security.UserSecurity;
import com.example.stock.repository.UserRepository;

import lombok.AllArgsConstructor;

/*
 * UserDetailsSerice custom implementation
 * Used by Spring Security
 */
@Service
@AllArgsConstructor
public class jpaUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {

        try {
            User user = userRepository.findUserByUsername(username);
            return new UserSecurity(user); // maps user to custom UserSecurity object
        } catch (Exception e) {
            throw new UsernameNotFoundException("Username not found " + username); // exception if not found
        }

    }

}
