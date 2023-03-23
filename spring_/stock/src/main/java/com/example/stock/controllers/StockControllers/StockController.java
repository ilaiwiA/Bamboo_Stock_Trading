package com.example.stock.controllers.StockControllers;

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
public class StockController {

    String URL = "https://api.tdameritrade.com/v1/marketdata/";
    String API_KEY = "GEARZVA8KB2B3YEO65VPE2FBLHJYDBAI";

    @GetMapping("/api/stock/{ticker}")
    @CrossOrigin
    public Map<String, Stock> getStock(@PathVariable("ticker") String ticker) {
        RestTemplate restTemplate = new RestTemplate();

        ParameterizedTypeReference<Map<String, Stock>> stock = new ParameterizedTypeReference<Map<String,Stock>>() {}; 
        
        RequestEntity<Void> request = RequestEntity.get(URL + ticker + "/quotes?apikey=" + API_KEY).accept(MediaType.APPLICATION_JSON).build();
        
        return restTemplate.exchange(request, stock).getBody();
    }

}
