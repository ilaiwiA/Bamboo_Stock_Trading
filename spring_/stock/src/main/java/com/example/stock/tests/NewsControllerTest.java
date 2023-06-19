package com.example.stock.tests;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.stock.models.News.NewsData;

@RestController
public class NewsControllerTest {

    NewsData data;

    @PostMapping("/api/news/test")
    public void postNews(@RequestBody NewsData newsData) {
        data = newsData;
    }

    @GetMapping("/api/news/test")
    public NewsData getNews() {
        return data;
    }

}
