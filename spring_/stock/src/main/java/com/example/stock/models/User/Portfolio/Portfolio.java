package com.example.stock.models.User.Portfolio;

import java.util.ArrayList;

import com.example.stock.models.User.UserStocks.UserStock;
import com.fasterxml.jackson.annotation.JsonGetter;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Portfolio {

    ArrayList<UserStock> stocks;

    ArrayList<String> watchList;


    @JsonGetter("userStocks")
    public ArrayList<String> getUserStocks() {
        ArrayList<String> userStocks = new ArrayList<>();

        for (int i = 0; i < stocks.size(); i++ ){
            userStocks.add(stocks.get(i).getTicker());
        }
        return userStocks;
    }

}
