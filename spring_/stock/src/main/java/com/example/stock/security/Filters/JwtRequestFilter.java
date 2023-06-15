package com.example.stock.security.Filters;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
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

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    // private final JwtTokenUtil jwtTokenUtil;
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    jpaUserDetailsService userDetailsService;

    // private final jpaUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();
        String requestToken = null;

        if (cookies == null || cookies.length < 1) {
            filterChain.doFilter(request, response);
        }

        for (Cookie cookie : cookies) {
            if ("Set-Cookie".equals(cookie.getName())) {
                requestToken = cookie.getValue();
            }
        }

        if (requestToken != null)

        {
            String jwtToken = requestToken;
            String username = jwtTokenUtil.getUsernameFromToken(jwtToken);

            if (username != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails,
                            null, null);
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
            filterChain.doFilter(request, response);
        }

    }

}
