package com.example.stock.models.ApiToken;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorizationToken {
    @Id
    int id = 1;

    @Column(
        name = "access_token",
        columnDefinition = "TEXT"
    )
    String access_token;

    Long expires_in;

    @Column(
        name = "expiration_time"
    )
    Long expirationTime;

    public Object authorizati() {
        return null;
    }
}
