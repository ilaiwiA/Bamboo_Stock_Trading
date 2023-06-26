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
 * Quotes model used by Portfolio
 * Portfolio uses quotes to store user portfolio value
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Quotes {

    @Id
    @SequenceGenerator(name = "quote_seq", sequenceName = "quote_seq", allocationSize = 1)

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "quote_seq")

    private Integer stockID;

    Double close;
    Long datetime;

    // "previous" field used to by first time in a day to store valuation from the
    // previous day -> used by client to render horizontal line
    Double previous;
}
