package com.example.stock.controllers.StockControllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.stock.models.News.NewsData;

@RestController
public class NewsController {
    String limit = "&limit=50";
    String sort = "&sort=LATEST";

    String URL = "https://www.alphavantage.co/query?function=NEWS_SENTIMENT";
    String tickersOption = "&tickers=";
    // String API_KEY = "RNCVO8QHAI6LHJYT"; //BASIC KEY
    String API_KEY = "WSKXEFPWRBXHEXXQ";

    @GetMapping("/api/news")
    public NewsData getNews() {
        RestTemplate restTemplate = new RestTemplate();

        NewsData response = restTemplate.getForObject(URL + "&apikey=" + API_KEY, NewsData.class);

        return response;
    }

    @GetMapping("/api/{ticker}/news")
    public NewsData getTickerNews(@PathVariable("ticker") String ticker) {
        RestTemplate restTemplate = new RestTemplate();

        NewsData response = restTemplate.getForObject(URL + "&apikey=" + API_KEY + sort + tickersOption + ticker,
                NewsData.class);

        return response;
    }

}
