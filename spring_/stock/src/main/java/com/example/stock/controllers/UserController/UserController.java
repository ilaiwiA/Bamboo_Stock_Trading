package com.example.stock.controllers.UserController;

import java.util.ArrayList;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.stock.models.User.User;
import com.example.stock.models.User.Portfolio.Portfolio;
import com.example.stock.models.User.UserStocks.UserStock;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    public User createUser() {
        ArrayList<String> watchList = new ArrayList<>();
        

        ArrayList<UserStock> stocks = new ArrayList<>();

        UserStock userStockA = new UserStock("AMD", "3/15/2023", 100.00, 85.23);
        UserStock userStockB = new UserStock("TSLA", "3/15/2023", 1000.00, 192.73);
        UserStock userStockC = new UserStock("AAPL", "3/15/2023", 10.00, 135.75);
        UserStock userStockD = new UserStock("NIO", "3/15/2023", 50.00, 15.25);
        UserStock userStockE = new UserStock("SPY", "3/15/2023", 25.00, 390.52);

        stocks.add(userStockA);
        stocks.add(userStockB);
        stocks.add(userStockC);
        stocks.add(userStockD);
        stocks.add(userStockE);

        watchList.add("AMD");
        watchList.add("TSLA");
        watchList.add("AAPL");
        watchList.add("GME");
        watchList.add("GME");

        Portfolio portfolio = Portfolio.builder().stocks(stocks).watchList(watchList).build();
        
        User user = User.builder().firstName("Ahmed").lastName("ilawi").userName("ilaiwiA").portfolio(portfolio).build();

        return user;
    }

    // @PathVariable("id") int ID
    @CrossOrigin
    @GetMapping("/stocklist")
    public ArrayList<String> getStocklist() {
        return createUser().getPortfolio().getUserStocks();
    }

    @CrossOrigin
    @GetMapping("/watchlist")
    public ArrayList<String> getWatchlist() {
        return createUser().getPortfolio().getWatchList();
    }

}
