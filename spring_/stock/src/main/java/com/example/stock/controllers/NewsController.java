package com.example.stock.controllers;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.stock.models.News.NewsData;

@RestController
public class NewsController {

    NewsData data;

    @CrossOrigin
    @PostMapping("/api/news")
    public void postNews(@RequestBody NewsData newsData ) {
        data = newsData;    
    }
    @CrossOrigin
    @GetMapping("/api/news")
    public NewsData getNews() {
        return data;
    }


}
