package com.example.stock.models.User.Portfolio;

import java.util.ArrayList;
import java.util.List;

import com.example.stock.models.User.UserStocks.Quotes;
import com.example.stock.models.User.UserStocks.UserStock;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * User portfolio model 
 * Stores user owned stocks, watchlist and available balance
 * 
 */
@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Portfolio {

    @Id
    // @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "portfolio_seq", sequenceName = "portfolio_seq", allocationSize = 1)

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "portfolio_seq")
    @JsonIgnore
    private Integer portfolioID;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "portfolio_id", referencedColumnName = "portfolioID")
    List<UserStock> stocks;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "portfolio_id", referencedColumnName = "portfolioID")
    List<Quotes> portfolioQuotes;

    List<String> watchList;

    // Only used for initialization
    Double totalBalance;

    Double availableBalance;

    // retrieve stocks as String list instead of objects
    @JsonGetter("stockList")
    public List<String> getUserStocks() {
        ArrayList<String> userStocks = new ArrayList<>();

        for (int i = 0; i < stocks.size(); i++) {
            userStocks.add(stocks.get(i).getTicker());
        }
        return userStocks;
    }

}
