package com.example.stock.models.Stocks;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StockQuotes {
    @JsonProperty("candles")
    ArrayList<Candles> candles;
}

class Candles {
    @JsonProperty("open")
    Double open;

    @JsonProperty("high")
    Double high;

    @JsonProperty("low")
    Double low;

    @JsonProperty("close")
    Double close;

    @JsonProperty("volume")
    Double volume;

    @JsonProperty("datetime")
    Double datetime;
}
