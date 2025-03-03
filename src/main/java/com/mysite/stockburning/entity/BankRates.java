package com.mysite.stockburning.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
public class BankRates extends  BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String country;

    @Column(nullable = false, unique = true)
    private String bank;

    @Column(nullable = true)
    private String rate;

    @Column(nullable = true)
    private String nextMeeting;


    public BankRates(String country, String bankTicker, String currentRate, String nextMeeting) {
    }
}
