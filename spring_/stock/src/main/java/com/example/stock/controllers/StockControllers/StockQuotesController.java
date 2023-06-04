package com.example.stock.controllers.StockControllers;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.stock.models.Stocks.StockQuotes.Candles;
import com.example.stock.services.StockServices;

@RestController
@RequestMapping("/api/stock")
public class StockQuotesController {

    @Autowired
    StockServices stockServices;

    @CrossOrigin
    @GetMapping("/{ticker}/quotes/{periodType}")
    public ArrayList<Candles> getHistoricalQuotes(@PathVariable("ticker") String ticker, @PathVariable("periodType") String periodType) {

        return stockServices.getHistoricalStockQuotes(ticker, periodType, "historical");
    }

    @CrossOrigin
    @GetMapping(value ={"/{ticker}/quotes", "/{ticker}/quotes/", "/{ticker}/quotes/reduced"})
    public ArrayList<Candles> getIntradayStockQuotes(@PathVariable("ticker") String ticker) {
        UriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentRequest();
        String periodType = builder.buildAndExpand().getPath().contains("reduced") ? "reduced" : "day";

        return stockServices.getIntraStockQuotes(ticker, periodType);
    }
}
