package com.mysite.stockburning.repository;

import com.mysite.stockburning.entity.StockOpinionStats;
import com.mysite.stockburning.entity.StockTickers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface StockOpinionRepository extends JpaRepository<StockOpinionStats, Integer> {
    StockOpinionStats findByStockTickersAndCreatedAt(StockTickers stockTickers, LocalDate createdAt);
}
