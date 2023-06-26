package com.example.stock.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.stock.security.AuthenticationSecurity.CustomAuthenticationEntryPoint;
import com.example.stock.security.Filters.JwtRequestFilter;

import lombok.AllArgsConstructor;

/*
 * Enable spring security and config filter chain
 */
@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class WebSecurityConfig {

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    private final JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/auth/**").permitAll() // Allow all requests to authentication endpoints ->
                                                                 // registration, login, logout
                        .requestMatchers(HttpMethod.OPTIONS).permitAll() // allow options method to permit preflight
                                                                         // requests
                        .anyRequest().authenticated()) // secure all other endpoints
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class) // JWT FILTER
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // prevent
                                                                                                                    // spring
                                                                                                                    // security
                                                                                                                    // from
                                                                                                                    // saving
                                                                                                                    // context
                .exceptionHandling(handling -> handling.authenticationEntryPoint(customAuthenticationEntryPoint)); // exception
                                                                                                                   // handling
                                                                                                                   // using
                                                                                                                   // our
                                                                                                                   // custom
                                                                                                                   // entry
                                                                                                                   // point

        return http.build();
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
