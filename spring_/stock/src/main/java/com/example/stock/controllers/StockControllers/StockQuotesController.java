package com.example.stock.controllers.StockControllers;

import java.time.LocalTime;
import java.util.ArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.stock.models.Stocks.StockQuotes.IntradayStockQuotes;
import com.example.stock.models.Stocks.StockQuotes.StockQuotes;

@RestController
@RequestMapping("/api/stock")
public class StockQuotesController {
    String URL = "https://api.tdameritrade.com/v1/marketdata/";
    String API_KEY = "GEARZVA8KB2B3YEO65VPE2FBLHJYDBAI";

    @CrossOrigin
    @GetMapping("/{ticker}/quotes/{periodType}")
    public StockQuotes getStockQuotes(@PathVariable("ticker") String ticker, @PathVariable("periodType") String periodType) {
        RestTemplate restTemplate = new RestTemplate();
        
        ResponseEntity<StockQuotes> response = restTemplate.getForEntity(getURL(ticker, periodType), StockQuotes.class);
        return response.getBody();
    }

    @CrossOrigin
    @GetMapping(value ={"/{ticker}/quotes", "/{ticker}/quotes/"})
    public ArrayList<IntradayStockQuotes[]> getIntradayStockQuotes(@PathVariable("ticker") String ticker) {
        RestTemplate restTemplate = new RestTemplate();
                
        ResponseEntity<IntradayStockQuotes []> response = restTemplate.getForEntity(getURL(ticker, "day"), IntradayStockQuotes[].class);
        IntradayStockQuotes [] arr = response.getBody();
        ArrayList<IntradayStockQuotes[]> stockList = new ArrayList<IntradayStockQuotes[]>();
        stockList.add(arr);
        return stockList;
    }


    String getURL(String ticker, String periodType) {
        String period = "1";
        String frequencyType = "daily";
        String frequency = "1";
        if(periodType.equals("day")) {
            if(LocalTime.now().getHour() >= 17) return "https://api.tiingo.com/iex/"+ ticker + "/prices?resampleFreq=5min&afterHours=true&token=10a4700444c2337bec9894d387dae95a222774ee";

            return "https://api.tiingo.com/iex/"+ ticker + "/prices?resampleFreq=1min&afterHours=true&token=10a4700444c2337bec9894d387dae95a222774ee";
    
            // LocalDate today = LocalDate.now(); // dependency for stockdata.org
            // return "https://api.stockdata.org/v1/data/intraday?symbols=" + ticker + "&api_token=PbNEPMgVRe2T01tEEhG68hy9RNtnd3Uu8PbaPaqA&date_from="+ today + "&date_to="+ today;
            // return "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=" + ticker + "&interval=1min&outputsize=full&apikey=RNCVO8QHAI6LHJYT";        
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

        System.out.println(URL + ticker + "/pricehistory?apikey=" + API_KEY + "&periodType=" + periodType + "&period=" + period + "&frequencyType=" + frequencyType + "&frequency=" + frequency + "&needExtendedHoursData=false");

        return URL + ticker + "/pricehistory?apikey=" + API_KEY + "&periodType=" + periodType + "&period=" + period + "&frequencyType=" + frequencyType + "&frequency=" + frequency + "&needExtendedHoursData=false";
    }
}
