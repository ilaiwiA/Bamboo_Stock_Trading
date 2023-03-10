package com.example.stock.models.Stocks.StockQuotes;

import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class StockQuotes {

    @JsonProperty("candles")
    ArrayList<Candles> candles;

    @JsonProperty("Time Series (1min)")
    HashMap<String, Quotes> quotes;
}

class Quotes {
    @JsonSetter("1. open")
    String open;

    @JsonSetter("2. high")
    String high;

    @JsonSetter("3. low")
    String low;

    @JsonSetter("4. close")
    String close;

    @JsonSetter("5. volume")
    String volume;

    @JsonGetter("open")
    public String getOpen() {
        return open;
    }
    @JsonGetter("high")
    public String getHigh() {
        return high;
    }

    @JsonGetter("low")
    public String getLow() {
        return low;
    }

    @JsonGetter("close")
    public String getClose() {
        return close;
    }

    @JsonGetter("volume")
    public String getVolume() {
        return volume;
    }
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
