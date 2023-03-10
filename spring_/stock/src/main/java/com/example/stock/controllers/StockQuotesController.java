package com.example.stock.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.stock.models.Stocks.StockQuotes.StockQuotes;

@RestController
public class StockQuotesController {
    String URL = "https://api.tdameritrade.com/v1/marketdata/";
    String API_KEY = "GEARZVA8KB2B3YEO65VPE2FBLHJYDBAI";

    @CrossOrigin
    @GetMapping("/api/stock/{ticker}/{periodType}")
    public StockQuotes getStockQuotes(@PathVariable("ticker") String ticker, @PathVariable("periodType") String periodType) {
        RestTemplate restTemplate = new RestTemplate();
        System.out.println(periodType);
                
        if (periodType.equals("day")){
            ResponseEntity<StockQuotes> response = restTemplate.getForEntity(getURL(ticker, periodType), StockQuotes.class);
            return response.getBody();
        }
        
        ResponseEntity<StockQuotes> response = restTemplate.getForEntity(getURL(ticker, periodType), StockQuotes.class);
        return response.getBody();
    }

    String getURL(String ticker, String periodType) {
        String period = "1";
        String frequencyType = "daily";
        String frequency = "1";
        LocalDate today = LocalDate.now();
        System.out.println(today);
        if(periodType.equals("day")) {
            return "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=" + ticker + "&interval=1min&outputsize=full&apikey=RNCVO8QHAI6LHJYT";        
        };
        
        if(periodType.equals("week")) {
            periodType = "day";
            period = "5";
            frequencyType = "minute";
            frequency = "10";
        }

        if(periodType.equals("3month")) {
            periodType = "month";
            period = "3";
        }

        if(periodType.equals("all")) {
            periodType = "year";
            period = "5";
            frequencyType = "monthly";
            frequency = "1";
        }

        System.out.println(URL + ticker + "/pricehistory?apikey=" + API_KEY + "&periodType=" + periodType + "&period=" + period + "&frequencyType=" + frequencyType + "&frequency=" + frequency + "&needExtendedHoursData=false");

        return URL + ticker + "/pricehistory?apikey=" + API_KEY + "&periodType=" + periodType + "&period=" + period + "&frequencyType=" + frequencyType + "&frequency=" + frequency + "&needExtendedHoursData=false";
    }
}
