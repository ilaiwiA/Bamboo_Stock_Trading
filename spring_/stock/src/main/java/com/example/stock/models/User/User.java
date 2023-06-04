package com.example.stock.models.User;

import com.example.stock.models.User.Portfolio.Portfolio;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_users") /*
    uniqueConstrains = @uniqueConstrains(
        name = "firstName_constraint",
        columnNames = "firstName"
    )
*/ 
@Cacheable(false)
public class User {

    @Id
    @SequenceGenerator(
        name = "user_seq",
        sequenceName = "user_seq",
        allocationSize = 1
    )

    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "user_seq"
    )

    @Column(
        name = "user_id"
    )
    @JsonIgnore
    private Integer userID;

    String firstName;
    String lastName;
    String userName;

    Long creationDate;

    @OneToOne(
        cascade = CascadeType.ALL,
        fetch = FetchType.EAGER,
        optional = false
    )
    @JoinColumn(
        name = "portfolio_id",
        referencedColumnName = "portfolioID"
    )
    Portfolio portfolio;
}
