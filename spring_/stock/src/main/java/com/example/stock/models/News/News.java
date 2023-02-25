package com.example.stock.models.News;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class News {
    
    @JsonProperty("title")
    public String title;

    @JsonProperty("url")
    public String url;

    @JsonProperty("time_published")
    public String time;

    @JsonProperty("summary")
    public String description;

    @JsonProperty("banner_image")
    public String image_url;

    @JsonProperty("source")
    public String source;

    @JsonIgnore
    public ArrayList<Ticker> tickerList;

    public String symbol;

    @JsonSetter("ticker_sentiment")
    public void setTickers(ArrayList<Ticker> tickerList){
        this.tickerList = tickerList;
    }

    @JsonGetter("symbol")
    public String getTicker(){
        if(tickerList.size() > 0) return tickerList.get(0).ticker;
        return "";
    }

}

class Ticker {
    @JsonProperty("ticker")
    public String ticker;
}
