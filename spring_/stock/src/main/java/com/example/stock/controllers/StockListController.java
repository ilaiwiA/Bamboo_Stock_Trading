package com.example.stock.controllers;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.stock.models.StockList;

@RestController
public class StockListController {

    Map<String, StockList> test;

    @PostMapping("/stocks")
    public void stocks (@RequestBody Map<String, StockList> stock){
        test = stock;
        System.out.println(stock);
    }

    @GetMapping("api/stocks")
    public Map<String, StockList> getStocks () {
        return test;
    }
}
