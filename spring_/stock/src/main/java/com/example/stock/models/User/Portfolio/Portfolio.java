package com.example.stock.models.User.Portfolio;

import java.util.ArrayList;

import com.example.stock.models.User.UserStocks.UserStocks;
import com.fasterxml.jackson.annotation.JsonGetter;

import lombok.Data;

@Data
public class Portfolio {

    ArrayList<UserStocks> stocks;

    ArrayList<String> watchList;


    @JsonGetter("userStock")
    public ArrayList<String> getUserStocks() {
        ArrayList<String> userStocks = new ArrayList<>();

        for (int i = 0; i < stocks.size(); i++ ){
            userStocks.add(stocks.get(i).getTicker());
        }
        return userStocks;
    }

}
