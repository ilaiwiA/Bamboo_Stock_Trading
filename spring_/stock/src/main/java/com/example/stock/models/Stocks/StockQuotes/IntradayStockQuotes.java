package com.example.stock.models.Stocks.StockQuotes;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/*
 * Controller for intraday live stock quotes
 * Fields designed for TDAmeritrade
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class IntradayStockQuotes {
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

    // tiingo api
    @JsonSetter("date")
    String date;

    /// USE WITH TIINGO API IF NEEDED
    @JsonGetter("datetime")
    Long getDate() {
        return Instant.parse(date).toEpochMilli();
    }
}
