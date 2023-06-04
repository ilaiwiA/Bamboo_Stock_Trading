package com.example.stock.controllers.StockControllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.stock.models.Stocks.Stock;
import com.example.stock.services.StockServices;

@RestController
public class StockListController {

    @Autowired
    StockServices stockServices;

    
    @CrossOrigin
    @GetMapping(("/api/stocks/{stockList}"))
    public Collection<Stock> getStocks (@PathVariable("stockList") String list){
        return stockServices.getStockQuotes(list);
    }
}
