package com.example.stock.tests;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class NewsDataTest {
    
    @JsonProperty("data")
    public ArrayList<NewsTest> data;
}
