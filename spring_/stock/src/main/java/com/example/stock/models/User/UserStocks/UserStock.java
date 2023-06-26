package com.example.stock.models.User.UserStocks;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Model for stocks owned by the user -> saved by portfolio
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserStock {

    @Id
    // @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "stock_seq", sequenceName = "stock_seq", allocationSize = 1)

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stock_seq")
    private Integer stockID;

    String ticker;
    Long date;

    Double quantity;
    Double avgPrice;
}