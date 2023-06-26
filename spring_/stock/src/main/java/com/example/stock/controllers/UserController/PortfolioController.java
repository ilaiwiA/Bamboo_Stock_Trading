package com.example.stock.controllers.UserController;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.stock.models.User.User;
import com.example.stock.models.User.Portfolio.Portfolio;
import com.example.stock.repository.UserRepository;
import com.example.stock.services.UserSecurityService;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.AllArgsConstructor;

/*
 * Controller portfolio update watchlist
 */
@RestController
@RequestMapping("/api/user/portfolio")
@AllArgsConstructor
public class PortfolioController {

    private final UserRepository userRepository;

    private final UserSecurityService userSecurityService;

    // Watchlist endpoint, called by client to update a users endpoint -> receives
    // ticker as a request
    @PostMapping("/watchlist")
    public void updateWatchlist(@RequestBody ObjectNode json) {
        String ticker = json.get("ticker").asText();

        // Get current logged in user
        User user = userRepository.getUserByID(userSecurityService.getCurrentUserID());

        // Get current user portfolio
        Portfolio portfolio = user.getPortfolio();

        // If watchlist is empty -> create watchlist and populate it with current ticker
        List<String> userWatchList = portfolio.getWatchList();
        if (userWatchList == null) {
            List<String> watchList = new ArrayList<>();
            watchList.add(ticker);
            portfolio.setWatchList(watchList);
            userRepository.save(user);
            return;
        }
        boolean update = userWatchList.remove(ticker); // Removes ticker from watchlist -> if successful ticker was
                                                       // already watchlisted so we remove it

        if (!update) // if remove returns false, ticker was not in watchlist so we add it
            userWatchList.add(ticker);

        portfolio.setWatchList(userWatchList); // update portfolio

        userRepository.save(user); // save to database
    }
}
