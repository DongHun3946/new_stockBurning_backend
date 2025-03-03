package com.mysite.stockburning.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class StockOpinionStats{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "stockTickers_id", nullable = false) //name은 왜래키 이름 설정
    private StockTickers stockTickers;

    @Column
    private int bullishCnt;

    @Column
    private int bearishCnt;

    @Column
    private int postCount;

    //CreatedDate
    @Column(nullable = false)
    private LocalDate createdAt;

    public static StockOpinionStats of(StockTickers stockTickers, int bullishCnt, int bearishCnt, int postCount){
        return StockOpinionStats.builder()
                .stockTickers(stockTickers)
                .bullishCnt(bullishCnt)
                .bearishCnt(bearishCnt)
                .postCount(postCount)
                .build();
    }
}

/*
CREATE TABLE StockOpinionStats (
    id INT AUTO_INCREMENT PRIMARY KEY,               -- 고유 ID
    stock_symbol VARCHAR(10) NOT NULL,               -- 주식 티커
    bullish_count INT DEFAULT 0,                     -- 상승 의견 게시글 수
    bearish_count INT DEFAULT 0,                     -- 하락 의견 게시글 수
    date DATE NOT NULL,                              -- 집계 날짜 (1일 단위)
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 마지막 갱신일
    UNIQUE(stock_symbol, date)                      -- 주식 티커와 날짜별로 유일한 값
);
*/