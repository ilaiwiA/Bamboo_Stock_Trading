package com.example.stock.services;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

    final long UNIX_MS_PER_MIN = 60000L;
    final long UNIX_MS_PER_5MIN = 300000L;
    final long UNIX_MS_PER_DAY = 86400000L;

    @Autowired
    UserRepository userRepository;

    @Autowired
    StockServices stockServices;

    @Autowired
    UserSecurityService userSecurityService;

    public List<Quotes> generatePortfolio(String periodType) {
        User user = userRepository.getUserByID(userSecurityService.getCurrentUserID());

        Portfolio portfolio = user.getPortfolio();

        List<Quotes> portfolioQuotes = portfolio.getPortfolioQuotes();
        List<UserStock> stocks = portfolio.getStocks();

        if (portfolioQuotes.size() == 0)
            createPortfolio(user, portfolioQuotes);

        if (stocks.size() > 0)
            updatePortfolio(user, portfolioQuotes, stocks);
        else
            fillPortfolio(portfolioQuotes);

        // return portfolioQuotes;

        return getPortfolioQuotesByDate(portfolioQuotes, periodType);
    }

    List<Quotes> createPortfolio(User user, List<Quotes> portfolioQuotes) {
        Quotes quotes = Quotes.builder().datetime(user.getCreationDate()).close(user.getPortfolio().getTotalBalance())
                .build();
        portfolioQuotes.add(quotes);

        user.getPortfolio().setPortfolioQuotes(portfolioQuotes);

        userRepository.saveAndFlush(user);
        return portfolioQuotes;
    }

    List<Quotes> updatePortfolio(User user, List<Quotes> portfolioQuotes, List<UserStock> stocks) {
        Quotes lastQuote = portfolioQuotes.get(portfolioQuotes.size() - 1);
        Long lastDate = lastQuote.getDatetime();

        Collections.sort(stocks, new DateComparator());

        long diff;
        if (lastDate < stocks.get(0).getDate()) {
            long msDiff = Math.abs(stocks.get(0).getDate() - lastDate);
            diff = TimeUnit.DAYS.convert(msDiff, TimeUnit.MILLISECONDS);
        } else
            diff = 0;

        Long newDate = lastDate + UNIX_MS_PER_DAY;
        for (int i = 1; i < diff; i++) {
            Quotes quotes = Quotes.builder().datetime(newDate).close(lastQuote.getClose()).build();
            portfolioQuotes.add(quotes);
            newDate += UNIX_MS_PER_DAY;
        }

        for (int i = 0; i < stocks.size(); i++) {
            System.out.println();
            System.out.println();

            Double prevValue = 0D;

            Long recentDate = stocks.get(i).getDate();
            if (lastDate > recentDate) {
                recentDate = lastDate;
            }

            ArrayList<Candles> stockList = stockServices.getHistoricalStockQuotes(stocks.get(i).getTicker(), "",
                    getDateParameter(getLatestMarketTime(recentDate)));

            if (stockList == null)
                continue;

            int index;

            try {
                index = portfolioQuotes
                        .indexOf(getStockDate(portfolioQuotes,
                                getStockDateCandles(stockList, recentDate).getDatetime()));

                if (i == 0) {
                    portfolioQuotes.remove(index);
                    index = -1;
                }

            } catch (Exception e) {
                index = -1;
            }

            System.out.println("RECENT DATE: " + recentDate + "  lastDate: " + lastDate);
            int stockListIndex = stockList.indexOf(getStockDateCandles(stockList,
                    recentDate));

            if (stockListIndex == -1)
                continue;

            if (lastDate == recentDate) {
                prevValue = (stockList.get(stockListIndex - 1).getClose() - stocks.get(i).getAvgPrice())
                        * stocks.get(i).getQuantity();

            }

            if (index == -1) {
                Double total = 0D;

                for (int x = 0; x < stockList.size(); x++) {

                    if (stockList.get(x).getDatetime() < stocks.get(i).getDate()) {
                        Quotes quotes = Quotes.builder().datetime(stockList.get(x).getDatetime())
                                .close(lastQuote.getClose()).build();
                        portfolioQuotes.add(quotes);
                        continue;
                    }
                    ;

                    if (stockList.get(x).getDatetime() < lastDate || stockList.get(x).getDatetime() < portfolioQuotes
                            .get(portfolioQuotes.size() - 1).getDatetime()) {
                        continue; // double check this
                    }

                    Double newQuote = (stockList.get(x).getClose() - stocks.get(i).getAvgPrice())
                            * stocks.get(i).getQuantity();

                    Double newValue = newQuote - prevValue;
                    Double oldBalance = portfolioQuotes.get(portfolioQuotes.size() - 1).getClose();

                    total += newValue;

                    prevValue = newQuote;

                    Quotes quotes = Quotes.builder().datetime(stockList.get(x).getDatetime())
                            .close(newValue + oldBalance).build();
                    portfolioQuotes.add(quotes);

                }
            } else {
                int indexPointer = index;
                Double total = 0D;
                Double oldBalance = portfolioQuotes.get(indexPointer).getClose();

                for (int x = stockListIndex; x < stockList.size(); x++) {

                    if (indexPointer > portfolioQuotes.size() - 1) {
                        oldBalance = portfolioQuotes.get(indexPointer - 1).getClose();
                        Quotes quotes = Quotes.builder().datetime(stockList.get(x).getDatetime()).close(oldBalance)
                                .build();

                        portfolioQuotes.add(quotes);
                    }

                    if (stockList.get(x).getDatetime().compareTo(portfolioQuotes.get(indexPointer).getDatetime()) < 0) {
                        continue;
                    }

                    if (stockList.get(x).getDatetime().compareTo(portfolioQuotes.get(indexPointer).getDatetime()) > 0) {
                        portfolioQuotes.get(indexPointer).setClose(portfolioQuotes.get(indexPointer - 1).getClose());

                        x--;
                    }

                    if (stockList.get(x).getDatetime()
                            .compareTo(portfolioQuotes.get(indexPointer).getDatetime()) == 0) {

                        Double newQuote = (stockList.get(x).getClose() - stocks.get(i).getAvgPrice())
                                * stocks.get(i).getQuantity();
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

    List<Quotes> fillPortfolio(List<Quotes> portfolioQuotes) {
        Quotes lastQuote = portfolioQuotes.get(portfolioQuotes.size() - 1);
        Long lastDate = lastQuote.getDatetime();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(lastDate));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        lastDate = calendar.getTimeInMillis();

        long currTime = getCurrentTime();

        int min = calendar.get(Calendar.MINUTE);
        int multipleFive = (int) (Math.ceil((double) min / 5) * 5);
        if (min != multipleFive) {
            calendar.set(Calendar.MINUTE, multipleFive);
            lastDate = calendar.getTimeInMillis();
            Quotes quotes = Quotes.builder().datetime(lastDate).close(lastQuote.getClose()).build();
            portfolioQuotes.add(quotes);
        }

        long diff;

        if (lastDate < currTime) {
            long msDiff = Math.abs(currTime - lastDate);
            diff = TimeUnit.MINUTES.convert(msDiff, TimeUnit.MILLISECONDS);
        } else
            diff = 0;

        Long newDate = lastDate + UNIX_MS_PER_5MIN;
        diff /= 5;

        for (int i = 1; i <= diff; i++) {
            Quotes quotes = Quotes.builder().datetime(newDate).close(lastQuote.getClose()).build();
            portfolioQuotes.add(quotes);
            newDate += UNIX_MS_PER_5MIN;
        }

        return portfolioQuotes;
    }

    List<Quotes> getPortfolioQuotesByDate(List<Quotes> portfolioQuotes, String periodType) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(new Date(portfolioQuotes.get(portfolioQuotes.size() - 1).getDatetime()));
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (periodType.equals("day")) {
            int index = portfolioQuotes.indexOf(getStockDate(portfolioQuotes, calendar.getTimeInMillis()));
            if (index == -1) {

                portfolioQuotes.get(0).setPrevious(portfolioQuotes.get(0).getClose());

                return portfolioQuotes;
            }

            calendar.set(Calendar.HOUR_OF_DAY, 18);
            calendar.set(Calendar.MINUTE, 55);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            // if Tues - Sat get previous date (-1) if Sun (-2) if Mon (-3)
            switch (calendar.get(Calendar.DAY_OF_WEEK)) {
                case 2:
                    calendar.add(Calendar.DATE, -3);
                    break;
                case 1:
                    calendar.add(Calendar.DATE, -2);
                    break;

                default:
                    calendar.add(Calendar.DATE, -1);
                    break;
            }

            int prevQuoteIndex = portfolioQuotes.indexOf(getLastStockDate(portfolioQuotes, calendar.getTimeInMillis()));

            if (prevQuoteIndex != -1) {
                Double prevQuote = portfolioQuotes.get(prevQuoteIndex).getClose();
                portfolioQuotes.get(index).setPrevious(prevQuote);
            } else
                portfolioQuotes.get(index).setPrevious(portfolioQuotes.get(index).getClose());

            return portfolioQuotes.subList(index, portfolioQuotes.size());
        }

        if (periodType.equals("week")) {
            calendar.add(Calendar.DATE, -7);
        }

        if (periodType.equals("month")) {
            calendar.add(Calendar.MONTH, -1);
        }

        if (periodType.equals("3month")) {
            calendar.add(Calendar.MONTH, -3);

        }

        if (periodType.equals("ytd")) {
            calendar.set(Calendar.MONTH, 0);
            calendar.set(Calendar.DAY_OF_MONTH, 1);

        }

        if (periodType.equals("all")) {
            calendar.setTime(new Date(portfolioQuotes.get(0).getDatetime()));
        }

        List<Quotes> portfolio = getStockDay(portfolioQuotes, periodType, calendar.getTimeInMillis());

        if (portfolio.size() == 0) {
            return new ArrayList<Quotes>(Arrays.asList(portfolioQuotes.get(0)));
        }

        return portfolio;

    }

    public ResponseEntity<String> purchaseStock(ObjectNode json) {
        String orderBuyIn = json.get("orderBuyIn").asText();
        Double orderValue = json.get("orderValue").asDouble();
        String symbol = json.get("symbol").asText();

        // load user
        User user = userRepository.getUserByID(userSecurityService.getCurrentUserID());

        Portfolio portfolio = user.getPortfolio();

        UserStock stock = getStock(portfolio.getStocks(), symbol);

        if (stock.getTicker() == null) {
            stock.setTicker(symbol);
            stock.setAvgPrice(0.00);
            stock.setQuantity(0.00);
            portfolio.getStocks().add(stock);
        }

        Stock currentStockQuote = stockServices.getStockQuote(symbol);

        Double newQuantity = Double.parseDouble(new DecimalFormat("0.00")
                .format(stock.getQuantity() + getQuantity(orderBuyIn, orderValue, currentStockQuote.getLastPrice())));

        stock.setAvgPrice(
                calculateAveragePrice(stock.getAvgPrice(), stock.getQuantity(), currentStockQuote.getLastPrice(),
                        getQuantity(orderBuyIn, orderValue, currentStockQuote.getLastPrice())));
        stock.setQuantity(newQuantity);
        // stock.setDate(getLatestMarketTime(new Date().getTime()));

        ///////////// EXPIERMIENTAL
        stock.setDate(new Date().getTime());

        ///

        Double availableBal = portfolio.getAvailableBalance()
                - (getQuantity(orderBuyIn, orderValue, currentStockQuote.getLastPrice())
                        * currentStockQuote.getLastPrice());

        Double newAvailableBal = Double.parseDouble(new DecimalFormat("0.00").format(availableBal));

        portfolio.setAvailableBalance(newAvailableBal);

        userRepository.saveAndFlush(user);
        return new ResponseEntity<>("Stock purchased", HttpStatus.OK);

    }

    public ResponseEntity<String> sellStock(ObjectNode json) {
        String orderBuyIn = json.get("orderBuyIn").asText();
        Double orderValue = json.get("orderValue").asDouble();
        String symbol = json.get("symbol").asText();

        User user = userRepository.getUserByID(userSecurityService.getCurrentUserID());

        Portfolio portfolio = user.getPortfolio();

        UserStock stock = getStock(portfolio.getStocks(), symbol);

        if (stock.getTicker() == null) {
            return new ResponseEntity<>("stock not owned", HttpStatus.BAD_REQUEST);
        }

        Stock currentStockQuote = stockServices.getStockQuote(symbol);

        Double newQuantity = Double.parseDouble(new DecimalFormat("0.00")
                .format(stock.getQuantity() - getQuantity(orderBuyIn, orderValue, currentStockQuote.getLastPrice())));

        stock.setQuantity(newQuantity);

        Double availableBal = portfolio.getAvailableBalance()
                + (getQuantity(orderBuyIn, orderValue, currentStockQuote.getLastPrice())
                        * currentStockQuote.getLastPrice());
        Double newAvailableBal = Double.parseDouble(new DecimalFormat("0.00").format(availableBal));

        portfolio.setAvailableBalance(newAvailableBal);

        if (stock.getQuantity() == 0) {
            portfolio.getStocks().remove(stock);
            userRepository.saveAndFlush(user);
            return new ResponseEntity<>("all shares sold", HttpStatus.OK);
        }
        ;

        userRepository.saveAndFlush(user);

        return new ResponseEntity<>("stock sold", HttpStatus.OK);
    }

    UserStock getStock(List<UserStock> stockList, String symbol) {

        Optional<UserStock> object = stockList.stream().filter(a -> a.getTicker().equalsIgnoreCase(symbol)).findFirst();

        if (object.isPresent())
            return object.get();

        return new UserStock();
    }

    Quotes getStockDate(List<Quotes> stockList, Long date) {
        Optional<Quotes> object = stockList.stream().filter(a -> a.getDatetime().compareTo(date) == 0).findFirst();

        if (object.isPresent())
            return object.get();

        return new Quotes();
    }

    Quotes getLastStockDate(List<Quotes> stockList, Long date) {
        Quotes quotes = new Quotes();
        Optional<Quotes> object = stockList.stream().filter(a -> {
            if (a.getDatetime().compareTo(date) < 0) {

                quotes.setClose(a.getClose());
                quotes.setDatetime(a.getDatetime());

            }
            return a.getDatetime().compareTo(date) == 0;
        }).findFirst();

        if (object.isPresent())
            return object.get();

        return quotes;
    }

    Candles getStockDateCandles(List<Candles> stockList, Long date) {
        Candles candles = new Candles();
        Calendar calendar = Calendar.getInstance();
        Optional<Candles> object = stockList.stream().filter(a -> {

            calendar.setTime(new Date(a.getDatetime()));

            if (a.getDatetime().compareTo(date) <= 0) {
                candles.setClose(a.getClose());
                candles.setDatetime(a.getDatetime());
                candles.setHigh(a.getHigh());
                candles.setLow(a.getLow());
                candles.setOpen(a.getOpen());
                candles.setVolume(a.getVolume());
            }

            // todo return next date where hour 19 is max
            return a.getDatetime().compareTo(date) >= 0;
        }).findFirst();

        if (object.isPresent())
            return object.get();

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

        filteredQuotes = stockList.stream()
                .filter(a -> a.getDatetime().compareTo(date) == 0 || a.getDatetime().compareTo(date) == 1)
                .collect(Collectors.toList());

        timeFilteredQuotes = filteredQuotes.stream().filter(a -> {

            calendarQuotes.setTime(new Date(a.getDatetime()));

            if (periodType.equals("day")) {
                return true;
            } else if (periodType.equals("week")) {
                return calendarQuotes.get(Calendar.MINUTE) % 10 == 0;
            } else if (periodType.equals("month")) {
                return (calendarQuotes.get(Calendar.HOUR_OF_DAY) == 12 && calendarQuotes.get(Calendar.MINUTE) == 0);
            }

            return (calendarQuotes.get(Calendar.HOUR_OF_DAY) == calendarCreation.get(Calendar.HOUR_OF_DAY)
                    && calendarQuotes.get(Calendar.MINUTE) == calendarCreation.get(Calendar.MINUTE))
                    || (calendarQuotes.get(Calendar.HOUR_OF_DAY) == 12 && calendarQuotes.get(Calendar.MINUTE) == 0);

        }).collect(Collectors.toList());

        return timeFilteredQuotes;
    }

    Double calculateAveragePrice(Double prevAvgPrice, Double prevQuantity, Double newPrice, Double newQuantity) {

        return ((prevAvgPrice * prevQuantity) + (newPrice * newQuantity)) / (prevQuantity + newQuantity);
    }

    Long getCurrentTimeStart() {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 6);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    Long getLatestMarketTime(Long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);

        if (calendar.get(Calendar.DAY_OF_WEEK) == 7) {
            calendar.set(Calendar.DAY_OF_WEEK, 6);
        } else if (calendar.get(Calendar.DAY_OF_WEEK) == 1 || calendar.get(Calendar.DAY_OF_WEEK) == 2) {
            calendar.add(Calendar.DATE, -7);
            calendar.set(Calendar.DAY_OF_WEEK, 6);
        }

        return calendar.getTimeInMillis();
    }

    Long getCurrentTime() {
        Calendar currentTime = Calendar.getInstance();

        Calendar minMarketTime = Calendar.getInstance();
        minMarketTime.set(Calendar.HOUR_OF_DAY, 6);
        minMarketTime.set(Calendar.MINUTE, 0);
        minMarketTime.set(Calendar.SECOND, 0);
        minMarketTime.set(Calendar.MILLISECOND, 0);

        Calendar maxMarketTime = Calendar.getInstance();
        maxMarketTime.set(Calendar.HOUR_OF_DAY, 18);
        maxMarketTime.set(Calendar.MINUTE, 55);
        maxMarketTime.set(Calendar.SECOND, 0);
        maxMarketTime.set(Calendar.MILLISECOND, 0);

        if (currentTime.getTimeInMillis() < minMarketTime.getTimeInMillis()) {
            maxMarketTime.add(Calendar.DATE, -1);
            return maxMarketTime.getTimeInMillis();
        } else if (currentTime.getTimeInMillis() > minMarketTime.getTimeInMillis()
                && currentTime.getTimeInMillis() > maxMarketTime.getTimeInMillis()) {
            return maxMarketTime.getTimeInMillis();
        }

        return currentTime.getTimeInMillis();

    }

    String getDateParameter(Long startDate) {
        Calendar calendar = Calendar.getInstance();

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        start.setTime(new Date(startDate));

        end.set(Calendar.HOUR_OF_DAY, 17);
        end.set(Calendar.MINUTE, 0);
        end.set(Calendar.SECOND, 0);
        end.set(Calendar.MILLISECOND, 0);

        if (calendar.get(Calendar.HOUR_OF_DAY) >= 6)
            return "&startDate=" + start.getTimeInMillis() + "&endDate=" + end.getTimeInMillis(); // tdameritrade api
                                                                                                  // sends data twice !

        return "&startDate=" + start.getTimeInMillis();
    }

    Double getQuantity(String orderBuyIn, Double orderValue, Double currentQuote) {

        if (orderBuyIn.equals("Shares"))
            return orderValue = Double.parseDouble(new DecimalFormat("0.00").format(orderValue));

        Double quantity = orderValue / currentQuote;

        return Double.parseDouble(new DecimalFormat("0.000").format(quantity));

    }
}