package com.example.stock.models.News;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

// Data model for News Model
public class NewsData {
    @JsonProperty("feed")
    public ArrayList<News> feed;
}
