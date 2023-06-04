package com.example.stock.controllers.UserController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.stock.models.User.User;
import com.example.stock.models.User.UserStocks.Quotes;
import com.example.stock.repository.UserRepository;
import com.example.stock.services.UserStockServices;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserStockServices userStockServices;

    //hardcoded for now
    int userID = 4;

    public void createUser() {
    }

    @CrossOrigin
    @GetMapping()
    public User getUser() {

        return userRepository.getUserByID(userID);
    }

    @CrossOrigin
    @PostMapping("/purchase")
    public ResponseEntity<String> postPurchaseStock(@RequestBody ObjectNode json) {
        return userStockServices.purchaseStock(json);
    }

    @CrossOrigin
    @PostMapping("/sell")
    public ResponseEntity<String> postSellStock(@RequestBody ObjectNode json) {
        return userStockServices.sellStock(json);
    }

    // @PathVariable("id") int ID
    @CrossOrigin
    @GetMapping("/stocklist")
    public List<String> getStocklist() {

        return userRepository.getUserByID(userID).getPortfolio().getUserStocks();
    }

    @CrossOrigin
    @GetMapping("/watchlist")
    public List<String> getWatchlist() {

        return userRepository.getUserByID(userID).getPortfolio().getWatchList();
    }

    @CrossOrigin
    @GetMapping("/portfolio/{periodType}")
    public List<Quotes> getPortfolioQuotes(@PathVariable("periodType") String periodType){
        return userStockServices.generatePortfolio(periodType);
    }

}
