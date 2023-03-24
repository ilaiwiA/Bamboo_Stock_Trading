package com.example.stock.models.User.UserStocks;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserStock {
    String ticker;
    String date;

    Double quantity;
    Double avgPrice;
}
