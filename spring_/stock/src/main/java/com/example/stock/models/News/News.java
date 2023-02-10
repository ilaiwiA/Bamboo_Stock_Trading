package com.example.stock.models.News;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class News {
    
    @JsonProperty("title")
    public String title;

    @JsonProperty("description")
    public String description;

    @JsonProperty("url")
    public String url;

    @JsonProperty("image_url")
    public String image_url;

    @JsonProperty("source")
    public String source;

    @JsonProperty("published_at")
    public String time;

    @JsonIgnore
    public ArrayList<entities> entity;
    
    //GET ENTITIES LIST (SYMBOL)
    @JsonGetter("ticker")
    public String getEntities() {
        return entity.get(0).symbol;
    }

    // SET ENTITIES LIST (SYMBOL)
    @JsonSetter("entities")
    public void setEntities(ArrayList<entities> entities) {
        this.entity = entities;
    }
}

class entities {
    @JsonProperty("symbol")
    public String symbol;
}
