package com.example.stock.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.stock.models.ApiToken.AuthorizationToken;
import com.example.stock.repository.TokenRepository;

import lombok.NoArgsConstructor;

/*
 * SERVICE Controller to ensure TDAmeritrade token is constantly NOT expired
 */
@NoArgsConstructor
@Service
public class TokenServices {

    // repository to get current token
    @Autowired
    TokenRepository tokenRepository;

    @Value("refresh_token")
    private String token;

    // expiration time length provided by tdameritrade
    Long expirationInterval = (long) 120000;

    // called by stock quotes controller -> get current authorizationToken to
    // recieve live quotes
    public String getAuthorizationToken() {
        AuthorizationToken authorizationToken = tokenRepository.getAuthorizationToken();

        // if token is not valid -> get new one and save it place of old
        if (authorizationToken == null || authorizationToken.getAccess_token() == null
                || authorizationToken.getExpirationTime() <= new Date().getTime() + expirationInterval) {
            System.out.println("TOKEN MISSING OR EXPIRED");
            AuthorizationToken newAuthToken = generateAuthorizationToken();
            tokenRepository.save(newAuthToken);
            return newAuthToken.getAccess_token();
        }

        return authorizationToken.getAccess_token();
    }

    // if expired used GRANT TOKEN to get new one
    private AuthorizationToken generateAuthorizationToken() {
        String URL = "https://api.tdameritrade.com/v1/oauth2/token";
        String data = "grant_type=refresh_token&refresh_token=" + token
                + "&access_type=&code=&client_id=GEARZVA8KB2B3YEO65VPE2FBLHJYDBAI&redirect_uri=http%3A%2F%2F127.0.0.1";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.add("Content-Type", "application/x-www-form-urlencoded");

        HttpEntity<String> requestEntity = new HttpEntity<>(data, headers);

        ResponseEntity<AuthorizationToken> response = restTemplate.postForEntity(URL, requestEntity,
                AuthorizationToken.class);
        Date date = new Date();
        Long time = date.getTime();

        // Get date from new token -> create new auth token object and set expiration
        // date to current date + expiration length in seconds

        AuthorizationToken authorizationToken = response.getBody();

        authorizationToken.setExpirationTime((authorizationToken.getExpires_in() * 1000) + time);

        System.out.println("NEW AUTHORIZATION TOKEN OPTAINED");

        return authorizationToken;
    }

}
