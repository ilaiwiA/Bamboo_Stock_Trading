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

// JWT TOKEN SERVICE
// RESPONSIBLE FOR GENERATING AND VALIDATING TOKEB
@Component
public class JwtTokenUtil {

    private final int JWT_EXPIRATION = 1000 * 60 * 60;

    private final String JWT_KEY = "C728574F4F605DB08C92069BDFDBA00636AEBC805EC89E9384F4FD116C1C1538";

    public String getUsernameFromToken(String token) { // extract subject from token
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationFromToken(String token) { // extract expiration date from token
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) { // retrieve claim by decrypting using JWT_KEY
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

    public Boolean validateToken(String token, UserDetails userDetails) { // Validates that token username is same as
                                                                          // authetication username and NOT expired
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public ResponseCookie generateToken(String userName) { // generate Token on successful login
        Map<String, Object> claims = new HashMap<>();

        return createToken(claims, userName);
    }

    private ResponseCookie createToken(Map<String, Object> claims, String userName) { // create token and add it to
                                                                                      // cookie with 3 hr expiration
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(getSignKey()).compact();

        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(true)
                .maxAge(Duration.ofHours(3))
                .path("/")
                .sameSite("None")
                .build();

        return cookie;
    }

    private Key getSignKey() {
        byte[] keyByes = Decoders.BASE64.decode(this.JWT_KEY);
        return Keys.hmacShaKeyFor(keyByes);
    }

}
