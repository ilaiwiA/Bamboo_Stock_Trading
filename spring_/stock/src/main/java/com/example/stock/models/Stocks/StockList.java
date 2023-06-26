package com.example.stock.models.Stocks;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Controller for stock quotes
 * Fields designed for TDAmeritrade
 */
@Data
@AllArgsConstructor
@NoArgsConstructor

public class StockList {
    private String assetType;
    private String symbol;
    private String description;
    private String lastPrice;
}
