package com.example.stock.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@RestController
public class StockSummaryController {
    String API_KEY = "RNCVO8QHAI6LHJYT";
    String URL = "https://www.alphavantage.co/query?function=OVERVIEW";
    
    
    @GetMapping("api/stock/{ticker}/summary")
    public String getStockSummary(@PathVariable("ticker") String ticker) {
        RestTemplate restTemplate = new RestTemplate();

        String response = restTemplate.getForObject(URL + "&symbol=" + ticker + "&apikey=" + API_KEY, String.class);
        return response;
    }
}
