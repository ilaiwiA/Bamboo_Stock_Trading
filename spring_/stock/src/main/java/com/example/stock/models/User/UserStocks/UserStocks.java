package com.example.stock.models.User.UserStocks;

import lombok.Data;

@Data
public class UserStocks {
    String ticker;
    String date;

    Double quantity;
    Double avgPrice;
}
