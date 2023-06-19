package com.example.stock.controllers.StockControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.stock.models.Stocks.Stock;
import com.example.stock.services.StockServices;

@RestController
public class StockController {

    @Autowired
    StockServices stockServices;

    @GetMapping("/api/stock/{ticker}")
    public Stock getStock(@PathVariable("ticker") String ticker) {
        return stockServices.getStockQuote(ticker);
    }

}
