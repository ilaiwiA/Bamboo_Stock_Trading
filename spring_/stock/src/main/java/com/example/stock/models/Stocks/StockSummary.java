package com.example.stock.models.Stocks;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_EMPTY)
public class StockSummary {

    @JsonProperty("Symbol")
    private String Symbol;
        
    @JsonProperty("AssetType")
    private String AssetType;
    
    @JsonProperty("Name")
    private String Name;

    @JsonProperty("Description")
    private String Description;
    
    @JsonProperty("Exchange")
    private String Exchange;
    
    @JsonProperty("Currency")
    private String Currency;
    
    @JsonProperty("Country")
    private String Country;
    
    @JsonProperty("Sector")
    private String Sector;
    
    @JsonProperty("Industry")
    private String Industry;
    
    @JsonProperty("Address")
    private String Address;
    
    @JsonProperty("MarketCapitalization")
    private String MarketCapitalization;
    
    @JsonProperty("PERatio")
    private String PERatio;
    
    @JsonProperty("DividendYield")
    private String DividendYield;
    
    @JsonProperty("EPS")
    private String EPS;
    
    @JsonProperty("TrailingPE")
    private String TrailingPE;

    @JsonProperty("ForwardPE")
    private String ForwardPE;

    @JsonProperty("52WeekHigh")
    private String _52WeekHigh;

    @JsonProperty("52WeekLow")
    private String _52WeekLow;

    @JsonProperty("50DayMovingAverage")
    private String _50DayMovingAverage;

    @JsonProperty("200DayMovingAverage")
    private String _200DayMovingAverage;

    @JsonProperty("SharesOutstanding")
    private String SharesOutstanding;

    @JsonProperty("DividendDate")
    private String DividendDate;    

    @JsonProperty("ExDividendDate")
    private String ExDividendDate;



}
