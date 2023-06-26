package com.example.stock.controllers.UserController;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.stock.models.User.User;
import com.example.stock.models.User.UserStocks.Quotes;
import com.example.stock.repository.UserRepository;
import com.example.stock.services.UserSecurityService;
import com.example.stock.services.UserStockServices;
import com.fasterxml.jackson.databind.node.ObjectNode;

/*
 * Controller to handle client user services
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserStockServices userStockServices;

    @Autowired
    UserSecurityService userSecurityService;

    // Retrieve current user from security context
    @GetMapping()
    public User getUser(Principal principal) {
        return userRepository.getUserByID(userSecurityService.getCurrentUserID());
    }

    // Retrieve ticker from client and purchase stock using service
    @PostMapping("/purchase")
    public ResponseEntity<String> postPurchaseStock(@RequestBody ObjectNode json) {
        return userStockServices.purchaseStock(json);
    }

    // Retrieve ticker from client and sell stock using service
    @PostMapping("/sell")
    public ResponseEntity<String> postSellStock(@RequestBody ObjectNode json) {
        return userStockServices.sellStock(json);
    }

    // Return owned stocks from currently authenticated user
    @GetMapping("/stocklist")
    public List<String> getStocklist() {

        return userRepository.getUserByID(userSecurityService.getCurrentUserID()).getPortfolio().getUserStocks();
    }

    // Return watchlist from currently authenticated user
    @GetMapping("/watchlist")
    public ResponseEntity<List<String>> getWatchlist() {
        List<String> watchList = userRepository.getUserByID(userSecurityService.getCurrentUserID()).getPortfolio()
                .getWatchList();

        if (watchList == null || watchList.size() < 1) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(watchList);
    }

    // Retrieve portfolio based on periodType -> default is day from client
    @GetMapping("/portfolio/{periodType}")
    public List<Quotes> getPortfolioQuotes(@PathVariable("periodType") String periodType) {
        return userStockServices.generatePortfolio(periodType);
    }

}
