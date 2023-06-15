package com.example.stock.controllers.AuthController;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.stock.models.User.AuthRequest;
import com.example.stock.security.Utility.JwtTokenUtil;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class LoginController {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenUtil jwtTokenUtil;

    @CrossOrigin
    @PostMapping("/login")
    public String generateToken(@RequestBody AuthRequest authRequest, HttpServletResponse response) {
        System.out.println(authRequest.toString());
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(),
                        authRequest.getPassword()));

        if (auth.isAuthenticated()) {
            response.setHeader(HttpHeaders.SET_COOKIE,
                    jwtTokenUtil.generateToken(authRequest.getUsername()).toString());
            return "success";
        } else {
            throw new BadCredentialsException("Invalid User");
        }
    }
}
