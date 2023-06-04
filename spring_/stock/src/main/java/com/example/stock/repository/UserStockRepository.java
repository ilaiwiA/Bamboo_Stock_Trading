package com.example.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.stock.models.User.UserStocks.UserStock;

public interface UserStockRepository extends JpaRepository<UserStock, Long> {

    @Query("SELECT x from UserStock x where x.ticker = ?1")
    UserStock findStockByPortfolioId(String id);
}
