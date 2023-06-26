package com.example.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.stock.models.User.Portfolio.Portfolio;

/*
 * Portfolio repository -> ONLY USED IN TESTS
 */
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    Portfolio findByPortfolioID(Integer portfolioID);
}
