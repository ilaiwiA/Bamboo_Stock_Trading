package com.example.stock.security.Utility;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;

@Component
public class JwtTokenUtil {

    private final int JWT_EXPIRATION = 1000 * 60 * 30;

    private final String JWT_KEY = "C728574F4F605DB08C92069BDFDBA00636AEBC805EC89E9384F4FD116C1C1538";

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        System.out.println("CLAIMS RESOLVER: " + claimsResolver.apply(claims));
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return getExpirationFromToken(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public ResponseCookie generateToken(String userName) {
        Map<String, Object> claims = new HashMap<>();

        return createToken(claims, userName);
    }

    private ResponseCookie createToken(Map<String, Object> claims, String userName) {
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(getSignKey()).compact();

        ResponseCookie cookie = ResponseCookie.from("Set-Cookie", token)
                .httpOnly(true)
                .secure(true)
                .maxAge(Duration.ofHours(1))
                .sameSite("None")
                .build();

        // Cookie cookie = new Cookie("token", token);
        // cookie.setHttpOnly(true);
        // cookie.setSecure(false);
        // cookie.setPath("/");
        // cookie.setMaxAge((int) ((System.currentTimeMillis() * 1000) +
        // JWT_EXPIRATION));
        return cookie;
    }

    private Key getSignKey() {
        byte[] keyByes = Decoders.BASE64.decode(this.JWT_KEY);
        return Keys.hmacShaKeyFor(keyByes);
    }

}
