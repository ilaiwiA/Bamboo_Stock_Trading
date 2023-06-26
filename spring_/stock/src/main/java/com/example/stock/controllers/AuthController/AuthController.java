package com.example.stock.controllers.AuthController;

import java.util.ArrayList;
import java.util.Date;
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
import com.example.stock.models.User.UserStocks.Quotes;
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

    /*
     * Login endpoints, retrieve username and password, authenticate that using the
     * authentication manager
     */
    @PostMapping("/login")
    public String generateToken(@RequestBody AuthRequest authRequest, HttpServletResponse response) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(),
                        authRequest.getPassword()));

        // if authenticated generate token and return to user
        if (auth.isAuthenticated()) {
            response.setHeader(HttpHeaders.SET_COOKIE,
                    jwtTokenUtil.generateToken(authRequest.getUsername()).toString());
            return "success";
        } else {
            // Throw error if invalid
            throw new BadCredentialsException("Invalid User");
        }
    }

    /*
     * Register endpoint, retrieve registration info from request and validates
     * object
     */
    @PostMapping("/register")
    public String registerUser(@RequestBody @Valid UserRegister userRegister, HttpServletResponse response) {

        // if registration is valid begin creating user

        // create portfolio quotes list
        List<Quotes> portfolioQuotes = new ArrayList<>();

        // add first portfolio quote
        Quotes quote = new Quotes(null, userRegister.getStartingBalance(), new Date().getTime(), null);
        portfolioQuotes.add(quote);

        // Create portfolio with starting balance and add new list
        Portfolio portfolio = Portfolio.builder().totalBalance(userRegister.getStartingBalance())
                .availableBalance(userRegister.getStartingBalance()).portfolioQuotes(portfolioQuotes).build();

        // call userServices and create user object
        User user = userServices.createUser(userRegister, portfolio);

        // add user to database
        userServices.addUser(user);

        return "registered";
    }

    /*
     * Logout endpoint
     * creates cookie with 0 age to override previous
     */
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

    // ExceptionHandler for validator
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseRegister> handleValidationException(MethodArgumentNotValidException exception) {
        // Get all failed validation fields, add them to response object and return as
        // response
        List<String> errMsg = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorResponseRegister err = new ErrorResponseRegister("Validation Failed", errMsg);
        return new ResponseEntity<ErrorResponseRegister>(err, HttpStatus.BAD_REQUEST);
    }
}
