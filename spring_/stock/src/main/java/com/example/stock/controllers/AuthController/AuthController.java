package com.example.stock.controllers.AuthController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.stock.ExceptionHandlers.ErrorResponseRegister;
import com.example.stock.models.User.AuthRequest;
import com.example.stock.models.User.User;
import com.example.stock.models.User.Portfolio.Portfolio;
import com.example.stock.models.User.Security.UserRegister;
import com.example.stock.security.Utility.JwtTokenUtil;
import com.example.stock.services.UserServices;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenUtil jwtTokenUtil;

    private final UserServices userServices;

    @PostMapping("/login")
    public String generateToken(@RequestBody AuthRequest authRequest, HttpServletResponse response) {
        System.out.println(authRequest.toString());

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(),
                        authRequest.getPassword()));

        System.out.println(auth.toString());

        if (auth.isAuthenticated()) {
            response.setHeader(HttpHeaders.SET_COOKIE,
                    jwtTokenUtil.generateToken(authRequest.getUsername()).toString());
            return "success";
        } else {
            throw new BadCredentialsException("Invalid User");
        }
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody @Valid UserRegister userRegister, HttpServletResponse response) {

        Portfolio portfolio = Portfolio.builder().totalBalance(userRegister.getStartingBalance())
                .availableBalance(userRegister.getStartingBalance()).build();

        User user = userServices.createUser(userRegister, portfolio);

        userServices.addUser(user);

        return "registered";
    }

    @GetMapping("/logout")
    public Map<String, String> generateToken(HttpServletResponse response) {
        Map<String, String> logoutRes = new HashMap<>();
        response.setHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from("jwt", "logout")
                .httpOnly(true)
                .secure(true)
                .maxAge(0)
                .sameSite("None")
                .path("/")
                .build().toString());

        logoutRes.put("response", "success");
        return logoutRes;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseRegister> handleValidationException(MethodArgumentNotValidException exception) {
        List<String> errMsg = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorResponseRegister err = new ErrorResponseRegister("Validation Failed", errMsg);
        return new ResponseEntity<ErrorResponseRegister>(err, HttpStatus.BAD_REQUEST);
    }
}
