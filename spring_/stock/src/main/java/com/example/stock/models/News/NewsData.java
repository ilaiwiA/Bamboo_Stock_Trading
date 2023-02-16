package com.example.stock.models.News;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NewsData {
    @JsonProperty("feed")
    public ArrayList<News> feed;
}
