package com.example.stock.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.stock.models.Stocks.Stock;
import com.example.stock.models.Stocks.StockQuotes.Candles;
import com.example.stock.models.Stocks.StockQuotes.StockQuotes;

@Service
public class StockServices {

    @Autowired
    TokenServices tokenServices;

    String URL = "https://api.tdameritrade.com/v1/marketdata/";
    String API_KEY = "GEARZVA8KB2B3YEO65VPE2FBLHJYDBAI";



    public Stock getStockQuote(String ticker) {
        RestTemplate restTemplate = new RestTemplate();

        ParameterizedTypeReference<Map<String, Stock>> stock = new ParameterizedTypeReference<Map<String,Stock>>() {}; 
        
        HttpEntity<String> headerEntity = new HttpEntity<String>(getHeader());   

        Stock currentStock = restTemplate.exchange(URL + ticker + "/quotes", HttpMethod.GET, headerEntity, stock).getBody().get(ticker);

        validateStock(currentStock);
        
        return currentStock;
    }

    public Collection<Stock> getStockQuotes(String list)  {
        RestTemplate restTemplate = new RestTemplate();

        ParameterizedTypeReference<Map<String, Stock>> stock = new ParameterizedTypeReference<Map<String,Stock>>() {}; 
        
        HttpEntity<String> headerEntity = new HttpEntity<String>(getHeader());

        Map<String, Stock> stockList = restTemplate.exchange(URL + "quotes?symbol=" + list, HttpMethod.GET, headerEntity, stock).getBody();

        for (Map.Entry<String, Stock> entry : stockList.entrySet()) {
            validateStock(entry.getValue());
        }

        return stockList.values();
    
    }

    public Candles getIntraDayStockWeekend(String ticker, String date) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> headerEntity = new HttpEntity<String>(getHeader());

        ResponseEntity<StockQuotes> stockQuotes = restTemplate.exchange(getURL(ticker, "day", date), HttpMethod.GET, headerEntity, StockQuotes.class);

        ArrayList<Candles> candleList = stockQuotes.getBody().candles;

