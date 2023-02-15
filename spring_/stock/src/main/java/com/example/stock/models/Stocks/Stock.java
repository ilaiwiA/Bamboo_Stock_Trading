package com.example.stock.models.Stocks;

import com.fasterxml.jackson.annotation.JsonProperty;

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
    private Double openPrice;
    private Double highPrice;
    private Double lowPrice;
    private Double closePrice;
    private Double netChange;
    private Double totalVolume;

    @JsonProperty("52WkHigh")
    private Double WkHigh;

    @JsonProperty("52WkLow")
    private Double WkLow;
    private Double peRatio;
    private Double divAmount;
    private Double netPercentChangeInDouble;
}
