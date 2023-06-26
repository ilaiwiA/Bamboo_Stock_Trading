package com.example.stock.services;

import java.util.Date;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.stock.models.User.User;
import com.example.stock.models.User.Portfolio.Portfolio;
import com.example.stock.models.User.Security.UserRegister;
import com.example.stock.repository.UserRepository;

import lombok.AllArgsConstructor;

/*
 * Service for authenticatin
 * 
 */
@Service
@AllArgsConstructor
public class UserServices {

    private final UserRepository userRepository;

    // called by registration controller, saves new user to database
    public void addUser(User user) {
        userRepository.save(user);
    }

    // called by registration controller, updates available balance
    public void updateUserBalance(User user, Double newBalance) {
        Double oldBal = user.getPortfolio().getAvailableBalance();

        user.getPortfolio().setAvailableBalance(newBalance + oldBal);
        userRepository.save(user);
    }

    // Called by Registration v, creates user object
    public User createUser(UserRegister userRegister, Portfolio portfolio) {
        return User.builder().firstName(userRegister.getFirstName()).lastName(userRegister.getLastName())
                .userName(userRegister.getUserName())
                .password(new BCryptPasswordEncoder().encode(userRegister.getPassword()))
                .creationDate(new Date().getTime()).portfolio(portfolio).build();

    }

}
