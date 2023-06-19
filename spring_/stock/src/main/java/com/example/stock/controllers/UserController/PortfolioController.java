package com.example.stock.controllers.UserController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@RequestMapping("/api/user/portfolio")
@AllArgsConstructor
public class PortfolioController {

    private final UserRepository userRepository;

    private final UserSecurityService userSecurityService;

    @PostMapping("/watchlist")
    public void updateWatchlist(@RequestBody ObjectNode json) {
        String ticker = json.get("ticker").asText();

        User user = userRepository.getUserByID(userSecurityService.getCurrentUserID());

        Portfolio portfolio = user.getPortfolio();

        List<String> userWatchList = portfolio.getWatchList();

        boolean update = userWatchList.remove(ticker);

        if (!update)
            userWatchList.add(ticker);

        portfolio.setWatchList(userWatchList);

        userRepository.save(user);
    }
}
