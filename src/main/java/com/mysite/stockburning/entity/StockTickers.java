package com.mysite.stockburning.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class StockTickers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String stockSymbol;

}
/*
CREATE TABLE StockInfo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    stock_symbol VARCHAR(10) UNIQUE,
    volume FLOAT,
    avg_volume FLOAT,
    rsi FLOAT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
 */