        return candleList.get(candleList.size() - 1);
    }

    public ArrayList<Candles> getIntraStockQuotes(String ticker, String periodType) {
        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<String> headerEntity = new HttpEntity<String>(getHeader());

        ResponseEntity<StockQuotes> stockQuotes;
        try {
            stockQuotes = restTemplate.exchange(getURL(ticker, periodType, getDateParameter()), HttpMethod.GET, headerEntity, StockQuotes.class);
        } catch (Exception e) {
            stockQuotes = restTemplate.exchange(getURL(ticker, periodType, ""), HttpMethod.GET, headerEntity, StockQuotes.class);
        }

        ArrayList<Candles> candleList = stockQuotes.getBody().candles;

        Calendar calendar = Calendar.getInstance();
        int DAY_OF_WEEK = calendar.get(Calendar.DAY_OF_WEEK);
        int HOUR_OF_DAY = calendar.get(Calendar.HOUR_OF_DAY);

        if(DAY_OF_WEEK != 1 && DAY_OF_WEEK != 7 && HOUR_OF_DAY >= 6 && HOUR_OF_DAY <= 19) {


        Stock stock = getStockQuote(ticker);
        calendar.setTime(new Date(stock.getTradeTimeInLong()));
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        Candles candle = Candles.builder().close(stock.getLastPrice()).datetime(calendar.getTimeInMillis()).build();

        candleList.add(candle);
        }

        return candleList;
    }
    
    public ArrayList<Candles> getHistoricalStockQuotes(String ticker, String periodType, String date) {
        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<String> headerEntity = new HttpEntity<String>(getHeader());

        System.out.println(getURL(ticker, periodType , date));
        try {
            ResponseEntity<StockQuotes> stockQuotes = restTemplate.exchange(getURL(ticker, periodType , date), HttpMethod.GET, headerEntity, StockQuotes.class);
            return stockQuotes.getBody().candles;
            
        } catch (Exception e) {
            return null;
        }
    }

    public String getURL(String ticker, String periodType, String date) {
        String period = "1";
        String frequencyType = "daily";
        String frequency = "1";
    
        if(periodType.equals("day")) {
            frequencyType = "minute";
            frequency = "5";

            return URL + ticker + "/pricehistory?periodType=" + periodType + "&period=" + period + "&frequencyType=" + frequencyType + "&frequency=" + frequency + date;
        };

        if(periodType.equals("reduced")) {
            frequencyType = "minute";
            frequency = "15";
            periodType = "day";
            
            return URL + ticker + "/pricehistory?periodType=" + periodType + "&period=" + period + "&frequencyType=" + frequencyType + "&frequency=" + frequency + date;
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
            frequencyType = "weekly";
            frequency = "1";
        }

        // return URL + ticker + "/pricehistory" + "?periodType=" + periodType + "&period=" + period + "&frequencyType=" + frequencyType + "&frequency=" + frequency + "&needExtendedHoursData=false"; // historical api using OAUTH
        // return URL + ticker + "/pricehistory?apikey=" + API_KEY + "&periodType=" + periodType + "&period=" + period + "&frequencyType=" + frequencyType + "&frequency=" + frequency + "&needExtendedHoursData=false"; api using basic auth

        if(!date.equals("historical")) return URL + ticker + "/pricehistory" +  "?frequency=5" + date + "&needExtendedHoursData=true";

        return URL + ticker + "/pricehistory" + "?periodType=" + periodType + "&period=" + period + "&frequencyType=" + frequencyType + "&frequency=" + frequency + "&needExtendedHoursData=false"; // historical api using OAUTH
    }


    HttpHeaders getHeader() {
        HttpHeaders headers = new HttpHeaders();
        
        String ACCESS_TOKEN = tokenServices.getAuthorizationToken();

        headers.add("Authorization", "Bearer " + ACCESS_TOKEN);

        return headers;
    }

    String getDateParameter(){
        Calendar calendar = Calendar.getInstance();

        if(calendar.get(Calendar.DAY_OF_WEEK) == 1 || calendar.get(Calendar.DAY_OF_WEEK) == 7 || calendar.get(Calendar.HOUR_OF_DAY) < 7){
            return "";
        }

        Long startDate = getStartDate(calendar);
        Long endDate = getEndDate(calendar);

        return "&startDate=" + startDate + "&endDate=" + endDate;
    }

    Long getStartDate(Calendar calendar){
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    Long getEndDate(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    void validateStock(Stock stock){
        Calendar calendar = Calendar.getInstance();
        Calendar stockTime = Calendar.getInstance();

        stockTime.setTime(new Date(stock.getRegularMarketTradeTimeInLong()));


        if( calendar.get(Calendar.DAY_OF_WEEK) == stockTime.get(Calendar.DAY_OF_WEEK)) return;

        if (calendar.get(Calendar.DAY_OF_WEEK) >= 2 && calendar.get(Calendar.DAY_OF_WEEK) <= 6) return;

        if (calendar.get(Calendar.DAY_OF_WEEK) < 7){
            calendar.add(Calendar.DATE, -7);
        }


        // SET THE LAST PRICE TO THURSDAY
        calendar.set(Calendar.DAY_OF_WEEK, 5);
        calendar.set(Calendar.HOUR_OF_DAY, 5);

        String date = "&startDate=" + calendar.getTime().getTime() + "&endDate=" + calendar.getTime().getTime();

        Candles candleThursday = getIntraDayStockWeekend(stock.getSymbol(), date);

        stock.setClosePrice(candleThursday.getClose());
        stock.setNetChange(stock.getLastPrice() - stock.getClosePrice());
        stock.setNetPercentChangeInDouble((stock.getNetChange() / stock.getLastPrice()) * 100);
    }
    
}
