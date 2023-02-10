package com.example.stock.models.News;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class NewsData {
    
    @JsonProperty("data")
    public ArrayList<News> data;
}
