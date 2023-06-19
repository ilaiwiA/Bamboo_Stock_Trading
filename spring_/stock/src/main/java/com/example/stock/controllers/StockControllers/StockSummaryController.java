package com.example.stock.controllers.StockControllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.stock.models.Stocks.StockSummary;

@RestController
public class StockSummaryController {

    // https://www.alphavantage.co/ api
    // String API_KEY = "RNCVO8QHAI6LHJYT";
    String API_KEY = "WSKXEFPWRBXHEXXQ";
    String URL = "https://www.alphavantage.co/query?function=OVERVIEW";

    @GetMapping("api/stock/{ticker}/summary")
    public StockSummary getStockSummary(@PathVariable("ticker") String ticker) {
        RestTemplate restTemplate = new RestTemplate();

        StockSummary response = restTemplate.getForObject(URL + "&symbol=" + ticker + "&apikey=" + API_KEY,
                StockSummary.class);
        return response;
    }
}
