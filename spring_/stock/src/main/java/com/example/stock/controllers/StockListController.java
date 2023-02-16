package com.example.stock.controllers;

import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.stock.models.Stocks.Stock;

@RestController
public class StockListController {

    Map<String, Stock> test;

    String API_KEY = "GEARZVA8KB2B3YEO65VPE2FBLHJYDBAI";
    String URL = "https://api.tdameritrade.com/v1/marketdata/quotes?apikey=" + API_KEY + "&symbol=";

    // @CrossOrigin
    // @PostMapping("/api/stocks")
    // public void setStocks (@RequestBody Map<String, Stock> stock){
    //     test = stock;
    // }

    @CrossOrigin
    @GetMapping(("/api/stocks/{stockList}"))
    public Map<String, Stock> getStocks (@PathVariable("stockList") String list){
        RestTemplate restTemplate = new RestTemplate();

        ParameterizedTypeReference<Map<String, Stock>> stock = new ParameterizedTypeReference<Map<String,Stock>>() {}; 
        
        RequestEntity<Void> request = RequestEntity.get(URL + list).accept(MediaType.APPLICATION_JSON).build();
        
        return restTemplate.exchange(request, stock).getBody();
    }
}
