package com.example.stock.security.Filters;

import java.io.IOException;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.stock.security.Utility.JwtTokenUtil;
import com.example.stock.services.jpaUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

/*
 * JWT TOKEN FILTER -> VALIDATES TOKEN and validates user and token match
 */
@Component
@AllArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;

    private final jpaUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();
        String jwtToken = null;

        // Parse cookie from request
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) { // retrieve jwt token
                    jwtToken = cookie.getValue();
                    break;
                }
            }
        }

        if (jwtToken != null) { // if found extract username from token
            String username = null;
            try {

                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (Exception e) { // if token is not valid -> skip to next filter
                filterChain.doFilter(request, response);
            }

            if (username != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) { // if username is parsed from
                                                                                      // token AND user is currently not
                                                                                      // authenticated
                UserDetails userDetails = userDetailsService.loadUserByUsername(username); // retrieve user from
                                                                                           // database
                if (jwtTokenUtil.validateToken(jwtToken, userDetails)) { // if token is validated that it belongs to the
                                                                         // user AND is not expired
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails,
                            null, userDetails.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(auth);
                    filterChain.doFilter(request, response); // ADD USER TO SECURITY CONTEXT AND GO TO NEXT FILTER
                } else {
                    throw new BadCredentialsException("Invalid Token"); // if token is invalid throw exception
                }
            } else {
                throw new BadCredentialsException("Invalid Token"); // if token is invalid throw exception

            }
        } else {

            filterChain.doFilter(request, response); // if token is missing go to next filter
        }

    }

}
