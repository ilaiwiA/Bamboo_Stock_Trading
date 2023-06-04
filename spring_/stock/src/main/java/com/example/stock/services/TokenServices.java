package com.example.stock.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.stock.models.ApiToken.AuthorizationToken;
import com.example.stock.repository.TokenRepository;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Service
public class TokenServices {

    @Autowired
    TokenRepository tokenRepository;

    Long expirationInterval = (long) 120000;


    public String getAuthorizationToken() {
        AuthorizationToken authorizationToken = tokenRepository.getAuthorizationToken();

        if(authorizationToken == null || authorizationToken.getAccess_token() == null || authorizationToken.getExpirationTime() <= new Date().getTime() + expirationInterval) {
            System.out.println("TOKEN MISSING OR EXPIRED");
            AuthorizationToken newAuthToken = generateAuthorizationToken();
            tokenRepository.save(newAuthToken);
            return newAuthToken.getAccess_token();
        }

        return authorizationToken.getAccess_token();
    }

    private AuthorizationToken generateAuthorizationToken() {
        String URL = "https://api.tdameritrade.com/v1/oauth2/token";
        String data = "grant_type=refresh_token&refresh_token=Z7Yk71BZ%2BY%2B0GDl6lxCDf4mE59%2FntsTweR0qV3CNh4J2IiBAUfmxdYuPPYrCd3oZbs2VyiP4QTYqGVsTJTNzrsoNpb6ACQTSghre%2Byajfqw29Oyurrre%2FyidLX5sg4nHRlnH5WriGY55KG10BbYVedomTVzrGKqbO1q70nqFCWllUSgtlZUWNumV0YMOrMW%2B%2F0nk8Vd1UcDWpaY1erk9ghAPKLleCbkJTzqFRRBKLeSRaqLCA5wNCRJCPyr4m3zXIkG7IxDh1susmTeWbgK1RC4d3crM%2FzqAJyYXrsaN75mTICuv%2FIDRGlelOV2IjfB8rmamMF0wi1%2B2HSiGqeb7ZA%2FEGy8LWj78Z7ZTwbiOq9x63e73%2B0PdXr9lovcd7wuE7sot%2FgDYKDnDzJf1Lz0XGqTUrSQG224xs4EjGL6YbrwJ7x6UJ6fD4iJW1L8100MQuG4LYrgoVi%2FJHHvlkV77wdFOeps%2FXL84%2Bx39QNkc5r01X5xbw4pamajRKahcw9Y53NILE22QtYuBTS84ejc%2Bj2gLCiNIrVnxiKU0iDlupv8sbfS%2BjuZpMoj8BKHNMMQKR4fPKil2m1jWTmCSXsSjI%2Be8MCmWIkmqR4PMrjctODZHcJPhsffIxLsSuvQEhPmFRKobU%2FXLZa%2FoSVZNgVKRzag6za%2FdIO3QYog4dNraMRmLWpPYoc3MOoNuHTyGe6MZJdilcluIxmtMYtUJucfKQKlQehngEURoy32NrhEv0k7GWXlzb8RTI%2BKx6FYbJ6rGZBqvHmhBwE75SJoS0FSDbFU3L7qB%2Ftu0zbyC5KGVKBah%2F783l8Su0mWxBpX%2FSTP8C2%2FRk7fw7oK%2BwjJ4pSfwvrC7KO9trLD5vyTYyWENdY4x04GTPYXi1FaZ2rh67DqhA4t92a7yR%2FQ%3D212FD3x19z9sWBHDJACbC00B75E&access_type=&code=&client_id=GEARZVA8KB2B3YEO65VPE2FBLHJYDBAI&redirect_uri=http%3A%2F%2F127.0.0.1";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.add("Content-Type", "application/x-www-form-urlencoded");
        
        HttpEntity<String> requestEntity = new HttpEntity<>(data, headers); 


        ResponseEntity<AuthorizationToken> response =  restTemplate.postForEntity(URL, requestEntity, AuthorizationToken.class);
        Date date = new Date();
        Long time = date.getTime();

        AuthorizationToken authorizationToken = response.getBody();
        
        authorizationToken.setExpirationTime((authorizationToken.getExpires_in() * 1000) + time);

        System.out.println("NEW AUTHORIZATION TOKEN OPTAINED");

        return authorizationToken;
    }
    
}
