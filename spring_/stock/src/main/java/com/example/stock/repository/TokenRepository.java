package com.example.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.stock.models.ApiToken.AuthorizationToken;

/*
 * Stores token for TDAmeritrade
 * Single token allowed at a time so it will always get id = 1
 */
public interface TokenRepository extends JpaRepository<AuthorizationToken, Long> {

    @Query("SELECT x from AuthorizationToken x where x.id = 1")
    AuthorizationToken getAuthorizationToken();
}
