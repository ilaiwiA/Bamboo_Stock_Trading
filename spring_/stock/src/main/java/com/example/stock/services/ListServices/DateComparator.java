package com.example.stock.services.ListServices;

import java.util.Comparator;

import com.example.stock.models.User.UserStocks.UserStock;

public class DateComparator implements Comparator<UserStock> {

    @Override
    public int compare(UserStock o1, UserStock o2) {
        if (o1.getDate() == o2.getDate()) return 0;

        else if (o1.getDate() < o2.getDate()) return -1;

        else return 1;
    }}
