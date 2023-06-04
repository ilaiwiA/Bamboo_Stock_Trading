package com.example.stock.services;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.stock.models.Stocks.Stock;
import com.example.stock.models.Stocks.StockQuotes.Candles;
import com.example.stock.models.User.User;
import com.example.stock.models.User.Portfolio.Portfolio;
import com.example.stock.models.User.UserStocks.Quotes;
import com.example.stock.models.User.UserStocks.UserStock;
import com.example.stock.repository.UserRepository;
import com.example.stock.services.ListServices.DateComparator;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class UserStockServices {

    final long UNIX_MS_PER_5MIN = 300000L;
    final long UNIX_MS_PER_DAY = 86400000L;


    int userID = 4;

    @Autowired
    UserRepository userRepository;

    @Autowired
    StockServices stockServices;

    public List<Quotes> generatePortfolio(String periodType){
        User user = userRepository.getUserByID(userID);

        Portfolio portfolio = user.getPortfolio();
        
        List<Quotes> portfolioQuotes = portfolio.getPortfolioQuotes();
        List<UserStock> stocks = portfolio.getStocks();

        if(portfolioQuotes.size() == 0)  createPortfolio(user, portfolioQuotes);
        else  updatePortfolio(user, portfolioQuotes, stocks);  

        return getPortfolioQuotesByDate(portfolioQuotes, periodType);
    }

    List<Quotes> createPortfolio(User user, List<Quotes> portfolioQuotes){
        Quotes quotes = Quotes.builder().datetime(user.getCreationDate()).close(user.getPortfolio().getTotalBalance()).build();
        portfolioQuotes.add(quotes);

        user.getPortfolio().setPortfolioQuotes(portfolioQuotes);

        userRepository.saveAndFlush(user);
        return portfolioQuotes;
    }

    List<Quotes> updatePortfolio(User user, List<Quotes> portfolioQuotes, List<UserStock> stocks) {
        Quotes lastQuote = portfolioQuotes.get(portfolioQuotes.size() - 1);
        Long lastDate = lastQuote.getDatetime();

        Collections.sort(stocks, new DateComparator());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(stocks.get(0).getDate()));

        long diff;
        if(lastDate < stocks.get(0).getDate()){
            long msDiff = Math.abs(stocks.get(0).getDate() - lastDate);
            diff = TimeUnit.DAYS.convert(msDiff, TimeUnit.MILLISECONDS);
        } else diff = 0;

        Long newDate = lastDate + UNIX_MS_PER_DAY;
        for (int i = 1; i < diff; i++) {
            Quotes quotes = Quotes.builder().datetime(newDate).close(lastQuote.getClose()).build();
            portfolioQuotes.add(quotes);
            newDate += UNIX_MS_PER_DAY;
        }
        
        for (int i = 0; i < stocks.size(); i++) {
            Long recentDate = stocks.get(i).getDate();
            if(lastDate > stocks.get(i).getDate()){
                recentDate = lastDate;
            }
            ArrayList<Candles> stockList = stockServices.getHistoricalStockQuotes(stocks.get(i).getTicker(), "", getDateParameter(recentDate));
            if (stockList == null) continue;

            int index = portfolioQuotes.indexOf(getStockDate(portfolioQuotes, getStockDateCandles(stockList, recentDate).getDatetime()));
            Double prevValue = 0D;


            if(index == -1){
                Double total = 0D;

                for (int x = 0; x < stockList.size(); x++) {

                    if (stockList.get(x).getDatetime() < stocks.get(i).getDate()) {
                        Quotes quotes = Quotes.builder().datetime(stockList.get(x).getDatetime()).close(lastQuote.getClose()).build();
                        portfolioQuotes.add(quotes);
                        continue;
                    };

                    if(stockList.get(x).getDatetime() < lastDate) continue; // double check this

                    Double newQuote = (stockList.get(x).getClose() - stocks.get(i).getAvgPrice()) * stocks.get(i).getQuantity();
                    Double newValue = newQuote - prevValue;
                    Double oldBalance = portfolioQuotes.get(portfolioQuotes.size() - 1).getClose();

                    total += newValue;   
            
                    prevValue = newQuote;
                    
                    Quotes quotes = Quotes.builder().datetime(stockList.get(x).getDatetime()).close(newValue + oldBalance).build();
                    portfolioQuotes.add(quotes);
                }
            } else {
                int indexPointer = index;
                Double total = 0D;
                Double oldBalance = portfolioQuotes.get(indexPointer).getClose();
                
                for (int x = 0; x < stockList.size(); x++) {


                    if(stockList.get(x).getDatetime().compareTo(portfolioQuotes.get(indexPointer).getDatetime()) < 0) {    
                        continue;
                    }

                    if(stockList.get(x).getDatetime().compareTo(portfolioQuotes.get(indexPointer).getDatetime()) > 0 ){
                        portfolioQuotes.get(indexPointer).setClose(portfolioQuotes.get(indexPointer - 1).getClose());    

                        x--;

                    }

                    if(stockList.get(x).getDatetime().compareTo(portfolioQuotes.get(indexPointer).getDatetime()) == 0 ){
                        Double newQuote = (stockList.get(x).getClose() - stocks.get(i).getAvgPrice()) * stocks.get(i).getQuantity();
                        Double newValue = newQuote - prevValue;
                        oldBalance = portfolioQuotes.get(indexPointer).getClose();

                        prevValue = newQuote;
                        total += newValue;

                        portfolioQuotes.get(indexPointer).setClose(oldBalance + total);    
 
                    }
                    indexPointer++;
                }
            }
        }

        userRepository.saveAndFlush(user);

        return portfolioQuotes;
    }

    List<Quotes> getPortfolioQuotesByDate(List<Quotes> portfolioQuotes, String periodType){
        Calendar calendar = Calendar.getInstance();
        // calendar.setTime(new Date(getCurrentTime()));
        calendar.setTime(new Date(portfolioQuotes.get(portfolioQuotes.size() - 1).getDatetime()));
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND, 0);    
        
        if(periodType.equals("day")) {
            int index = portfolioQuotes.indexOf(getStockDate(portfolioQuotes, calendar.getTimeInMillis()));

            calendar.set(Calendar.HOUR_OF_DAY, 18);
            calendar.set(Calendar.MINUTE, 55);
            calendar.set(Calendar.SECOND,0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.add(Calendar.DATE, -1);

            Double prevQuote = portfolioQuotes.get(portfolioQuotes.indexOf(getStockDate(portfolioQuotes, calendar.getTimeInMillis()))).getClose();
            portfolioQuotes.get(index).setPrevious(prevQuote);
            return portfolioQuotes.subList(index, portfolioQuotes.size() - 1);
        }

        if(periodType.equals("week")) {
            calendar.add(Calendar.DATE, -7);
        }

        if(periodType.equals("month")) {
            calendar.add(Calendar.MONTH, -1);
        }

        if(periodType.equals("3month")) {
            calendar.add(Calendar.MONTH, -3);

        }

        if(periodType.equals("ytd")) {
            calendar.set(Calendar.MONTH, 0);
            calendar.set(Calendar.DAY_OF_MONTH, 1);

        }

        if(periodType.equals("all")) {
            calendar.setTime(new Date(portfolioQuotes.get(0).getDatetime()));
        }

        return getStockDay(portfolioQuotes, periodType, calendar.getTimeInMillis());
    }

    public ResponseEntity<String> purchaseStock(ObjectNode json){
        String orderBuyIn = json.get("orderBuyIn").asText();
        Double orderValue = json.get("orderValue").asDouble();
        String symbol = json.get("symbol").asText();


        // load user
        User user = userRepository.getUserByID(userID);

        Portfolio portfolio = user.getPortfolio();
        
        UserStock stock =  getStock(portfolio.getStocks(), symbol);

        if(stock.getTicker() == null) {
            stock.setTicker(symbol);
            stock.setAvgPrice(0.00);
            stock.setQuantity(0.00);
            portfolio.getStocks().add(stock);
        }

        Stock currentStockQuote = stockServices.getStockQuote(symbol);

        Double newQuantity = Double.parseDouble(new DecimalFormat("0.00").format(stock.getQuantity() + getQuantity(orderBuyIn, orderValue, currentStockQuote.getLastPrice())));

        stock.setAvgPrice(calculateAveragePrice(stock.getAvgPrice(), stock.getQuantity(), currentStockQuote.getLastPrice(), getQuantity(orderBuyIn, orderValue, currentStockQuote.getLastPrice())));
        stock.setQuantity(newQuantity);
        stock.setDate(getCurrentTime());

        Double availableBal = portfolio.getAvailableBalance() - (getQuantity(orderBuyIn, orderValue, currentStockQuote.getLastPrice()) * currentStockQuote.getLastPrice());

        Double newAvailableBal = Double.parseDouble(new DecimalFormat("0.00").format(availableBal));


        portfolio.setAvailableBalance(newAvailableBal);
        
        userRepository.saveAndFlush(user);
        return new ResponseEntity<>("Stock purchased", HttpStatus.OK);

    }

    public ResponseEntity<String> sellStock(ObjectNode json){
        String orderBuyIn = json.get("orderBuyIn").asText();
        Double orderValue = json.get("orderValue").asDouble();
        String symbol = json.get("symbol").asText();

        User user = userRepository.getUserByID(userID);

        Portfolio portfolio = user.getPortfolio();
        
        UserStock stock =  getStock(portfolio.getStocks(), symbol);

        if(stock.getTicker() == null) {
            return new ResponseEntity<>("stock not owned", HttpStatus.BAD_REQUEST);
        }

        Stock currentStockQuote = stockServices.getStockQuote(symbol);

        Double newQuantity = Double.parseDouble(new DecimalFormat("0.00").format(stock.getQuantity() - getQuantity(orderBuyIn, orderValue, currentStockQuote.getLastPrice())));

        stock.setQuantity(newQuantity);
        stock.setDate(getCurrentTime());

        Double availableBal = portfolio.getAvailableBalance() + (getQuantity(orderBuyIn, orderValue, currentStockQuote.getLastPrice()) * currentStockQuote.getLastPrice());
        Double newAvailableBal = Double.parseDouble(new DecimalFormat("0.00").format(availableBal));

        portfolio.setAvailableBalance(newAvailableBal);

        if(stock.getQuantity() == 0) {
            portfolio.getStocks().remove(stock);
            userRepository.saveAndFlush(user);
            return new ResponseEntity<>("all shares sold", HttpStatus.OK);
        };

        
        userRepository.saveAndFlush(user);

        return new ResponseEntity<>("stock sold", HttpStatus.OK);
    }

    UserStock getStock(List<UserStock> stockList, String symbol) {

        Optional<UserStock> object =  stockList.stream().filter(a -> a.getTicker().equalsIgnoreCase(symbol)).findFirst();

        if(object.isPresent()) return object.get();

        return new UserStock();
    }

    Quotes getStockDate(List<Quotes> stockList, Long date) {
        Optional<Quotes> object =  stockList.stream().filter(a -> a.getDatetime().compareTo(date) == 0).findFirst();

        if(object.isPresent()) return object.get();

        return new Quotes();
    }

    Candles getStockDateCandles(List<Candles> stockList, Long date) {
        Optional<Candles> object =  stockList.stream().filter(a -> a.getDatetime().compareTo(date) >= 0).findFirst();

        if(object.isPresent()) return object.get();

        return null;
    }

    List<Quotes> getStockDay(List<Quotes> stockList, String periodType, Long date) {
        Long dateCreation = stockList.get(0).getDatetime();

        Calendar calendar = Calendar.getInstance();
        Calendar calendarQuotes = Calendar.getInstance();
        Calendar calendarCreation = Calendar.getInstance();
        calendar.setTime(new Date(date));
        calendarCreation.setTime(new Date(dateCreation));

        List<Quotes> filteredQuotes;
        List<Quotes> timeFilteredQuotes;

        filteredQuotes =  stockList.stream().filter(a -> {
            
            return a.getDatetime().compareTo(date) == 0 || a.getDatetime().compareTo(date) == 1;
        } ).collect(Collectors.toList());

        timeFilteredQuotes = filteredQuotes.stream().filter(a -> {

            calendarQuotes.setTime(new Date(a.getDatetime()));

            if(periodType.equals("day")) {
                return true;
            } 
            else if (periodType.equals("week")) {
                return calendarQuotes.get(Calendar.MINUTE) % 10 == 0;
            }
            else if (periodType.equals("month")) {
                // return calendarQuotes.get(Calendar.MINUTE) == 0;
                return (calendarQuotes.get(Calendar.HOUR_OF_DAY) == 12 && calendarQuotes.get(Calendar.MINUTE) == 0);
            }
            
            return (calendarQuotes.get(Calendar.HOUR_OF_DAY) == calendarCreation.get(Calendar.HOUR_OF_DAY) && calendarQuotes.get(Calendar.MINUTE) == calendarCreation.get(Calendar.MINUTE)) || (calendarQuotes.get(Calendar.HOUR_OF_DAY) == 12 && calendarQuotes.get(Calendar.MINUTE) == 0);

        }).collect(Collectors.toList());

        return timeFilteredQuotes;
    }

    Double calculateAveragePrice(Double prevAvgPrice, Double prevQuantity, Double newPrice, Double newQuantity){

         return ((prevAvgPrice * prevQuantity) + (newPrice * newQuantity)) / (prevQuantity + newQuantity);
    }

    Long getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        if(calendar.get(Calendar.DAY_OF_WEEK) == 7){
            calendar.set(Calendar.DAY_OF_WEEK, 6);
        } else if (calendar.get(Calendar.DAY_OF_WEEK) == 1 || calendar.get(Calendar.DAY_OF_WEEK) == 2){
            calendar.add(Calendar.DATE, -7);
            calendar.set(Calendar.DAY_OF_WEEK, 6);
        }

        return calendar.getTimeInMillis();
    }

    String getDateParameter(Long startDate){
        Calendar calendar = Calendar.getInstance();

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        start.setTime(new Date(startDate));

        end.set(Calendar.HOUR_OF_DAY, 17);
        end.set(Calendar.MINUTE, 0);
        end.set(Calendar.SECOND, 0);
        end.set(Calendar.MILLISECOND, 0);

        if(calendar.get(Calendar.HOUR_OF_DAY) >= 6)
            return "&startDate=" + start.getTimeInMillis() + "&endDate=" + end.getTimeInMillis(); //tdameritrade api sends data twice !


        return "&startDate=" + start.getTimeInMillis();
    }

    Double getQuantity(String orderBuyIn, Double orderValue, Double currentQuote) {

        if(orderBuyIn.equals("Shares")) return orderValue = Double.parseDouble(new DecimalFormat("0.00").format(orderValue));


        Double quantity = orderValue / currentQuote;

        return Double.parseDouble(new DecimalFormat("0.000").format(quantity));

    }
}
