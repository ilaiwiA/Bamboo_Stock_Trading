package com.example.stock.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.stock.models.Stocks.StockQuotes;

@RestController
public class StockQuotesController {
    String URL = "https://api.tdameritrade.com/v1/marketdata/";
    String API_KEY = "GEARZVA8KB2B3YEO65VPE2FBLHJYDBAI";

    @GetMapping("/api/stock/{ticker}/{periodType}")
    public StockQuotes getStockQuotes(@PathVariable("ticker") String ticker, @PathVariable("periodType") String periodType) {
        RestTemplate restTemplate = new RestTemplate();

        System.out.println(getURL(ticker, periodType));
                
        ResponseEntity<StockQuotes> response = restTemplate.getForEntity(getURL(ticker, periodType), StockQuotes.class);
        
        System.out.print(response.getBody());
        
        return response.getBody();

    }

    String getURL(String ticker, String periodType) {
        String frequencyType = "daily";
        if(periodType.equals("day")) frequencyType = "minute";

        return URL + ticker + "/pricehistory?apikey=" + API_KEY + "&periodType=" + periodType + "&period=1&frequencyType=" + frequencyType + "&frequency=1&needExtendedHoursData=false";
    }
}
