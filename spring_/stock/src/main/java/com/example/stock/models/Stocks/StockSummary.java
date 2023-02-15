package com.example.stock.models.Stocks;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class StockSummary {
    private String Symbol;
    private String AssetType;
    private String Name;
    private String Description;
    private String CIK;
    private String Exchange;
    private String Currency;
    private String Country;
    private String Sector;
    private String Industry;
    private String Address;
    private String FiscalYearEnd;
    private String LatestQuarter;
    private String MarketCapitalization;
    private String EBITDA;
    private String PERatio;
    private String PEGRatio;
    private String BookValue;
    private String DividendPerShare;
    private String DividendYield;
    private String EPS;
    private String RevenuePerShareTTM;
    private String ProfitMargin;
    private String OperatingMarginTTM;
    private String ReturnOnAssetsTTM;
    private String ReturnOnEquityTTM;
    private String RevenueTTM;
    private String GrossProfitTTM;
    private String DilutedEPSTTM;
    private String QuarterlyEarningsGrowthYOY;
    private String QuarterlyRevenueGrowthYOY;
    private String TrailingPE;
    private String ForwardPE;
    private String PriceToSalesRatioTTM;
    private String PriceToBookRatio;

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
