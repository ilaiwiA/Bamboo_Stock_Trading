package com.example.stock.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Stock {
    private String assetType;
    private String symbol;
    private String description;
    private String lastPrice;
}
